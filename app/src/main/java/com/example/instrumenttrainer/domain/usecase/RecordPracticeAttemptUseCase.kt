package com.example.instrumenttrainer.domain.usecase

import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.repository.PracticeJournalRepository
import javax.inject.Inject

class RecordPracticeAttemptUseCase @Inject constructor(
    private val repository: PracticeJournalRepository,
) {
    suspend fun startSession(): Long = repository.startSession()

    suspend operator fun invoke(
        sessionId: Long,
        detected: Note,
        target: Note,
    ) {
        repository.recordAttempt(
            sessionId = sessionId,
            detected = detected,
            target = target,
            isCorrect = detected.samePitchClass(target),
        )
    }
}
