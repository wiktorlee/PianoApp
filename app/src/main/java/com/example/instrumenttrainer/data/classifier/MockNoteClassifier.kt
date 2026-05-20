package com.example.instrumenttrainer.data.classifier

import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import com.example.instrumenttrainer.domain.model.Note
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

    override fun onAudioFrame(buffer: ShortArray, length: Int) {
        // Mock ignores audio; TfliteNoteClassifier will use frames in a later stage.
    }

    private fun randomNote(): Note = Note(
        name = NOTE_NAMES.random(),
        octave = Random.nextInt(MIN_OCTAVE, MAX_OCTAVE + 1),
    )

    companion object {
        private const val EMIT_INTERVAL_MS = 500L
        private const val MIN_OCTAVE = 3
        private const val MAX_OCTAVE = 5
        private val NOTE_NAMES = listOf("C", "D", "E", "F", "G", "A", "B")
    }
}
