package com.example.instrumenttrainer.presentation.journal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import com.example.instrumenttrainer.domain.usecase.ObservePracticeSessionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgressJournalUiState(
    val sessions: List<PracticeSessionSummary> = emptyList(),
    val overallAccuracyPercent: Int = 0,
    val totalAttempts: Int = 0,
)

@HiltViewModel
class ProgressJournalViewModel @Inject constructor(
    observePracticeSessions: ObservePracticeSessionsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressJournalUiState())
    val uiState: StateFlow<ProgressJournalUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            observePracticeSessions().collect { sessions ->
                val withAttempts = sessions.filter { it.totalAttempts > 0 }
                val total = withAttempts.sumOf { it.totalAttempts }
                val correct = withAttempts.sumOf { it.correctAttempts }
                val overall = if (total == 0) 0 else (correct * 100) / total
                _uiState.update {
                    ProgressJournalUiState(
                        sessions = withAttempts,
                        overallAccuracyPercent = overall,
                        totalAttempts = total,
                    )
                }
            }
        }
    }
}
