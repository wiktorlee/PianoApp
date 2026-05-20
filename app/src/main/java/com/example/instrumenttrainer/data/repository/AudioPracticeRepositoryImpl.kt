package com.example.instrumenttrainer.data.repository

import com.example.instrumenttrainer.data.audio.AudioCaptureManager
import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.repository.AudioPracticeRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt

@Singleton
class AudioPracticeRepositoryImpl @Inject constructor(
    private val audioCaptureManager: AudioCaptureManager,
    private val noteClassifier: NoteClassifier,
) : AudioPracticeRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var sessionJob: Job? = null

    private val _amplitude = MutableStateFlow(0f)
    override val amplitude: StateFlow<Float> = _amplitude.asStateFlow()

    override val detectedNotes: Flow<Note> = noteClassifier.detectedNotes

    override fun startSession() {
        if (sessionJob?.isActive == true) return

        noteClassifier.start()
        audioCaptureManager.start()

        sessionJob = scope.launch {
            audioCaptureManager.frames.collect { frame ->
                noteClassifier.onAudioFrame(frame.buffer, frame.length)
                _amplitude.value = computeAmplitude(frame.buffer, frame.length)
            }
        }
    }

    override fun stopSession() {
        sessionJob?.cancel()
        sessionJob = null
        audioCaptureManager.stop()
        noteClassifier.stop()
        _amplitude.value = 0f
    }

    private fun computeAmplitude(buffer: ShortArray, length: Int): Float {
        if (length == 0) return 0f
        var sum = 0.0
        for (i in 0 until length) {
            val sample = buffer[i].toDouble()
            sum += sample * sample
        }
        val rms = sqrt(sum / length)
        return (rms / Short.MAX_VALUE).toFloat().coerceIn(0f, 1f)
    }
}
