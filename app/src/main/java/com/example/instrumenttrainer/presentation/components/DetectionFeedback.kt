package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.Note

@Composable
fun DetectionFeedback(
    userPlayedNote: Note,
    detectedNote: Note?,
    amplitude: Float,
    modifier: Modifier = Modifier,
) {
    val isMatch = detectedNote != null && detectedNote == userPlayedNote
    val compareColor = when {
        detectedNote == null -> MaterialTheme.colorScheme.onSurface
        isMatch -> Color(0xFF2E7D32)
        else -> Color(0xFFC62828)
    }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = stringResource(R.string.feedback_user_note, userPlayedNote.displayName),
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = stringResource(
                R.string.feedback_detected_note,
                detectedNote?.displayName ?: "—",
            ),
            style = MaterialTheme.typography.displaySmall,
            color = compareColor,
            modifier = Modifier.padding(top = 16.dp),
        )
        if (detectedNote != null) {
            Text(
                text = if (isMatch) {
                    stringResource(R.string.feedback_match_ok)
                } else {
                    stringResource(R.string.feedback_match_wrong)
                },
                color = compareColor,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
        Text(
            text = stringResource(R.string.feedback_amplitude, amplitude),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 12.dp),
        )
        LinearProgressIndicator(
            progress = { amplitude },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
    }
}
