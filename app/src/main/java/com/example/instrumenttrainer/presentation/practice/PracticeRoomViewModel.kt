package com.example.instrumenttrainer.presentation.practice

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.usecase.ManagePracticeSessionUseCase
import com.example.instrumenttrainer.domain.usecase.ObserveDetectedNotesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PracticeRoomUiState(
    val detectedNote: Note? = null,
    val amplitude: Float = 0f,
    val targetNote: Note = Note(name = "C", octave = 4),
    val isListening: Boolean = false,
)

@HiltViewModel
class PracticeRoomViewModel @Inject constructor(
    observeDetectedNotes: ObserveDetectedNotesUseCase,
    private val managePracticeSession: ManagePracticeSessionUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PracticeRoomUiState())
    val uiState: StateFlow<PracticeRoomUiState> = _uiState.asStateFlow()

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

    fun startListening() {
        if (_uiState.value.isListening) return
        managePracticeSession.start()
        _uiState.update { it.copy(isListening = true) }
    }

    fun stopListening() {
        if (!_uiState.value.isListening) return
        managePracticeSession.stop()
        _uiState.update { it.copy(isListening = false, amplitude = 0f) }
    }

    override fun onCleared() {
        managePracticeSession.stop()
        super.onCleared()
    }
}
