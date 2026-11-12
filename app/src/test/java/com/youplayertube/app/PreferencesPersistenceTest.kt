package com.youplayertube.app
import androidx.datastore.preferences.core.*
import com.youplayertube.app.data.preferences.DataStorePreferencesRepository
import com.youplayertube.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.*
import org.junit.Assert.*
import org.junit.rules.TemporaryFolder

class PreferencesPersistenceTest {
    @get:Rule val files = TemporaryFolder()
    @Test fun durableUpdateReopensAndUnknownEnumUsesDefault() = runTest {
        val file = java.io.File(files.root,"prefs.preferences_pb")
        var job = SupervisorJob()
        var store = PreferenceDataStoreFactory.create(scope=CoroutineScope(Dispatchers.IO+job), produceFile={file})
        var repository = DataStorePreferencesRepository(store)
        assertEquals(LibrarySort.DATE,repository.preferences.first().librarySort)
        repository.setLibrarySort(LibrarySort.NAME)
        job.cancelAndJoin()
        job=SupervisorJob(); store=PreferenceDataStoreFactory.create(scope=CoroutineScope(Dispatchers.IO+job),produceFile={file})
        repository=DataStorePreferencesRepository(store)
        try {
            assertEquals(LibrarySort.NAME,repository.preferences.first().librarySort)
            store.edit { it[DataStorePreferencesRepository.SORT]="removed" }
            assertEquals(LibrarySort.DATE,repository.preferences.first().librarySort)
        } finally { job.cancelAndJoin() }
    }
    @Test fun corruptPreferencesArePreservedAndReported() = runTest {
        val file=java.io.File(files.root,"bad.preferences_pb"); val bytes=byteArrayOf(-1,-1,-1);file.writeBytes(bytes)
        val job=SupervisorJob(); val store=PreferenceDataStoreFactory.create(scope=CoroutineScope(Dispatchers.IO+job),produceFile={file})
        try {
            var failed=false
            try { DataStorePreferencesRepository(store).preferences.first() } catch (expected: androidx.datastore.core.CorruptionException) { failed=true }
            assertTrue(failed); assertArrayEquals(bytes,file.readBytes())
        } finally { job.cancelAndJoin() }
    }
}
