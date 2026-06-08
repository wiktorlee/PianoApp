package com.example.instrumenttrainer.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.instrumenttrainer.R

data class TopLevelTab(
    val route: String,
    @StringRes val labelRes: Int,
    val icon: ImageVector,
)

val topLevelTabs = listOf(
    TopLevelTab(NavRoutes.PRACTICE, R.string.tab_practice, Icons.Outlined.Mic),
    TopLevelTab(NavRoutes.JOURNAL, R.string.tab_journal, Icons.Outlined.History),
    TopLevelTab(NavRoutes.SETTINGS, R.string.tab_settings, Icons.Outlined.Settings),
)

fun routeToTabIndex(route: String): Int =
    topLevelTabs.indexOfFirst { it.route == route }.coerceAtLeast(0)
