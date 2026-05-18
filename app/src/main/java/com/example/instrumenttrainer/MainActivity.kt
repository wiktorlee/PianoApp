package com.example.instrumenttrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.instrumenttrainer.presentation.navigation.InstrumentTrainerRoot
import com.example.instrumenttrainer.ui.theme.InstrumentTrainerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            InstrumentTrainerTheme {
                InstrumentTrainerRoot()
            }
        }
    }
}
