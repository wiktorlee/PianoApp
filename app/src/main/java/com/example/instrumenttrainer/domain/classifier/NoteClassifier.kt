package com.example.instrumenttrainer.domain.classifier

import com.example.instrumenttrainer.domain.model.Note
import kotlinx.coroutines.flow.Flow

interface NoteClassifier {
    val detectedNotes: Flow<Note>

    fun start()

    fun stop()

    fun onAudioFrame(buffer: ShortArray, length: Int)
}
