package com.example.instrumenttrainer.domain.model

data class PracticeSessionSummary(
    val sessionId: Long,
    val startedAtMillis: Long,
    val totalAttempts: Int,
    val correctAttempts: Int,
) {
    val accuracyPercent: Int
        get() = if (totalAttempts == 0) {
            0
        } else {
            (correctAttempts * 100) / totalAttempts
        }
}
