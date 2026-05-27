package com.example.instrumenttrainer.presentation.practice

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

data class PracticeRoomUiState(
    val userPlayedNote: Note = NoteCatalog.defaultNote(),
    val detectedNote: Note? = null,
    val amplitude: Float = 0f,
    val isListening: Boolean = false,
    val lastSaveMessage: String? = null,
)

@HiltViewModel
class PracticeRoomViewModel @Inject constructor(
    observeDetectedNotes: ObserveDetectedNotesUseCase,
    private val managePracticeSession: ManagePracticeSessionUseCase,
    private val recordPracticeAttempt: RecordPracticeAttemptUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeRoomUiState())
    val uiState: StateFlow<PracticeRoomUiState> = _uiState.asStateFlow()

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
        _uiState.update { it.copy(userPlayedNote = note, lastSaveMessage = null) }
    }

    fun startListening() {
        if (_uiState.value.isListening) return
        viewModelScope.launch {
            if (currentSessionId == null) {
                currentSessionId = recordPracticeAttempt.startSession()
            }
            managePracticeSession.start()
            _uiState.update { it.copy(isListening = true, lastSaveMessage = null) }
        }
    }

    fun stopListening() {
        if (!_uiState.value.isListening) return
        managePracticeSession.stop()
        _uiState.update { it.copy(isListening = false, amplitude = 0f) }
    }

    fun saveAttempt() {
        val state = _uiState.value
        val detected = state.detectedNote
        if (detected == null) {
            _uiState.update { it.copy(lastSaveMessage = SAVE_NEED_DETECTION) }
            return
        }
        viewModelScope.launch {
            val sessionId = currentSessionId ?: recordPracticeAttempt.startSession().also {
                currentSessionId = it
            }
            recordPracticeAttempt(
                sessionId = sessionId,
                detected = detected,
                target = state.userPlayedNote,
            )
            val match = detected.samePitchClass(state.userPlayedNote)
            _uiState.update {
                it.copy(lastSaveMessage = if (match) SAVE_OK else SAVE_MISMATCH)
            }
        }
    }

    override fun onCleared() {
        managePracticeSession.stop()
        super.onCleared()
    }

    companion object {
        const val SAVE_OK = "saved_ok"
        const val SAVE_MISMATCH = "saved_mismatch"
        const val SAVE_NEED_DETECTION = "saved_need_detection"
    }
}
