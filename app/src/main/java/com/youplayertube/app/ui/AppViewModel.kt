package com.youplayertube.app.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.youplayertube.app.domain.LibraryRepository
import com.youplayertube.app.ui.navigation.MainTab
import com.youplayertube.app.ui.navigation.NavigationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AppUiState(val navigation: NavigationState, val library: com.youplayertube.app.domain.LibrarySnapshot)
class AppViewModel(private val savedState: SavedStateHandle, repository: LibraryRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(AppUiState(NavigationState.restore(savedState["selectedTab"]), repository.snapshot()))
    val state = mutableState.asStateFlow()
    fun select(tab: MainTab) = update(mutableState.value.navigation.select(tab))
    fun back() { mutableState.value.navigation.back()?.let(::update) }
    private fun update(navigation: NavigationState) {
        savedState["selectedTab"] = navigation.save()
        mutableState.value = mutableState.value.copy(navigation = navigation)
    }
}
