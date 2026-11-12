package com.youplayertube.app.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.youplayertube.app.domain.*
import kotlinx.coroutines.flow.map

/** No corruption reset handler: failures are surfaced, never silently overwritten. */
class DataStorePreferencesRepository(private val store: DataStore<Preferences>) : PreferencesRepository {
    override val preferences = store.data.map { values ->
        AppPreferences(LibrarySort.entries.firstOrNull { it.name == values[SORT] } ?: LibrarySort.DATE)
    }
    override suspend fun setLibrarySort(sort: LibrarySort) { store.edit { it[SORT] = sort.name } }
    companion object { val SORT = stringPreferencesKey("library_sort") }
}
