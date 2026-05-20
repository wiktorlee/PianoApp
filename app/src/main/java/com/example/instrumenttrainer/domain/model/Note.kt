package com.example.instrumenttrainer.domain.model

data class Note(
    val name: String,
    val octave: Int,
) {
    val displayName: String get() = "$name$octave"
}
