package com.example.todoapp.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.junit4.ComposeTestRule

fun ComposeTestRule.assertTextDisplayed(text: String) {
    this.onNodeWithText(text).assertIsDisplayed()
}