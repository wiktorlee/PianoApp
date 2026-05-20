package com.example.instrumenttrainer.domain.repository

import com.example.instrumenttrainer.domain.model.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface AudioPracticeRepository {
    val detectedNotes: Flow<Note>
    val amplitude: StateFlow<Float>

    fun startSession()

    fun stopSession()
}
