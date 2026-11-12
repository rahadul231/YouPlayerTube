package com.youplayertube.app
import androidx.lifecycle.SavedStateHandle
import com.youplayertube.app.ui.AppViewModel
import com.youplayertube.app.ui.navigation.MainTab
import com.youplayertube.app.domain.*
import kotlinx.coroutines.*
import kotlinx.coroutines.test.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.*

@OptIn(ExperimentalCoroutinesApi::class)
class AppViewModelTest {
    private val dispatcher = UnconfinedTestDispatcher()
    @Before fun setup() { Dispatchers.setMain(dispatcher) }
    @After fun cleanup() { Dispatchers.resetMain() }
    @Test fun selectionWritesSavedStateAndNewModelRestoresIt() {
        val handle = SavedStateHandle(); val model = AppViewModel(handle, TestLibrary(), TestPreferences())
        model.select(MainTab.SETTINGS)
        assertEquals("SETTINGS", handle.get<String>("selectedTab"))
        assertEquals(MainTab.SETTINGS, AppViewModel(handle, TestLibrary(), TestPreferences()).state.value.navigation.selected)
    }
    @Test fun backUpdatesBothVisibleAndSavedState() {
        val handle = SavedStateHandle(mapOf("selectedTab" to "DOWNLOADS"))
        val model = AppViewModel(handle, TestLibrary(), TestPreferences()); model.back()
        assertEquals(MainTab.HOME, model.state.value.navigation.selected); assertEquals("HOME", handle.get<String>("selectedTab"))
    }
    @Test fun freshLaunchIgnoresDurableSortForNavigation() {
        val prefs = TestPreferences(); prefs.state.value = AppPreferences(LibrarySort.NAME)
        val model = AppViewModel(SavedStateHandle(), TestLibrary(), prefs)
        assertEquals(MainTab.HOME, model.state.value.navigation.selected); assertEquals(LibrarySort.NAME, model.state.value.sort)
    }
    @Test fun corruptSavedSelectionRecoversWithoutDemoData() {
        val model = AppViewModel(SavedStateHandle(mapOf("selectedTab" to "obsolete")), TestLibrary(), TestPreferences())
        assertEquals(MainTab.HOME, model.state.value.navigation.selected)
        assertEquals(0, (model.state.value.library as LoadState.Ready).value.summary.videos)
    }
    @Test fun sortChangeDrivesRepositoryQuery() {
        val library = TestLibrary(); val model = AppViewModel(SavedStateHandle(), library, TestPreferences())
        model.setSort(LibrarySort.NAME)
        assertEquals(LibrarySort.NAME, library.lastQuery?.sort)
    }
    @Test fun preferenceWriteFailureIsVisibleAndDoesNotChangeSort() {
        val prefs = TestPreferences(); prefs.failWrite = true
        val model = AppViewModel(SavedStateHandle(), TestLibrary(), prefs); model.setSort(LibrarySort.NAME)
        assertEquals(LibrarySort.DATE, model.state.value.sort); assertNotNull(model.state.value.preferenceWriteError)
    }
    @Test fun databaseErrorIsNotAnEmptyLibraryAndRetryRecovers() {
        val library = TestLibrary(); library.data.value = LoadState.Failed("failed")
        val model = AppViewModel(SavedStateHandle(), library, TestPreferences())
        assertTrue(model.state.value.library is LoadState.Failed)
        library.data.value = LoadState.Ready(LibraryData(LibrarySnapshot(videos=1), emptyList()))
        model.retry(); assertEquals(1, (model.state.value.library as LoadState.Ready).value.summary.videos)
    }
    @Test fun preferenceReadFailureDoesNotBecomeDefaults() {
        val prefs = object: PreferencesRepository {
            override val preferences = flow<AppPreferences> { throw java.io.IOException("read failure") }
            override suspend fun setLibrarySort(sort: LibrarySort) { }
        }
        val model = AppViewModel(SavedStateHandle(), TestLibrary(), prefs)
        assertTrue(model.state.value.library is LoadState.Failed)
    }
}
