package com.example.instrumenttrainer.data.local.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.example.instrumenttrainer.domain.model.AppLanguage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object AppLocaleManager {

    fun syncFromStorage(context: Context) {
        apply(readLanguageBlocking(context))
    }

    fun apply(language: AppLanguage) {
        AppCompatDelegate.setApplicationLocales(
            LocaleListCompat.forLanguageTags(language.tag),
        )
    }

    fun readLanguageBlocking(context: Context): AppLanguage = runBlocking {
        readLanguage(context)
    }

    suspend fun readLanguage(context: Context): AppLanguage {
        val prefs = context.appSettingsDataStore.data.first()
        return AppLanguage.fromTag(prefs[AppSettingsKeys.LANGUAGE])
    }
}
