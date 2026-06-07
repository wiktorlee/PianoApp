package com.example.instrumenttrainer.presentation.journal

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.instrumenttrainer.R
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import com.example.instrumenttrainer.presentation.components.AppScreenHeader
import com.example.instrumenttrainer.presentation.components.SurfaceCard
import com.example.instrumenttrainer.ui.theme.BrandCoral
import com.example.instrumenttrainer.ui.theme.BrandTextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProgressJournalScreen(
    modifier: Modifier = Modifier,
    viewModel: ProgressJournalViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

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
                        color = BrandCoral,
                    )
                }
                LinearProgressIndicator(
                    progress = { state.overallAccuracyPercent / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    color = BrandCoral,
                    trackColor = BrandTextTertiary.copy(alpha = 0.2f),
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
    val dateText = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        .format(Date(session.startedAtMillis))

    SurfaceCard(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = dateText,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
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
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = stringResource(
                    R.string.journal_accuracy_value,
                    session.accuracyPercent,
                ),
                style = MaterialTheme.typography.titleMedium,
                color = BrandCoral,
            )
        }
        LinearProgressIndicator(
            progress = { session.accuracyPercent / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            color = BrandCoral,
            trackColor = BrandTextTertiary.copy(alpha = 0.2f),
        )
    }
}
