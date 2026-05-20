package com.example.instrumenttrainer.presentation.practice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.R

@Composable
fun PracticeRoomScreen(
    modifier: Modifier = Modifier,
    viewModel: PracticeRoomViewModel = hiltViewModel(),
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

    val isCorrect = state.detectedNote == state.targetNote
    val compareColor = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(R.string.screen_practice_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(R.string.practice_target_note, state.targetNote.displayName),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 20.dp),
        )
        Text(
            text = state.detectedNote?.displayName ?: "—",
            style = MaterialTheme.typography.displaySmall,
            color = compareColor,
            modifier = Modifier.padding(top = 24.dp),
        )
        Text(
            text = if (isCorrect) {
                stringResource(R.string.practice_match_ok)
            } else {
                stringResource(R.string.practice_match_wrong)
            },
            color = compareColor,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp),
        )
        Text(
            text = stringResource(R.string.practice_amplitude, state.amplitude),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(top = 8.dp),
        )
        LinearProgressIndicator(
            progress = { state.amplitude },
            modifier = Modifier
                .padding(top = 8.dp)
                .padding(horizontal = 24.dp),
        )

        if (!hasAudioPermission) {
            Text(
                text = stringResource(R.string.practice_permission_required),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp),
            )
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                modifier = Modifier.padding(top = 8.dp),
            ) {
                Text(text = stringResource(R.string.practice_permission_grant))
            }
        }
    }
}
