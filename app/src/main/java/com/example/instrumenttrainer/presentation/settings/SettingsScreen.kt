package com.example.instrumenttrainer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.BuildConfig
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.AppLanguage
import com.example.instrumenttrainer.presentation.components.AppScreenHeader
import com.example.instrumenttrainer.presentation.components.SettingsToggleRow
import com.example.instrumenttrainer.presentation.components.SurfaceCard

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AppSettingsViewModel = hiltViewModel(),
) {
    val settings by viewModel.settings.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AppScreenHeader(
                title = stringResource(R.string.screen_settings_title),
                subtitle = stringResource(R.string.screen_settings_subtitle),
            )
        }

        item {
            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                verticalSpacing = 12.dp,
            ) {
                Text(
                    text = stringResource(R.string.settings_language_section),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                SettingsToggleRow(
                    label = stringResource(R.string.settings_language_english),
                    hint = stringResource(R.string.settings_language_hint),
                    checked = settings.language == AppLanguage.EN,
                    onCheckedChange = viewModel::setEnglish,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                verticalSpacing = 12.dp,
            ) {
                Text(
                    text = stringResource(R.string.settings_appearance_section),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.fillMaxWidth(),
                )
                SettingsToggleRow(
                    label = stringResource(R.string.settings_appearance_dark_theme),
                    checked = settings.darkTheme,
                    onCheckedChange = viewModel::setDarkTheme,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        item {
            Text(
                text = stringResource(R.string.settings_app_version, BuildConfig.VERSION_NAME),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 8.dp),
            )
        }
    }
}
