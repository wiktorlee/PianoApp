package com.example.instrumenttrainer.data.repository

import com.example.instrumenttrainer.data.local.dao.PracticeJournalDao
import com.example.instrumenttrainer.data.local.entity.PracticeAttemptEntity
import com.example.instrumenttrainer.data.local.entity.PracticeSessionEntity
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.PracticeSessionSummary
import com.example.instrumenttrainer.domain.repository.PracticeJournalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PracticeJournalRepositoryImpl @Inject constructor(
    private val dao: PracticeJournalDao,
) : PracticeJournalRepository {

    override suspend fun startSession(): Long {
        return dao.insertSession(
            PracticeSessionEntity(startedAtMillis = System.currentTimeMillis()),
        )
    }

    override suspend fun recordAttempt(
        sessionId: Long,
        detected: Note,
        target: Note,
        isCorrect: Boolean,
    ) {
        dao.insertAttempt(
            PracticeAttemptEntity(
                sessionId = sessionId,
                detectedNoteName = detected.name,
                detectedOctave = detected.octave,
                targetNoteName = target.name,
                targetOctave = target.octave,
                isCorrect = isCorrect,
            ),
        )
    }

    override fun observeSessionSummaries(): Flow<List<PracticeSessionSummary>> {
        return dao.observeSessionSummaries().map { rows ->
            rows.map { row ->
                PracticeSessionSummary(
                    sessionId = row.sessionId,
                    startedAtMillis = row.startedAtMillis,
                    totalAttempts = row.totalAttempts,
                    correctAttempts = row.correctAttempts,
                )
            }
        }
    }
}
