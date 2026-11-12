package com.youplayertube.app

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.youplayertube.app.data.*
import com.youplayertube.app.data.local.*
import com.youplayertube.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.*
import org.junit.Assert.*

class LibraryDatabaseTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var db: YouPlayerTubeDatabase
    private lateinit var repository: RoomLibraryRepository
    private fun item(id: String, title: String = id, date: Long = 1) = MediaRecord(id, LocalUri("content://media/video/$id"), title, MediaKind.VIDEO, date)
    @Before fun open() {
        db = Room.inMemoryDatabaseBuilder(context, YouPlayerTubeDatabase::class.java).openHelperFactory(PreservingOpenHelperFactory()).build()
        repository = RoomLibraryRepository { db }
    }
    @After fun close() { db.close() }
    @Test fun realDatabaseRoundTripUpdateRemoveAndReactiveCounts() = runBlocking {
        assertEquals(WriteResult.Saved,repository.save(item("one")))
        repository.save(item("two"))
        repository.save(item("one","Updated"))
        val ready = repository.observe(LibraryQuery()).filterIsInstance<LoadState.Ready<LibraryData>>().first().value
        assertEquals(2,ready.summary.videos);assertEquals(2,ready.items.size)
        assertEquals("Updated",db.library().find("one")!!.toDomain().title)
        repository.removeMetadata("one");assertNull(db.library().find("one"))
    }
    @Test fun realDatabaseSortFilterBoundsAndSourceCounts() = runBlocking {
        repository.save(item("a","Zulu",1));repository.save(item("b","Alpha",2))
        repository.save(MediaRecord("yt",YouTubeReference(YouTubeVideoId("abcdefghijk")),"External",MediaKind.VIDEO,3))
        val first = repository.observe(LibraryQuery(LibrarySort.NAME,limit=1)).filterIsInstance<LoadState.Ready<LibraryData>>().first().value
        assertEquals("Alpha",first.items.single().title);assertEquals(2,first.summary.videos)
        val matching=db.library().observeItems("Zulu","DATE",100).first();assertEquals("a",matching.single().id)
    }
    @Test fun sourceUniquenessAndForeignKeysAreEnforced() = runBlocking {
        repository.save(item("one"))
        assertTrue(repository.save(item("one").copy(id="duplicate")) is WriteResult.Failed)
        assertTrue(repository.favorite("missing",1) is WriteResult.Failed)
    }
    @Test fun playlistReorderIsAtomicAndInvalidOrderDoesNotLoseEntries() = runBlocking {
        repository.save(item("a"));repository.save(item("b"));repository.savePlaylist(Playlist("p","Owned",1))
        repository.addToPlaylist("p","a");repository.addToPlaylist("p","b")
        assertEquals(WriteResult.Saved,repository.reorderPlaylist("p",listOf("b","a")))
        assertTrue(repository.reorderPlaylist("p",listOf("a","missing")) is WriteResult.Failed)
        assertEquals(listOf("b","a"),db.library().entries("p").map { it.mediaId })
        repository.save(item("a","updated"));assertEquals(2,db.library().entries("p").size)
    }
    @Test fun clearingHistoryDoesNotDeleteMetadataOrBytes() = runBlocking {
        val ownedFile=java.io.File(context.cacheDir,"history-owned-test");ownedFile.writeText("owned")
        try {
            repository.save(item("a"));repository.recordHistory(HistoryEvent("event","a",2,1))
            assertEquals(1,db.library().historyCount())
            repository.clearHistory();assertEquals(0,db.library().historyCount());assertNotNull(db.library().find("a"))
            repository.removeMetadata("a");assertEquals("owned",ownedFile.readText())
        } finally { ownedFile.delete() }
    }
    @Test fun onDiskDatabaseSurvivesCloseAndReopen() = runBlocking {
        val name="reopen-test.db"; context.deleteDatabase(name)
        try {
            YouPlayerTubeDatabase.open(context,name).let { disk -> try { disk.library().save(item("saved").toEntity()) } finally { disk.close() } }
            YouPlayerTubeDatabase.open(context,name).let { disk -> try { assertEquals("saved",disk.library().find("saved")!!.id) } finally { disk.close() } }
        } finally { context.deleteDatabase(name) }
    }
    @Test fun corruptDatabaseReportsFailureAndPreservesOriginalBytes() = runBlocking {
        val name="corrupt-test.db";context.deleteDatabase(name)
        val file=context.getDatabasePath(name);file.parentFile!!.mkdirs();val bytes=ByteArray(1024) { 42 };file.writeBytes(bytes)
        val broken=YouPlayerTubeDatabase.open(context,name)
        try {
            val result=RoomLibraryRepository { broken }.observe(LibraryQuery()).first { it !is LoadState.Loading }
            assertTrue(result is LoadState.Failed);assertTrue(file.exists());assertArrayEquals(bytes,file.readBytes())
        } finally { broken.close();context.deleteDatabase(name) }
    }
    @Test fun unknownSchemaVersionIsNotDestructivelyRecreated() = runBlocking {
        val name="version-test.db";context.deleteDatabase(name)
        context.openOrCreateDatabase(name,0,null).use { sql -> sql.execSQL("CREATE TABLE user_evidence (value TEXT)");sql.execSQL("INSERT INTO user_evidence VALUES ('keep')");sql.version=2 }
        val unsupported=YouPlayerTubeDatabase.open(context,name)
        try {
            assertTrue(RoomLibraryRepository { unsupported }.observe(LibraryQuery()).first { it !is LoadState.Loading } is LoadState.Failed)
        } finally { unsupported.close() }
        try { context.openOrCreateDatabase(name,0,null).use { sql -> sql.rawQuery("SELECT value FROM user_evidence",null).use { cursor -> assertTrue(cursor.moveToFirst());assertEquals("keep",cursor.getString(0)) } } } finally { context.deleteDatabase(name) }
    }
}
