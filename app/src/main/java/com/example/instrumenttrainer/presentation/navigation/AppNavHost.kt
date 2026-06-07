package com.example.instrumenttrainer.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.instrumenttrainer.presentation.components.AppScreenBackground
import com.example.instrumenttrainer.presentation.journal.ProgressJournalScreen
import com.example.instrumenttrainer.presentation.practice.PracticeRoomScreen
import com.example.instrumenttrainer.presentation.settings.SettingsScreen

@Composable
fun InstrumentTrainerRoot() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Box(modifier = Modifier.fillMaxSize()) {
        AppScreenBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = androidx.compose.ui.graphics.Color.Transparent,
            bottomBar = {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        navController.navigate(route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            },
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.PRACTICE,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
            ) {
                composable(NavRoutes.PRACTICE) {
                    PracticeRoomScreen()
                }
                composable(NavRoutes.JOURNAL) {
                    ProgressJournalScreen()
                }
                composable(NavRoutes.SETTINGS) {
                    SettingsScreen()
                }
            }
        }
    }
}
