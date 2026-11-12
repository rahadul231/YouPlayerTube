package com.youplayertube.app.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.youplayertube.app.ui.navigation.MainTab

private val colors = darkColorScheme(primary = Color(0xFFEF202B), background = Color(0xFF0B0B0E),
    surface = Color(0xFF17171C), onBackground = Color.White, onSurface = Color.White,
    secondaryContainer = Color(0xFF3A151B), onSecondaryContainer = Color(0xFFFF8C98))

@Composable
fun YouPlayerTubeApp(model: AppViewModel) {
    val state by model.state.collectAsStateWithLifecycle()
    BackHandler(enabled = state.navigation.selected != MainTab.HOME) { model.back() }
    val tabStates = rememberSaveableStateHolder()
    MaterialTheme(colorScheme = colors) {
        Scaffold(containerColor = colors.background, bottomBar = {
            NavigationBar(containerColor = colors.surface) {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(selected = state.navigation.selected == tab,
                        onClick = { model.select(tab) }, icon = { Icon(tabIcon(tab), contentDescription = null) },
                        label = { Text(tab.label()) }, colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = colors.primary, selectedTextColor = colors.primary))
                }
            }
        }) { padding ->
            tabStates.SaveableStateProvider(state.navigation.selected.name) {
            Box(Modifier.fillMaxSize().padding(padding)) {
                Column(Modifier.fillMaxSize().widthIn(max = 720.dp)
                    .align(androidx.compose.ui.Alignment.TopCenter)
                    .verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 28.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)) {
                    Text("YouPlayerTube", style = MaterialTheme.typography.labelLarge, color = colors.primary)
                    Text(state.navigation.selected.label(), style = MaterialTheme.typography.headlineLarge)
                    when (state.navigation.selected) {
                        MainTab.HOME -> {
                            InfoCard("Your media, in one place", "YouTube search and embedded playback are planned. Local and authorized media playback will be added in a later milestone.")
                            InfoCard("Provider boundaries", "YouTube downloads and forced background playback are unavailable. YouTube ads and player controls will remain provider-controlled.")
                        }
                        MainTab.DOWNLOADS -> InfoCard("No downloads yet", "Authorized downloads are planned. No download queue or files have been created.")
                        MainTab.LIBRARY -> {
                            InfoCard("Your library is empty", "Local media import and persistent collections are planned.")
                            val counts = state.library
                            listOf("Videos" to counts.videos, "Music" to counts.music, "Playlists" to counts.playlists,
                                "Favorites" to counts.favorites, "Watch Later" to counts.watchLater, "History" to counts.history).forEach { (name, count) ->
                                Text("$name · $count", style = MaterialTheme.typography.titleMedium)
                            }
                        }
                        MainTab.SETTINGS -> {
                            listOf("Playback", "Downloads", "Appearance", "Data & Storage", "Notifications", "Privacy & Security", "About").forEach { section ->
                                InfoCard(section, if (section == "About") "YouPlayerTube · Phase 03.1 preview. Independent app; not affiliated with YouTube or Google."
                                    else "Settings controls are planned for a later milestone.")
                            }
                        }
                    }
                }
            }
            }
        }
    }
}

@Composable
private fun InfoCard(title: String, body: String) {
    Surface(shape = MaterialTheme.shapes.large, color = colors.surface, modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(body, style = MaterialTheme.typography.bodyLarge, color = Color(0xFFBDBDC7))
        }
    }
}
private fun MainTab.label() = name.lowercase().replaceFirstChar { it.uppercase() }
/** Small original line icons; no icon-library dependency or replacement brand artwork. */
private fun tabIcon(tab: MainTab): ImageVector = ImageVector.Builder(tab.name, 24.dp, 24.dp, 24f, 24f).apply {
    path(stroke = androidx.compose.ui.graphics.SolidColor(Color.White), strokeLineWidth = 1.8f,
        fill = null) {
        when (tab) {
            MainTab.HOME -> { moveTo(3f,11f); lineTo(12f,3f); lineTo(21f,11f); moveTo(5f,10f); lineTo(5f,21f); lineTo(10f,21f); lineTo(10f,15f); lineTo(14f,15f); lineTo(14f,21f); lineTo(19f,21f); lineTo(19f,10f) }
            MainTab.DOWNLOADS -> { moveTo(12f,3f); lineTo(12f,16f); moveTo(7f,11f); lineTo(12f,16f); lineTo(17f,11f); moveTo(4f,17f); lineTo(4f,21f); lineTo(20f,21f); lineTo(20f,17f) }
            MainTab.LIBRARY -> { moveTo(4f,4f); lineTo(4f,20f); lineTo(9f,20f); lineTo(9f,4f); close(); moveTo(13f,4f); lineTo(13f,20f); moveTo(17f,4f); lineTo(21f,20f) }
            MainTab.SETTINGS -> { moveTo(4f,6f); lineTo(20f,6f); moveTo(4f,12f); lineTo(20f,12f); moveTo(4f,18f); lineTo(20f,18f); moveTo(8f,3f); lineTo(8f,9f); moveTo(16f,9f); lineTo(16f,15f); moveTo(10f,15f); lineTo(10f,21f) }
        }
    }
}.build()
