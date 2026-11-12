package com.youplayertube.app.ui.navigation

enum class MainTab { HOME, DOWNLOADS, LIBRARY, SETTINGS }
/** Roots only in 03.1. Add per-tab child stacks when child destinations are implemented. */
data class NavigationState(val selected: MainTab = MainTab.HOME) {
    fun select(tab: MainTab) = copy(selected = tab)
    fun back(): NavigationState? = if (selected == MainTab.HOME) null else NavigationState()
    fun save(): String = selected.name
    companion object {
        fun restore(value: String?) = NavigationState(MainTab.entries.firstOrNull { it.name == value } ?: MainTab.HOME)
    }
}
