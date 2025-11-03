package com.example.todoapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.todoapp.presentation.details.DetailsScreen
import com.example.todoapp.presentation.list.ListScreen
import kotlinx.coroutines.CoroutineScope
import androidx.compose.material3.SnackbarHostState

@Composable
fun TodoNavigation(
    navController: NavHostController = rememberNavController(),
    snackbarHostState: SnackbarHostState? = null,
    scope: CoroutineScope? = null
) {
    NavHost(
        navController = navController,
        startDestination = "list"
    ) {
        composable("list") {
            ListScreen(
                onNavigateToDetails = { noteId ->
                    navController.navigate("details/$noteId")
                },
                onNavigateToCreateNote = {
                    navController.navigate("details/new")
                },
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }

        composable("details/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")
            DetailsScreen(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                snackbarHostState = snackbarHostState,
                scope = scope
            )
        }
    }
}