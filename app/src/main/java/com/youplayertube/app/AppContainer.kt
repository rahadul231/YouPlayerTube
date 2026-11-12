package com.youplayertube.app

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import com.youplayertube.app.data.RoomLibraryRepository
import com.youplayertube.app.data.local.YouPlayerTubeDatabase
import com.youplayertube.app.data.preferences.DataStorePreferencesRepository
import com.youplayertube.app.domain.*

private val Context.preferences by preferencesDataStore(name = "app_preferences")
class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    private val database by lazy { YouPlayerTubeDatabase.open(appContext) }
    val library: LibraryRepository = RoomLibraryRepository { database }
    val preferences: PreferencesRepository = DataStorePreferencesRepository(appContext.preferences)
}
