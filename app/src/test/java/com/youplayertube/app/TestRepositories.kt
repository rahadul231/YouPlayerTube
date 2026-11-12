package com.youplayertube.app
import com.youplayertube.app.domain.*
import kotlinx.coroutines.flow.*
class TestLibrary : LibraryRepository {
    val data = MutableStateFlow<LoadState<LibraryData>>(LoadState.Ready(LibraryData(LibrarySnapshot(), emptyList())))
    var lastQuery: LibraryQuery? = null
    override fun observe(query: LibraryQuery): Flow<LoadState<LibraryData>> { lastQuery = query; return data }
    override suspend fun save(record: MediaRecord) = WriteResult.Saved
    override suspend fun removeMetadata(id: String) = WriteResult.Saved
}
class TestPreferences : PreferencesRepository {
    val state = MutableStateFlow(AppPreferences())
    var failWrite = false
    override val preferences: Flow<AppPreferences> get() = state
    override suspend fun setLibrarySort(sort: LibrarySort) {
        if (failWrite) throw java.io.IOException("disk full")
        state.value = AppPreferences(sort)
    }
}
