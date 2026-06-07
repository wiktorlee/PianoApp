package com.example.instrumenttrainer.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.presentation.components.AppScreenHeader
import com.example.instrumenttrainer.presentation.components.SurfaceCard

@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
) {
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
            SettingsSectionCard(
                title = stringResource(R.string.settings_appearance_section),
                body = stringResource(R.string.settings_appearance_placeholder),
            )
        }
        item {
            SettingsSectionCard(
                title = stringResource(R.string.settings_model_section),
                body = stringResource(R.string.settings_model_placeholder),
            )
        }
        item {
            SettingsSectionCard(
                title = stringResource(R.string.settings_practice_section),
                body = stringResource(R.string.settings_practice_placeholder),
            )
        }
    }
}

@Composable
private fun SettingsSectionCard(
    title: String,
    body: String,
) {
    SurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
        )
    }
}
