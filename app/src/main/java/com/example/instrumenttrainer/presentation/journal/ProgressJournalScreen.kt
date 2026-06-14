package com.example.instrumenttrainer.presentation.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import com.example.instrumenttrainer.presentation.components.AppScreenHeader
import com.example.instrumenttrainer.presentation.components.SurfaceCard
import com.example.instrumenttrainer.ui.theme.AppTheme

@Composable
fun ProgressJournalScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressJournalViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val primary = MaterialTheme.colorScheme.primary
    val trackColor = AppTheme.extra.ringTrack

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            AppScreenHeader(
                title = stringResource(R.string.screen_journal_title),
                subtitle = stringResource(R.string.screen_journal_subtitle),
            )
        }

        item {
            SurfaceCard(
                modifier = Modifier.fillMaxWidth(),
                strong = true,
                verticalSpacing = 16.dp,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.journal_accuracy_label),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = stringResource(
                                R.string.journal_attempts_count,
                                state.totalAttempts,
                            ),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Text(
                        text = stringResource(
                            R.string.journal_accuracy_value,
                            state.overallAccuracyPercent,
                        ),
                        style = MaterialTheme.typography.displaySmall,
                        color = primary,
                    )
                }
                LinearProgressIndicator(
                    progress = { state.overallAccuracyPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp),
                    color = primary,
                    trackColor = trackColor,
                )
            }
        }

        if (state.sessions.isEmpty()) {
            item {
                SurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.journal_empty),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            items(state.sessions, key = { it.sessionId }) { session ->
                SessionRow(session = session)
            }
        }
    }
}

@Composable
private fun SessionRow(session: PracticeSessionSummary) {
    val primary = MaterialTheme.colorScheme.primary
    val trackColor = AppTheme.extra.ringTrack
    val dateText = java.text.SimpleDateFormat("dd.MM.yyyy HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(session.startedAtMillis))

    SurfaceCard(
        modifier = Modifier.fillMaxWidth(),
        verticalSpacing = 10.dp,
    ) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = stringResource(
                    R.string.journal_session_correct,
                    session.correctAttempts,
                    session.totalAttempts,
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = stringResource(
                    R.string.journal_accuracy_value,
                    session.accuracyPercent,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = primary,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
        LinearProgressIndicator(
            progress = { session.accuracyPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = primary,
            trackColor = trackColor,
        )
    }
}
