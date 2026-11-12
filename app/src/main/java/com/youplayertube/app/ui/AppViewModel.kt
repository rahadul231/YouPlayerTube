package com.youplayertube.app.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.youplayertube.app.domain.*
import com.youplayertube.app.ui.navigation.MainTab
import com.youplayertube.app.ui.navigation.NavigationState
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class AppUiState(
    val navigation: NavigationState,
    val library: LoadState<LibraryData> = LoadState.Loading,
    val sort: LibrarySort = LibrarySort.DATE,
    val preferenceWriteError: String? = null,
)
class AppViewModel(private val savedState: SavedStateHandle, private val repository: LibraryRepository,
    private val preferences: PreferencesRepository) : ViewModel() {
    private val mutableState = MutableStateFlow(AppUiState(NavigationState.restore(savedState["selectedTab"])))
    val state = mutableState.asStateFlow()
    private var observation: Job? = null
    init { retry() }
    fun select(tab: MainTab) = updateNavigation(mutableState.value.navigation.select(tab))
    fun back() { mutableState.value.navigation.back()?.let(::updateNavigation) }
    private fun updateNavigation(navigation: NavigationState) {
        savedState["selectedTab"] = navigation.save()
        mutableState.update { it.copy(navigation = navigation) }
    }
    fun retry() {
        observation?.cancel()
        mutableState.update { it.copy(library = LoadState.Loading) }
        observation = viewModelScope.launch {
            try {
                preferences.preferences.distinctUntilChanged().collectLatest { prefs ->
                    mutableState.update { it.copy(sort = prefs.librarySort) }
                    repository.observe(LibraryQuery(sort = prefs.librarySort)).collect { library ->
                        mutableState.update { it.copy(library = library) }
                    }
                }
            } catch (cause: Exception) {
                if (cause is CancellationException) throw cause
                mutableState.update { it.copy(library = LoadState.Failed("Preferences could not be loaded. Stored data has not been reset.")) }
            }
        }
    }
    fun setSort(sort: LibrarySort) {
        viewModelScope.launch {
            try {
                preferences.setLibrarySort(sort)
                mutableState.update { it.copy(preferenceWriteError = null) }
            } catch (cause: Exception) {
                if (cause is CancellationException) throw cause
                mutableState.update { it.copy(preferenceWriteError = "Sort preference could not be saved. Please retry.") }
            }
        }
    }
}
