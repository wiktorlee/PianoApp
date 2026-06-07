package com.example.instrumenttrainer.data.classifier

import android.content.Context
import android.util.Log
import com.example.instrumenttrainer.domain.classifier.NoteClassifier
import com.example.instrumenttrainer.domain.model.Note
import com.example.instrumenttrainer.domain.model.NoteCatalog
import dagger.hilt.android.qualifiers.ApplicationContext
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
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TfliteNoteClassifier @Inject constructor(
    @ApplicationContext private val context: Context,
) : NoteClassifier {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private val _detectedNotes = MutableSharedFlow<Note>(replay = 1)
    override val detectedNotes: SharedFlow<Note> = _detectedNotes.asSharedFlow()

    private var interpreter: Interpreter? = null
    private var metadata: ModelMetadata? = null
    private var preprocessor: MelSpectrogramPreprocessor? = null
    private var inputBuffer: ByteBuffer? = null
    private var outputBuffer: Array<FloatArray>? = null

    private val audioRingBuffer = ArrayList<Float>()
    private var inferenceJob: Job? = null
    private val recentPredictions = ArrayDeque<Int>()

    val isAvailable: Boolean
        get() = interpreter != null

    init {
        loadModel()
    }

    override fun start() {
        if (!isAvailable) return
        audioRingBuffer.clear()
        recentPredictions.clear()
        inferenceJob?.cancel()
        inferenceJob = scope.launch {
            while (isActive) {
                delay(INFERENCE_INTERVAL_MS)
                runInference()
            }
        }
    }

    override fun stop() {
        inferenceJob?.cancel()
        inferenceJob = null
        audioRingBuffer.clear()
        recentPredictions.clear()
    }

    override fun onAudioFrame(buffer: ShortArray, length: Int, sampleRate: Int) {
        if (!isAvailable || length <= 0) return
        val resampled = AudioResampler.resample(
            input = buffer,
            length = length,
            sourceRate = sampleRate,
            targetRate = metadata?.sampleRate ?: ModelMetadata.DEFAULT_SAMPLE_RATE,
        )
        appendSamples(resampled)
    }

    private fun loadModel() {
        val loadedMetadata = ModelMetadata.fromAssets(context.assets) ?: run {
            Log.w(TAG, "Brak model_metadata.json w assets — TFLite wylaczone.")
            return
        }
        metadata = loadedMetadata

        try {
            val modelBuffer = loadModelBuffer(loadedMetadata.modelFile)
            interpreter = Interpreter(modelBuffer)
        } catch (exception: Exception) {
            Log.w(
                TAG,
                "Brak ${loadedMetadata.modelFile} w assets — TFLite wylaczone. Skopiuj model z PianoNotesNeuralNetwork/models/.",
                exception,
            )
            metadata = null
            return
        }

        preprocessor = MelSpectrogramPreprocessor(
            sampleRate = loadedMetadata.sampleRate,
            nMels = loadedMetadata.nMels,
            nFft = loadedMetadata.nFft,
            hopLength = loadedMetadata.hopLength,
            targetLength = loadedMetadata.targetSampleCount,
            inputMean = loadedMetadata.inputMean,
            inputStd = loadedMetadata.inputStd,
        )

        val frameCount = 1 + loadedMetadata.targetSampleCount / loadedMetadata.hopLength
        val inputSize = loadedMetadata.nMels * frameCount * INPUT_CHANNELS
        inputBuffer = ByteBuffer.allocateDirect(inputSize * Float.SIZE_BYTES).apply {
            order(ByteOrder.nativeOrder())
        }
        outputBuffer = Array(1) { FloatArray(loadedMetadata.classLabels.size) }
        Log.i(TAG, "Zaladowano ${loadedMetadata.modelFile}")
    }

    private fun appendSamples(samples: FloatArray) {
        synchronized(audioRingBuffer) {
            samples.forEach { sample ->
                audioRingBuffer.add(sample)
            }
            val maxSize = metadata?.targetSampleCount ?: ModelMetadata.DEFAULT_SAMPLE_RATE
            while (audioRingBuffer.size > maxSize) {
                audioRingBuffer.removeAt(0)
            }
        }
    }

    private fun loadModelBuffer(fileName: String): MappedByteBuffer {
        context.assets.openFd(fileName).use { asset ->
            FileInputStream(asset.fileDescriptor).channel.use { channel ->
                return channel.map(
                    FileChannel.MapMode.READ_ONLY,
                    asset.startOffset,
                    asset.declaredLength,
                )
            }
        }
    }

    private fun runInference() {
        val currentMetadata = metadata ?: return
        val currentPreprocessor = preprocessor ?: return
        val currentInterpreter = interpreter ?: return
        val input = inputBuffer ?: return
        val output = outputBuffer ?: return

        val targetCount = currentMetadata.targetSampleCount
        val window = synchronized(audioRingBuffer) {
            if (audioRingBuffer.size < targetCount) {
                return
            }
            FloatArray(targetCount) { index ->
                audioRingBuffer[audioRingBuffer.size - targetCount + index]
            }
        }

        val rms = computeRms(window)
        if (rms < MIN_SIGNAL_RMS) {
            return
        }

        val pitchShifted = AudioResampler.pitchShift(window, currentMetadata.pitchShiftSemitones)
        val features = currentPreprocessor.preprocess(pitchShifted)
        if (features.size != input.capacity() / Float.SIZE_BYTES) {
            Log.w(TAG, "Niezgodny rozmiar cech: ${features.size}")
            return
        }

        input.rewind()
        features.forEach { value -> input.putFloat(value) }
        input.rewind()

        currentInterpreter.run(input, output)
        val probabilities = output[0]
        val rawIndex = stabilizedClassIndex(probabilities)
        if (probabilities[rawIndex] < MIN_CONFIDENCE) {
            return
        }
        val correctedIndex = normalizeClassIndex(rawIndex + currentMetadata.pitchClassOffset)
        val label = currentMetadata.classLabels.getOrNull(correctedIndex) ?: return
        val note = Note(name = label, octave = NoteCatalog.DEFAULT_DETECTED_OCTAVE)
        scope.launch {
            _detectedNotes.emit(note)
        }
    }

    private fun normalizeClassIndex(index: Int): Int {
        val size = metadata?.classLabels?.size ?: 12
        return ((index % size) + size) % size
    }

    private fun stabilizedClassIndex(probabilities: FloatArray): Int {
        val bestIndex = probabilities.indices.maxByOrNull { index -> probabilities[index] } ?: 0
        recentPredictions.addLast(bestIndex)
        while (recentPredictions.size > STABILIZATION_WINDOW) {
            recentPredictions.removeFirst()
        }
        return recentPredictions.groupingBy { it }.eachCount().maxByOrNull { it.value }?.key
            ?: bestIndex
    }

    private fun computeRms(window: FloatArray): Float {
        if (window.isEmpty()) return 0f
        var sum = 0.0
        for (sample in window) {
            val value = sample.toDouble()
            sum += value * value
        }
        return kotlin.math.sqrt(sum / window.size).toFloat()
    }

    companion object {
        private const val TAG = "TfliteNoteClassifier"
        private const val INFERENCE_INTERVAL_MS = 400L
        private const val INPUT_CHANNELS = 1
        private const val STABILIZATION_WINDOW = 5
        private const val MIN_SIGNAL_RMS = 0.015f
        private const val MIN_CONFIDENCE = 0.35f
    }
}
