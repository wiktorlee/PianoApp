package com.example.instrumenttrainer.presentation.recognition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.NoteCatalog
import com.example.instrumenttrainer.domain.usecase.ManagePracticeSessionUseCase
import com.example.instrumenttrainer.domain.usecase.ObserveDetectedNotesUseCase
import com.example.instrumenttrainer.domain.usecase.RecordPracticeAttemptUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class RecognitionTestUiState(
    val userPlayedNote: Note = NoteCatalog.defaultNote(),
    val detectedNote: Note? = null,
    val amplitude: Float = 0f,
    val isListening: Boolean = false,
    val isSessionActive: Boolean = false,
    val attemptsInSession: Int = 0,
    val correctInSession: Int = 0,
    val lastResultKey: String? = null,
)

@HiltViewModel
class RecognitionTestViewModel @Inject constructor(
    observeDetectedNotes: ObserveDetectedNotesUseCase,
    private val managePracticeSession: ManagePracticeSessionUseCase,
    private val recordPracticeAttempt: RecordPracticeAttemptUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecognitionTestUiState())
    val uiState: StateFlow<RecognitionTestUiState> = _uiState.asStateFlow()

    private var currentSessionId: Long? = null

    init {
        viewModelScope.launch {
            observeDetectedNotes().collect { note ->
                _uiState.update { it.copy(detectedNote = note) }
            }
        }
        viewModelScope.launch {
            managePracticeSession.amplitude.collect { amplitude ->
                _uiState.update { it.copy(amplitude = amplitude) }
            }
        }
    }

    fun setUserPlayedNote(note: Note) {
        _uiState.update { it.copy(userPlayedNote = note, lastResultKey = null) }
    }

    fun startTestSession() {
        if (_uiState.value.isSessionActive) return
        viewModelScope.launch {
            currentSessionId = recordPracticeAttempt.startSession()
            managePracticeSession.start()
            _uiState.update {
                it.copy(
                    isSessionActive = true,
                    isListening = true,
                    attemptsInSession = 0,
                    correctInSession = 0,
                    lastResultKey = null,
                )
            }
        }
    }

    fun endTestSession() {
        managePracticeSession.stop()
        currentSessionId = null
        _uiState.update {
            it.copy(
                isSessionActive = false,
                isListening = false,
                amplitude = 0f,
                lastResultKey = null,
            )
        }
    }

    fun saveAttempt() {
        val state = _uiState.value
        if (!state.isSessionActive) return
        val detected = state.detectedNote
        if (detected == null) {
            _uiState.update { it.copy(lastResultKey = RESULT_NEED_DETECTION) }
            return
        }
        viewModelScope.launch {
            val sessionId = currentSessionId ?: return@launch
            val isCorrect = detected.samePitchClass(state.userPlayedNote)
            recordPracticeAttempt(
                sessionId = sessionId,
                detected = detected,
                target = state.userPlayedNote,
            )
            _uiState.update {
                it.copy(
                    attemptsInSession = it.attemptsInSession + 1,
                    correctInSession = it.correctInSession + if (isCorrect) 1 else 0,
                    lastResultKey = if (isCorrect) RESULT_MATCH else RESULT_MISMATCH,
                )
            }
        }
    }

    override fun onCleared() {
        managePracticeSession.stop()
        super.onCleared()
    }

    companion object {
        const val RESULT_MATCH = "result_match"
        const val RESULT_MISMATCH = "result_mismatch"
        const val RESULT_NEED_DETECTION = "result_need_detection"
    }
}
