package com.example.instrumenttrainer.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.instrumenttrainer.presentation.components.AppScreenBackground
import com.example.instrumenttrainer.presentation.journal.ProgressJournalScreen
import com.example.instrumenttrainer.presentation.practice.PracticeRoomScreen
import com.example.instrumenttrainer.presentation.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun InstrumentTrainerRoot() {
    val pagerState = rememberPagerState(pageCount = { topLevelTabs.size })
    val scope = rememberCoroutineScope()
    val currentRoute = topLevelTabs[pagerState.currentPage].route

    Box(modifier = Modifier.fillMaxSize()) {
        AppScreenBackground()
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = Color.Transparent,
            bottomBar = {
                AppBottomBar(
                    currentRoute = currentRoute,
                    onTabSelected = { route ->
                        val index = routeToTabIndex(route)
                        scope.launch {
                            pagerState.animateScrollToPage(index)
                        }
                    },
                )
            },
        ) { innerPadding ->
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                beyondViewportPageCount = 0,
            ) { page ->
                when (topLevelTabs[page].route) {
                    NavRoutes.PRACTICE -> PracticeRoomScreen()
                    NavRoutes.JOURNAL -> ProgressJournalScreen()
                    NavRoutes.SETTINGS -> SettingsScreen()
                    else -> Unit
                }
            }
        }
    }
}
