package com.example.instrumenttrainer.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.presentation.challenge.ChallengeModeScreen
import com.example.instrumenttrainer.presentation.journal.ProgressJournalScreen
import com.example.instrumenttrainer.presentation.practice.PracticeRoomScreen
import com.example.instrumenttrainer.presentation.settings.SettingsScreen

data class TopLevelDestination(
    val route: String,
    @StringRes val labelRes: Int,
    @StringRes val titleRes: Int,
)

private val topLevelDestinations = listOf(
    TopLevelDestination(NavRoutes.PRACTICE, R.string.nav_practice, R.string.screen_practice_title),
    TopLevelDestination(NavRoutes.JOURNAL, R.string.nav_journal, R.string.screen_journal_title),
    TopLevelDestination(NavRoutes.CHALLENGE, R.string.nav_challenge, R.string.screen_challenge_title),
    TopLevelDestination(NavRoutes.SETTINGS, R.string.nav_settings, R.string.screen_settings_title),
)

@Composable
fun InstrumentTrainerRoot() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    Scaffold(
        bottomBar = {
            NavigationBar {
                topLevelDestinations.forEach { destination ->
                    val selected = currentDestination?.hierarchy?.any { it.route == destination.route } == true
                    NavigationBarItem(
                        selected = selected,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { IconPlaceholder() },
                        label = { Text(stringResource(destination.labelRes)) },
                    )
                }
            }
        },
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoutes.PRACTICE,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(NavRoutes.PRACTICE) {
                PracticeRoomScreen(title = stringResource(R.string.screen_practice_title))
            }
            composable(NavRoutes.JOURNAL) {
                ProgressJournalScreen(title = stringResource(R.string.screen_journal_title))
            }
            composable(NavRoutes.CHALLENGE) {
                ChallengeModeScreen(title = stringResource(R.string.screen_challenge_title))
            }
            composable(NavRoutes.SETTINGS) {
                SettingsScreen(title = stringResource(R.string.screen_settings_title))
            }
        }
    }
}

@Composable
private fun IconPlaceholder() {
    Text(text = "•")
}
