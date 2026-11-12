package com.youplayertube.app

import androidx.lifecycle.SavedStateHandle
import com.youplayertube.app.data.EmptyLibraryRepository
import com.youplayertube.app.ui.AppViewModel
import com.youplayertube.app.ui.navigation.MainTab
import org.junit.Assert.*
import org.junit.Test

class AppViewModelTest {
    @Test fun selectionWritesSavedStateAndNewModelRestoresIt() {
        val handle = SavedStateHandle()
        val model = AppViewModel(handle, EmptyLibraryRepository())
        model.select(MainTab.SETTINGS)
        assertEquals("SETTINGS", handle.get<String>("selectedTab"))
        assertEquals(MainTab.SETTINGS, AppViewModel(handle, EmptyLibraryRepository()).state.value.navigation.selected)
    }
    @Test fun backUpdatesBothVisibleAndSavedState() {
        val handle = SavedStateHandle(mapOf("selectedTab" to "DOWNLOADS"))
        val model = AppViewModel(handle, EmptyLibraryRepository())
        model.back()
        assertEquals(MainTab.HOME, model.state.value.navigation.selected)
        assertEquals("HOME", handle.get<String>("selectedTab"))
        model.back()
        assertEquals(MainTab.HOME, model.state.value.navigation.selected)
    }
    @Test fun corruptSavedSelectionRecoversWithoutDemoData() {
        val model = AppViewModel(SavedStateHandle(mapOf("selectedTab" to "obsolete")), EmptyLibraryRepository())
        assertEquals(MainTab.HOME, model.state.value.navigation.selected)
        assertEquals(0, model.state.value.library.videos)
    }
}
