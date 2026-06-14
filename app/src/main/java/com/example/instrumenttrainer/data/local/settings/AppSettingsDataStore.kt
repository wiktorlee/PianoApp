package com.example.instrumenttrainer.data.local.settings

import android.content.Context
import androidx.datastore.preferences.core.edit
import com.example.instrumenttrainer.domain.model.AppLanguage
import com.example.instrumenttrainer.domain.model.AppSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppSettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    val settings: Flow<AppSettings> = context.appSettingsDataStore.data.map { prefs ->
        AppSettings(
            darkTheme = prefs[AppSettingsKeys.DARK_THEME] ?: false,
            language = AppLanguage.fromTag(prefs[AppSettingsKeys.LANGUAGE]),
        )
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.appSettingsDataStore.edit { prefs ->
            prefs[AppSettingsKeys.DARK_THEME] = enabled
        }
    }

    suspend fun setLanguage(language: AppLanguage) {
        context.appSettingsDataStore.edit { prefs ->
            prefs[AppSettingsKeys.LANGUAGE] = language.tag
        }
    }
}
