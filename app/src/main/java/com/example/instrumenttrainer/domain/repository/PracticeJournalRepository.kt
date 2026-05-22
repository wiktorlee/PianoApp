package com.example.instrumenttrainer.domain.repository

import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import kotlinx.coroutines.flow.Flow

interface PracticeJournalRepository {
    suspend fun startSession(): Long

    suspend fun recordAttempt(
        sessionId: Long,
        detected: Note,
        target: Note,
        isCorrect: Boolean,
    )

    fun observeSessionSummaries(): Flow<List<PracticeSessionSummary>>
}
