package com.example.instrumenttrainer.presentation.settings

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instrumenttrainer.data.local.settings.AppLocaleManager
import com.example.instrumenttrainer.data.local.settings.AppSettingsKeys
import com.example.instrumenttrainer.data.local.settings.appSettingsDataStore
import com.example.instrumenttrainer.domain.model.AppLanguage
import com.example.instrumenttrainer.domain.model.AppSettings
import com.example.instrumenttrainer.domain.repository.AppSettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val repository: AppSettingsRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val settings: StateFlow<AppSettings> = repository.settings
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = runBlocking {
                val prefs = context.appSettingsDataStore.data.first()
                AppSettings(
                    darkTheme = prefs[AppSettingsKeys.DARK_THEME] ?: false,
                    language = AppLanguage.fromTag(prefs[AppSettingsKeys.LANGUAGE]),
                )
            },
        )

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { repository.setDarkTheme(enabled) }
    }

    fun setEnglish(enabled: Boolean) {
        viewModelScope.launch {
            val language = if (enabled) AppLanguage.EN else AppLanguage.PL
            repository.setLanguage(language)
            AppLocaleManager.apply(language)
        }
    }
}
