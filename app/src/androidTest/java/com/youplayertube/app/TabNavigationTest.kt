package com.youplayertube.app

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test

class TabNavigationTest {
    @get:Rule val compose = createAndroidComposeRule<MainActivity>()
    private fun tab(label: String) = compose.onNode(hasText(label) and hasClickAction())
    @Test fun tabsAndBackAndRecreation() {
        tab("Home").assertIsSelected()
        tab("Downloads").performClick().assertIsSelected()
        compose.onNodeWithText("No downloads yet").assertIsDisplayed()
        tab("Library").performClick().assertIsSelected()
        compose.onNodeWithText("Your library is empty").assertIsDisplayed()
        tab("Settings").performClick().assertIsSelected()
        compose.activityRule.scenario.recreate()
        tab("Settings").assertIsSelected()
        compose.runOnUiThread { compose.activity.onBackPressedDispatcher.onBackPressed() }
        tab("Home").assertIsSelected()
    }
}
