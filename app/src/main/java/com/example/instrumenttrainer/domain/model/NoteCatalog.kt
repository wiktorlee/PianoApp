package com.example.instrumenttrainer.domain.model

object NoteCatalog {
    /** 12 klas zgodnych z modelem CNN (bez oktawy). */
    val PITCH_CLASS_NAMES = listOf(
        "C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B",
    )

    val NOTE_NAMES = PITCH_CLASS_NAMES

    const val MIN_OCTAVE = 3
    const val MAX_OCTAVE = 5
    const val DEFAULT_DETECTED_OCTAVE = 4

    fun defaultNote(): Note = Note(name = "C", octave = DEFAULT_DETECTED_OCTAVE)

    fun allNotes(): List<Note> = buildList {
        for (octave in MIN_OCTAVE..MAX_OCTAVE) {
            for (name in NOTE_NAMES) {
                add(Note(name = name, octave = octave))
            }
        }
    }
}
