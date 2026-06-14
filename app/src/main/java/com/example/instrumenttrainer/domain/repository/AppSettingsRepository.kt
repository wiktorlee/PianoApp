package com.example.instrumenttrainer.domain.repository

import com.example.instrumenttrainer.domain.model.AppLanguage
import com.example.instrumenttrainer.domain.model.AppSettings
import kotlinx.coroutines.flow.Flow

interface AppSettingsRepository {
    val settings: Flow<AppSettings>

    suspend fun setDarkTheme(enabled: Boolean)

    suspend fun setLanguage(language: AppLanguage)
}
