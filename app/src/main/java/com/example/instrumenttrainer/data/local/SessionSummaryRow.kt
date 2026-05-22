package com.example.instrumenttrainer.data.local

data class SessionSummaryRow(
    val sessionId: Long,
    val startedAtMillis: Long,
    val totalAttempts: Int,
    val correctAttempts: Int,
)
