# Pliki modelu TFLite

Skopiuj z projektu `PianoNotesNeuralNetwork` po treningu:

1. `models/piano_cnn.tflite` → `app/src/main/assets/piano_cnn.tflite`
2. Zaktualizuj `inputMean` i `inputStd` w `model_metadata.json` (wartości z treningu).

## Eksport mean/std z Pythona

W katalogu `PianoNotesNeuralNetwork` (po `preprocess.py`):

```powershell
python -c "
from pathlib import Path
import json
from scripts.dataset_split import load_train_val_split
split = load_train_val_split(Path('data_processed/dataset.npz'))
meta = {
    'modelFile': 'piano_cnn.tflite',
    'sampleRate': 22050,
    'durationSec': 1.0,
    'nMels': 128,
    'nFft': 2048,
    'hopLength': 512,
    'inputMean': split.input_mean,
    'inputStd': split.input_std,
    'classLabels': ['C','C#','D','D#','E','F','F#','G','G#','A','A#','B'],
}
print(json.dumps(meta, indent=2))
"
```

Bez pliku `.tflite` aplikacja uruchamia się z `MockNoteClassifier` (fallback).
