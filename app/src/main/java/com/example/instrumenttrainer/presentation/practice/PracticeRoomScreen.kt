package com.example.instrumenttrainer.presentation.practice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import com.example.instrumenttrainer.presentation.components.AppScreenHeader
import com.example.instrumenttrainer.presentation.components.DetectionFeedback
import com.example.instrumenttrainer.presentation.components.PitchClassSelector
import com.example.instrumenttrainer.presentation.components.SurfaceCard

@Composable
fun PracticeRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: PracticeRoomViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val primary = MaterialTheme.colorScheme.primary
    var saveFeedback by remember { mutableStateOf<String?>(null) }

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

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }
    LaunchedEffect(hasAudioPermission) {
        if (hasAudioPermission) {
            viewModel.startListening()
        } else {
            viewModel.stopListening()
        }
    }
    DisposableEffect(Unit) {
        onDispose { viewModel.stopListening() }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        AppScreenHeader(
            title = stringResource(R.string.screen_main_title),
            subtitle = stringResource(R.string.screen_main_subtitle),
        )

        if (!hasAudioPermission) {
            SurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.practice_permission_required),
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primary),
                ) {
                    Text(text = stringResource(R.string.practice_permission_grant))
                }
            }
            return@Column
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            DetectionFeedback(
                userPlayedNote = state.userPlayedNote,
                detectedNote = state.detectedNote,
                amplitude = state.amplitude,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        SurfaceCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PitchClassSelector(
                    selectedName = state.userPlayedNote.name,
                    onPitchSelected = viewModel::setUserPlayedPitch,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = stringResource(R.string.practice_save_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Button(
            onClick = {
                viewModel.saveCurrentAttempt(
                    onSaved = {
                        saveFeedback = context.getString(R.string.practice_save_success)
                    },
                    onNothingToSave = {
                        saveFeedback = context.getString(R.string.practice_save_empty)
                    },
                )
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = primary),
        ) {
            Text(text = stringResource(R.string.practice_save_attempt))
        }

        if (saveFeedback != null) {
            Text(
                text = saveFeedback!!,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        OutlinedButton(
            onClick = {
                if (state.isListening) {
                    viewModel.stopListening()
                } else {
                    viewModel.startListening()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary,
            ),
        ) {
            Text(
                text = if (state.isListening) {
                    stringResource(R.string.practice_stop_listening)
                } else {
                    stringResource(R.string.practice_start_listening)
                },
            )
        }
    }
}
