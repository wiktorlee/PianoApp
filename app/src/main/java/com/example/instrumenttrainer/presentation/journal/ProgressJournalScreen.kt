package com.example.instrumenttrainer.presentation.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressJournalScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressJournalViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(R.string.screen_journal_title),
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = stringResource(
                R.string.journal_overall_accuracy,
                state.overallAccuracyPercent,
                state.totalAttempts,
            ),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(top = 16.dp),
        )
        LinearProgressIndicator(
            progress = { state.overallAccuracyPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )

        if (state.sessions.isEmpty()) {
            Text(
                text = stringResource(R.string.journal_empty),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 24.dp),
            )
        } else {
            LazyColumn(
                modifier = Modifier.padding(top = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.sessions, key = { it.sessionId }) { session ->
                    SessionRow(session = session)
                }
            }
        }
    }
}

@Composable
private fun SessionRow(session: PracticeSessionSummary) {
    val dateText = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(Date(session.startedAtMillis))

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = dateText, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = stringResource(
                R.string.journal_session_stats,
                session.correctAttempts,
                session.totalAttempts,
                session.accuracyPercent,
            ),
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 4.dp),
        )
        LinearProgressIndicator(
            progress = { session.accuracyPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
        )
    }
}
