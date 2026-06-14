package com.example.instrumenttrainer.data.repository

import com.example.instrumenttrainer.data.local.settings.AppSettingsDataStore
import com.example.instrumenttrainer.domain.model.AppLanguage
import com.example.instrumenttrainer.domain.model.AppSettings
import com.example.instrumenttrainer.domain.repository.AppSettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsRepositoryImpl @Inject constructor(
    private val dataStore: AppSettingsDataStore,
) : AppSettingsRepository {

    override val settings: Flow<AppSettings> = dataStore.settings

    override suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.setDarkTheme(enabled)
    }

    override suspend fun setLanguage(language: AppLanguage) {
        dataStore.setLanguage(language)
    }
}
