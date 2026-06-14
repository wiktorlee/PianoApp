package com.example.instrumenttrainer.data.local.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

val Context.appSettingsDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "app_settings",
)

object AppSettingsKeys {
    val DARK_THEME = booleanPreferencesKey("dark_theme")
    val LANGUAGE = stringPreferencesKey("language")
}
