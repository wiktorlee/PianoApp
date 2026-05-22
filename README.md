# InstrumentTrainer

Aplikacja Android do treningu gry na instrumencie. Przechwytuje dźwięk z mikrofonu, wykrywa nutę (na razie mock) i pokazuje informację zwrotną użytkownikowi.

Stack: Kotlin, Jetpack Compose, MVVM, Hilt, Room, Coroutines.

## Struktura pakietów

```
com.example.instrumenttrainer/
├── di/
├── domain/
│   ├── model/
│   ├── repository/
│   └── usecase/
├── data/
│   ├── audio/
│   ├── classifier/
│   ├── local/
│   └── repository/
└── presentation/
    ├── navigation/
    ├── practice/
    ├── journal/
    ├── challenge/
    ├── settings/
    └── components/
```

## Ekrany

- **PracticeRoom** — mikrofon, wizualizator, wykryta nuta, porównanie z nutą docelową
- **ProgressJournal** — historia sesji i statystyki z bazy
- **ChallengeMode** — zadania typu „zagraj nutę X”
- **Settings** — skala instrumentu, ustawienia mikrofonu (mock)

## Etapy prac

### Etap 0 — fundament (zrobione)

- Gradle: Hilt, Room (KSP), Navigation Compose, ViewModel, Coroutines
- Klasa `Application` z `@HiltAndroidApp`
- Manifest: `RECORD_AUDIO`
- Nawigacja między czterema ekranami (placeholdery)
- Pakiet / `applicationId`: `com.example.instrumenttrainer`

### Etap 1 — audio i klasyfikacja (mock) (zrobione)

- `NoteClassifier` (interfejs w domain)
- `MockNoteClassifier` — losowa nuta co ~500 ms przez Flow
- `AudioCaptureManager` — `AudioRecord`, pętla na `Dispatchers.Default`
- Repozytorium / use case łączące capture z classifierem
- Zatrzymanie capture przy zamknięciu ViewModelu

### Etap 2 — PracticeRoom (zrobione)

- Uprawnienie `RECORD_AUDIO` w runtime
- `PracticeRoomViewModel` + Compose UI
- Wyświetlanie wykrytej nuty, kolor OK / błąd względem nuty docelowej
- Prosty wizualizator amplitudy (`LinearProgressIndicator` + wartość RMS)

### Etap 3 — baza i ProgressJournal (zrobione)

- Room: sesje ćwiczeń, pojedyncze próby (nuty wykryta / docelowa / poprawność)
- Zapis z Practice Room
- Lista sesji, accuracy, prosty wykres lub podsumowanie (`LinearProgressIndicator`)

### Etap 4 — ChallengeMode

- Maszyna stanów: pytanie → nasłuch → ocena → następne zadanie
- Ten sam pipeline audio co w Practice Room
- Punkty / licznik; zapis prób do Room

### Etap 5 — Settings

- Skala (np. C-dur, chromatyczna) — DataStore lub Room
- Slider czułości (mock; na razie bez wpływu na classifier)
- Skala ogranicza nuty w trybie Challenge

### Etap 6 — TensorFlow Lite

- `TfliteNoteClassifier` implementujący `NoteClassifier`
- Podmiana mocka w DI (module Hilt)
- Pre/post-processing bufora audio według modelu

## Poza zakresem (na później)

- Nagrywanie w tle / Foreground Service
- Analiza pitch bez modelu
- Eksport danych, widgety
