package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.NoteCatalog

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NoteSelector(
    selected: Note,
    onNoteChange: (Note) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.note_selector_pitch),
            style = MaterialTheme.typography.labelLarge,
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
                    selected = selected.name == name,
                    onClick = { onNoteChange(selected.copy(name = name)) },
                    label = { Text(name) },
                )
            }
        }
        Text(
            text = stringResource(R.string.note_selector_pitch_hint),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            IconButton(
                onClick = {
                    if (selected.octave > NoteCatalog.MIN_OCTAVE) {
                        onNoteChange(selected.copy(octave = selected.octave - 1))
                    }
                },
                enabled = selected.octave > NoteCatalog.MIN_OCTAVE,
            ) {
                Text(text = "−", style = MaterialTheme.typography.titleLarge)
            }
            Text(
                text = stringResource(R.string.note_selector_octave, selected.octave),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 16.dp),
            )
            IconButton(
                onClick = {
                    if (selected.octave < NoteCatalog.MAX_OCTAVE) {
                        onNoteChange(selected.copy(octave = selected.octave + 1))
                    }
                },
                enabled = selected.octave < NoteCatalog.MAX_OCTAVE,
            ) {
                Text(text = "+", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}
