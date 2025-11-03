package com.example.todoapp.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.ComposeTestRule

fun ComposeTestRule.assertTextDisplayed(text: String) {
    this.onNodeWithText(text).assertIsDisplayed()
}

fun ComposeTestRule.waitForText(text: String, timeoutMillis: Long = 5000) {
    this.waitUntil(timeoutMillis) {
        try {
            this.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        } catch (e: Exception) {
            false
        }
    }
}
