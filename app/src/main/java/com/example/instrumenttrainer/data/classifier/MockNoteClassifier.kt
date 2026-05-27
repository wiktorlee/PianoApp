package com.example.instrumenttrainer.data.classifier

import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.NoteCatalog
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class MockNoteClassifier @Inject constructor() : NoteClassifier {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _detectedNotes = MutableSharedFlow<Note>(replay = 1)
    override val detectedNotes: SharedFlow<Note> = _detectedNotes.asSharedFlow()

    private var emitJob: Job? = null

    override fun start() {
        emitJob?.cancel()
        emitJob = scope.launch {
            while (isActive) {
                delay(EMIT_INTERVAL_MS)
                _detectedNotes.emit(randomNote())
            }
        }
    }

    override fun stop() {
        emitJob?.cancel()
        emitJob = null
    }

    override fun onAudioFrame(buffer: ShortArray, length: Int, sampleRate: Int) {
        // Mock ignores audio; TfliteNoteClassifier uses frames for live inference.
    }

    private fun randomNote(): Note = Note(
        name = NoteCatalog.PITCH_CLASS_NAMES.random(),
        octave = Random.nextInt(NoteCatalog.MIN_OCTAVE, NoteCatalog.MAX_OCTAVE + 1),
    )

    companion object {
        private const val EMIT_INTERVAL_MS = 500L
    }
}
