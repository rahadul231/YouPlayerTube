package com.youplayertube.app
import com.youplayertube.app.ui.navigation.*
import com.youplayertube.app.domain.LibrarySnapshot
import org.junit.Assert.*
import org.junit.Test

class NavigationTest {
    @Test fun startsAtHome() { assertEquals(MainTab.HOME, NavigationState().selected) }
    @Test fun everyTabIsSelectableAndRestorable() {
        MainTab.entries.forEach { tab ->
            val selected = NavigationState().select(tab)
            assertEquals(tab, NavigationState.restore(selected.save()).selected)
        }
    }
    @Test fun backFromEveryOtherRootReturnsHome() {
        MainTab.entries.filter { it != MainTab.HOME }.forEach {
            assertEquals(NavigationState(), NavigationState(it).back())
        }
    }
    @Test fun homeBackDelegatesToAndroidExit() { assertNull(NavigationState().back()) }
    @Test fun unknownSavedRouteFallsBackToHome() {
        assertEquals(NavigationState(), NavigationState.restore("removed-route"))
        assertEquals(NavigationState(), NavigationState.restore(null))
    }
    @Test fun reselectDoesNotAddHistory() {
        val state = NavigationState(MainTab.LIBRARY)
        assertEquals(state, state.select(MainTab.LIBRARY))
        assertEquals(NavigationState(), state.select(MainTab.LIBRARY).back())
    }
    @Test fun initialLibraryContainsNoDemoCounts() {
        val snapshot = LibrarySnapshot()
        assertEquals(listOf(0,0,0,0,0,0), listOf(snapshot.videos,snapshot.music,snapshot.playlists,snapshot.favorites,snapshot.watchLater,snapshot.history))
    }
}
