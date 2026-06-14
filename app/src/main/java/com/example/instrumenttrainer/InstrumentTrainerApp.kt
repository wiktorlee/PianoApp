package com.example.instrumenttrainer

import android.app.Application
import com.example.instrumenttrainer.data.local.settings.AppLocaleManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class InstrumentTrainerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLocaleManager.syncFromStorage(this)
    }
}
