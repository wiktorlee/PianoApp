package com.example.instrumenttrainer.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.ui.theme.AppTheme
import com.example.instrumenttrainer.ui.theme.SemanticError
import com.example.instrumenttrainer.ui.theme.SemanticSuccess

private const val MIN_DISPLAY_AMPLITUDE = 0.015f

@Composable
fun DetectionFeedback(
    userPlayedNote: Note,
    detectedNote: Note?,
    amplitude: Float,
    modifier: Modifier = Modifier,
) {
    val extra = AppTheme.extra
    val idleColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.55f)
    val displayDetected = if (amplitude >= MIN_DISPLAY_AMPLITUDE) detectedNote else null
    val isMatch = displayDetected != null && displayDetected.samePitchClass(userPlayedNote)
    val ringColor = when {
        displayDetected == null -> extra.ringTrack
        isMatch -> SemanticSuccess
        else -> SemanticError
    }
    val statusLabel = when {
        displayDetected == null -> stringResource(R.string.feedback_status_listening)
        isMatch -> stringResource(R.string.feedback_match_ok)
        else -> stringResource(R.string.feedback_match_wrong)
    }
    val statusColor = when {
        displayDetected == null -> idleColor
        isMatch -> SemanticSuccess
        else -> SemanticError
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = statusLabel,
            style = MaterialTheme.typography.labelSmall,
            color = statusColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 12.dp),
        )

        NoteDetectionRing(
            noteText = displayDetected?.name ?: "—",
            sublabel = stringResource(R.string.feedback_user_note, userPlayedNote.name),
            progress = amplitude.coerceIn(0f, 1f),
            ringColor = ringColor,
            amplitude = amplitude,
        )

        AmplitudeBar(
            amplitude = amplitude,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp),
        )

        Text(
            text = stringResource(R.string.feedback_amplitude, amplitude),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}
