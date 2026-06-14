package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.NoteCatalog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PitchClassSelector(
    selectedName: String,
    onPitchSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.note_selector_pitch),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            NoteCatalog.PITCH_CLASS_NAMES.forEach { name ->
                FilterChip(
                    selected = selectedName == name,
                    onClick = { onPitchSelected(name) },
                    label = { Text(name) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = primary.copy(alpha = 0.18f),
                        selectedLabelColor = primary,
                    ),
                )
            }
        }
    }
}
