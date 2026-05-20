package com.example.instrumenttrainer.domain.usecase

import com.example.instrumenttrainer.domain.repository.AudioPracticeRepository
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

class ManagePracticeSessionUseCase @Inject constructor(
    private val repository: AudioPracticeRepository,
) {
    val amplitude: StateFlow<Float> = repository.amplitude

    fun start() = repository.startSession()

    fun stop() = repository.stopSession()
}
