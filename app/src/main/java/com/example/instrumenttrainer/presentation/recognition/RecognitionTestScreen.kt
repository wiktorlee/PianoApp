package com.example.instrumenttrainer.presentation.recognition

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.presentation.components.DetectionFeedback
import com.example.instrumenttrainer.presentation.components.NoteSelector

@Composable
fun RecognitionTestScreen(
    modifier: Modifier = Modifier,
    viewModel: RecognitionTestViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO,
            ) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasAudioPermission = granted
    }

    DisposableEffect(Unit) {
        onDispose { viewModel.endTestSession() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.screen_recognition_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.recognition_hint),
            style = MaterialTheme.typography.bodyMedium,
        )

        if (!hasAudioPermission) {
            Text(text = stringResource(R.string.practice_permission_required))
            Button(onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) }) {
                Text(text = stringResource(R.string.practice_permission_grant))
            }
            return@Column
        }

        if (!state.isSessionActive) {
            Button(
                onClick = viewModel::startTestSession,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.recognition_start_session))
            }
        } else {
            Text(
                text = stringResource(
                    R.string.recognition_session_stats,
                    state.correctInSession,
                    state.attemptsInSession,
                ),
                style = MaterialTheme.typography.titleSmall,
            )

            NoteSelector(
                selected = state.userPlayedNote,
                onNoteChange = viewModel::setUserPlayedNote,
                modifier = Modifier.fillMaxWidth(),
            )

            DetectionFeedback(
                userPlayedNote = state.userPlayedNote,
                detectedNote = state.detectedNote,
                amplitude = state.amplitude,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = viewModel::saveAttempt,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.action_save_attempt))
            }

            state.lastResultKey?.let { key ->
                Text(
                    text = resultMessageText(key),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            OutlinedButton(
                onClick = viewModel::endTestSession,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = stringResource(R.string.recognition_end_session))
            }
        }
    }
}

@Composable
private fun resultMessageText(key: String): String = when (key) {
    RecognitionTestViewModel.RESULT_MATCH -> stringResource(R.string.recognition_result_match)
    RecognitionTestViewModel.RESULT_MISMATCH -> stringResource(R.string.recognition_result_mismatch)
    RecognitionTestViewModel.RESULT_NEED_DETECTION -> stringResource(R.string.recognition_result_need_detection)
    else -> key
}
