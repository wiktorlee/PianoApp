package com.example.instrumenttrainer.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.instrumenttrainer.data.local.SessionSummaryRow
import com.example.instrumenttrainer.data.local.entity.PracticeAttemptEntity
import com.example.instrumenttrainer.data.local.entity.PracticeSessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PracticeJournalDao {

    @Insert
    suspend fun insertSession(session: PracticeSessionEntity): Long

    @Insert
    suspend fun insertAttempt(attempt: PracticeAttemptEntity)

    @Query(
        """
        SELECT
            s.id AS sessionId,
            s.startedAtMillis AS startedAtMillis,
            COUNT(a.id) AS totalAttempts,
            COALESCE(SUM(CASE WHEN a.isCorrect THEN 1 ELSE 0 END), 0) AS correctAttempts
        FROM practice_sessions s
        LEFT JOIN practice_attempts a ON s.id = a.sessionId
        GROUP BY s.id
        ORDER BY s.startedAtMillis DESC
        """,
    )
    fun observeSessionSummaries(): Flow<List<SessionSummaryRow>>
}
