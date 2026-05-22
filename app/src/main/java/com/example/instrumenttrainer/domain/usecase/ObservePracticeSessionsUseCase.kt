package com.example.instrumenttrainer.domain.usecase

import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import com.example.instrumenttrainer.domain.repository.PracticeJournalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePracticeSessionsUseCase @Inject constructor(
    private val repository: PracticeJournalRepository,
) {
    operator fun invoke(): Flow<List<PracticeSessionSummary>> = repository.observeSessionSummaries()
}
