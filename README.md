# InstrumentTrainer

Aplikacja Android do testowania rozpoznawania nut z mikrofonu. Użytkownik gra znaną nutę na instrumencie, wskazuje ją w aplikacji i porównuje z odczytem modelu (TFLite z projektu PianoNotesNeuralNetwork; fallback: mock).

Stack: Kotlin, Jetpack Compose, MVVM, Hilt, Room, Coroutines.

## Struktura pakietów

```
com.example.instrumenttrainer/
├── di/
├── domain/
│   ├── model/
│   ├── classifier/
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
    ├── recognition/
    ├── journal/
    ├── settings/
    └── components/
```

## Ekrany

- **PracticeRoom** — krótki podgląd nasłuchu, wybór nuty przez użytkownika, odczyt modelu
- **RecognitionTest** — główny tryb testu sieci: sesja, zapis pojedynczych prób, statystyki na żywo
- **ProgressJournal** — historia sesji i celność z bazy
- **Settings** — placeholder (skala, czułość mikrofonu)

## Pipeline (v1)

1. Użytkownik zna nutę, którą zagrał na pianinie.
2. W aplikacji wybiera: „Zagrałem: X”.
3. Model (mock / TFLite) zwraca: „Wykryto: Y”.
4. Porównanie X vs Y; opcjonalny zapis próby do Room.

Quiz „zagraj nutę X od aplikacji” nie jest w v1 — odłożony.

## Etapy prac

### Etap 0 — fundament (zrobione)

- Gradle: Hilt, Room (KSP), Navigation Compose, ViewModel, Coroutines
- `Application` z `@HiltAndroidApp`, manifest `RECORD_AUDIO`, nawigacja

### Etap 1 — audio i klasyfikacja (zrobione)

- `NoteClassifier`, `MockNoteClassifier`, `AudioCaptureManager`
- `AudioPracticeRepository` łączy capture z classifierem

### Etap 2 — PracticeRoom (zrobione, doprecyzowany)

- Uprawnienie mikrofonu, ręczny start/stop nasłuchu
- Użytkownik wybiera nutę (`userPlayedNote`), wyświetlana `detectedNote`
- Porównanie i zapis próby na żądanie (nie przy każdym ticku mocka)

### Etap 3 — baza i ProgressJournal (zrobione)

- Room: sesje, próby (`target` = nuta użytkownika, `detected` = output modelu)
- Lista sesji i agregowana celność

### Etap 4 — RecognitionTest (zrobione)

- Dedykowany ekran testu modelu (zakładka „Test”)
- Sesja testowa, licznik prób i trafień w UI
- Ten sam pipeline audio; zapis do Room po „Zapisz próbę”

### Etap 5 — Settings (odłożony)

- Skala instrumentu, czułość mikrofonu (mock)
- DataStore lub Room

### Etap 6 — TensorFlow Lite (zrobione)

- `TfliteNoteClassifier` + preprocessing mel (zgodny z `preprocess.py`)
- Hilt: TFLite gdy jest `assets/piano_cnn.tflite`, inaczej `MockNoteClassifier`
- `assets/model_metadata.json` — parametry wejścia i mean/std z treningu
- Selektor 12 klas (C…B), porównanie po wysokości (bez oktawy w modelu)

**Podpięcie modelu:**

1. W `PianoNotesNeuralNetwork`: `python scripts/train.py` (powstaje `models/piano_cnn.tflite`).
2. Eksport metadanych: `python scripts/export_android_metadata.py --out ../PianoApp/app/src/main/assets/model_metadata.json`
3. Skopiuj `models/piano_cnn.tflite` → `PianoApp/app/src/main/assets/piano_cnn.tflite`
4. Zbuduj i uruchom aplikację na urządzeniu z mikrofonem.

## Poza zakresem (na później)

- Tryb quizu („zagraj nutę X”)
- Nagrywanie w tle / Foreground Service
- Eksport danych, widgety
