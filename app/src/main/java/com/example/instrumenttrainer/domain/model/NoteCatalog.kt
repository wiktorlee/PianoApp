package com.example.instrumenttrainer.domain.model

object NoteCatalog {
    val NOTE_NAMES = listOf("C", "D", "E", "F", "G", "A", "B")
    const val MIN_OCTAVE = 3
    const val MAX_OCTAVE = 5

    fun defaultNote(): Note = Note(name = "C", octave = 4)

    fun allNotes(): List<Note> = buildList {
        for (octave in MIN_OCTAVE..MAX_OCTAVE) {
            for (name in NOTE_NAMES) {
                add(Note(name = name, octave = octave))
            }
        }
    }
}
