package com.example.todoapp.screens

import androidx.compose.ui.test.junit4.ComposeContentTestRule

/**
 * Base class for all screen objects.
 */
open class BaseScreen(protected val composeTestRule: ComposeContentTestRule) {

    fun waitForIdle() {
        composeTestRule.waitForIdle()
    }
}
