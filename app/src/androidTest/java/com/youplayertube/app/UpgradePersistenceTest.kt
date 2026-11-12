package com.youplayertube.app

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.test.platform.app.InstrumentationRegistry
import com.youplayertube.app.data.local.*
import com.youplayertube.app.data.preferences.DataStorePreferencesRepository
import com.youplayertube.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import org.junit.Test
import org.junit.Assert.*

/** Explicitly run prepare before package replacement, verify afterwards; excluded from normal suite. */
class UpgradePersistenceTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    @Test fun prepare() = runBlocking {
        val db=YouPlayerTubeDatabase.open(context)
        try { db.library().save(MediaRecord("upgrade-fixture",LocalUri("content://media/video/upgrade"),"Upgrade fixture",MediaKind.VIDEO,1).toEntity()) } finally { db.close() }
        preferences { it.setLibrarySort(LibrarySort.NAME) }
    }
    @Test fun verify() = runBlocking {
        val db=YouPlayerTubeDatabase.open(context)
        try { assertEquals("Upgrade fixture",db.library().find("upgrade-fixture")!!.title) } finally { db.close() }
        preferences { assertEquals(LibrarySort.NAME,it.preferences.first().librarySort) }
        assertEquals("retained",java.io.File(context.filesDir,"upgrade-marker").readText().trim())
    }
    private suspend fun preferences(block: suspend (DataStorePreferencesRepository)->Unit) {
        val job=SupervisorJob()
        val file=java.io.File(context.filesDir,"datastore/app_preferences.preferences_pb")
        val store=PreferenceDataStoreFactory.create(scope=CoroutineScope(Dispatchers.IO+job),produceFile={file})
        try { block(DataStorePreferencesRepository(store)) } finally { job.cancelAndJoin() }
    }
}
