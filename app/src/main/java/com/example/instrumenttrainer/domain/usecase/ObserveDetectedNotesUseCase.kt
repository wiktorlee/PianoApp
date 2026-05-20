package com.example.instrumenttrainer.domain.usecase

import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.repository.AudioPracticeRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDetectedNotesUseCase @Inject constructor(
    private val repository: AudioPracticeRepository,
) {
    operator fun invoke(): Flow<Note> = repository.detectedNotes
}
