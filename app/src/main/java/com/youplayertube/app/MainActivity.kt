package com.youplayertube.app

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Phase 02 build-check activity. This is NOT the branded UI: it only proves that the
 * project compiles, installs and launches, and shows the device it is running on.
 * Navigation, branding and features start in later phases.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaceholderTheme {
                BuildCheckScreen()
            }
        }
    }
}

// Placeholder tokens from the playbook defaults; final values are set in Prompt 04.
private val PlaceholderColors = darkColorScheme(
    primary = Color(0xFFEF202B),
    background = Color(0xFF0B0B0E),
    surface = Color(0xFF17171C),
    onBackground = Color.White,
    onSurface = Color.White,
)

@Composable
private fun PlaceholderTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = PlaceholderColors, content = content)
}

@Composable
private fun BuildCheckScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "YouPlayerTube",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Phase 02 build check",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
            Text(
                text = "Device: ${Build.MANUFACTURER} ${Build.MODEL}",
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )
        }
    }
}
