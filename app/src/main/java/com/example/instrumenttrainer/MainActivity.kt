package com.example.instrumenttrainer

import android.content.Context
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.toArgb
import androidx.core.view.WindowCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.data.local.settings.AppLocaleManager
import com.example.instrumenttrainer.presentation.navigation.InstrumentTrainerRoot
import com.example.instrumenttrainer.presentation.settings.AppSettingsViewModel
import com.example.instrumenttrainer.ui.theme.BrandBgDark
import com.example.instrumenttrainer.ui.theme.BrandBgLight
import com.example.instrumenttrainer.ui.theme.InstrumentTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    override fun attachBaseContext(newBase: Context) {
        AppLocaleManager.syncFromStorage(newBase)
        super.attachBaseContext(newBase)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: AppSettingsViewModel = hiltViewModel()
            val settings by viewModel.settings.collectAsState()
            val dark = settings.darkTheme

            SideEffect {
                val bg = if (dark) BrandBgDark else BrandBgLight
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                window.decorView.setBackgroundColor(bg.toArgb())
                WindowCompat.getInsetsController(window, window.decorView).apply {
                    isAppearanceLightStatusBars = !dark
                    isAppearanceLightNavigationBars = !dark
                }
            }

            InstrumentTrainerTheme(darkTheme = dark) {
                InstrumentTrainerRoot()
            }
        }
    }
}
