package com.example.instrumenttrainer.data.classifier

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * Mel-spektrogram zgodny z [librosa.feature.melspectrogram] + [librosa.power_to_db]
 * używanym w PianoNotesNeuralNetwork/scripts/preprocess.py.
 */
internal class MelSpectrogramPreprocessor(
    private val sampleRate: Int,
    private val nMels: Int,
    private val nFft: Int,
    private val hopLength: Int,
    private val targetLength: Int,
    private val inputMean: Float,
    private val inputStd: Float,
) {
    private val melFilterBank = buildMelFilterBank()
    private val hannWindow = FloatArray(nFft) { index ->
        (0.5f * (1f - cos(2f * PI.toFloat() * index / nFft)))
    }

    fun preprocess(audio: FloatArray): FloatArray {
        val normalized = normalizeLength(audio)
        val padded = padCenter(normalized)
        val powerSpec = computePowerSpectrogram(padded)
        val mel = applyMelFilterBank(powerSpec)
        val melDb = powerToDb(mel)
        return standardize(melDb)
    }

    private fun normalizeLength(audio: FloatArray): FloatArray {
        return when {
            audio.size == targetLength -> audio
            audio.size > targetLength -> audio.copyOfRange(0, targetLength)
            else -> {
                val padded = FloatArray(targetLength)
                audio.copyInto(padded)
                padded
            }
        }
    }

    private fun padCenter(audio: FloatArray): FloatArray {
        val pad = nFft / 2
        val padded = FloatArray(audio.size + pad * 2)
        audio.copyInto(padded, destinationOffset = pad)
        return padded
    }

    private fun computePowerSpectrogram(paddedAudio: FloatArray): Array<FloatArray> {
        val frameCount = 1 + (paddedAudio.size - nFft) / hopLength
        val bins = nFft / 2 + 1
        val spectrogram = Array(frameCount) { FloatArray(bins) }

        val frame = FloatArray(nFft)
        val real = FloatArray(nFft)
        val imag = FloatArray(nFft)

        for (frameIndex in 0 until frameCount) {
            val start = frameIndex * hopLength
            for (i in 0 until nFft) {
                frame[i] = paddedAudio[start + i] * hannWindow[i]
            }
            fftInPlace(frame, real, imag)
            for (bin in 0 until bins) {
                val magnitudeSquared = real[bin] * real[bin] + imag[bin] * imag[bin]
                spectrogram[frameIndex][bin] = magnitudeSquared
            }
        }
        return spectrogram
    }

    private fun applyMelFilterBank(powerSpec: Array<FloatArray>): Array<FloatArray> {
        val frameCount = powerSpec.size
        val mel = Array(nMels) { FloatArray(frameCount) }
        for (melIndex in 0 until nMels) {
            val filter = melFilterBank[melIndex]
            for (frameIndex in 0 until frameCount) {
                var sum = 0f
                val spectrum = powerSpec[frameIndex]
                for (bin in spectrum.indices) {
                    sum += filter[bin] * spectrum[bin]
                }
                mel[melIndex][frameIndex] = sum
            }
        }
        return mel
    }

    private fun powerToDb(mel: Array<FloatArray>): FloatArray {
        var maxValue = Float.MIN_VALUE
        mel.forEach { row ->
            row.forEach { value ->
                if (value > maxValue) maxValue = value
            }
        }
        val ref = max(maxValue, 1e-10f)
        val flat = FloatArray(nMels * mel[0].size)
        var index = 0
        for (row in mel) {
            for (value in row) {
                flat[index++] = 10f * log10(max(value, 1e-10f) / ref)
            }
        }
        return flat
    }

    private fun standardize(melDb: FloatArray): FloatArray {
        val std = if (inputStd < 1e-6f) 1f else inputStd
        return FloatArray(melDb.size) { index ->
            (melDb[index] - inputMean) / std
        }
    }

    private fun buildMelFilterBank(): Array<FloatArray> {
        val bins = nFft / 2 + 1
        val fMax = sampleRate / 2f
        val melMin = hzToMel(0f)
        val melMax = hzToMel(fMax)
        val melPoints = FloatArray(nMels + 2) { index ->
            melMin + (melMax - melMin) * index / (nMels + 1)
        }
        val hzPoints = FloatArray(nMels + 2) { index -> melToHz(melPoints[index]) }
        val binPoints = IntArray(nMels + 2) { index ->
            min(
                bins - 1,
                max(0, ((nFft + 1) * hzPoints[index] / sampleRate).toInt()),
            )
        }

        return Array(nMels) { melIndex ->
            val filter = FloatArray(bins)
            val left = binPoints[melIndex]
            val center = binPoints[melIndex + 1]
            val right = binPoints[melIndex + 2]
            for (bin in left until center) {
                if (center != left) {
                    filter[bin] = (bin - left).toFloat() / (center - left)
                }
            }
            for (bin in center until right) {
                if (right != center) {
                    filter[bin] = (right - bin).toFloat() / (right - center)
                }
            }
            val enorm = 2f / (hzPoints[melIndex + 2] - hzPoints[melIndex])
            for (bin in filter.indices) {
                filter[bin] *= enorm
            }
            filter
        }
    }

    /** Slaney mel — domyślnie w librosa (htk=False). */
    private fun hzToMel(hz: Float): Float {
        val fSp = 200f / 3f
        val minLogHz = 1000f
        val minLogMel = minLogHz / fSp
        val logStep = ln(6.4f) / 27f
        return if (hz >= minLogHz) {
            minLogMel + ln(hz / minLogHz) / logStep
        } else {
            hz / fSp
        }
    }

    private fun melToHz(mel: Float): Float {
        val fSp = 200f / 3f
        val minLogHz = 1000f
        val minLogMel = minLogHz / fSp
        val logStep = ln(6.4f) / 27f
        return if (mel >= minLogMel) {
            minLogHz * exp(logStep * (mel - minLogMel))
        } else {
            fSp * mel
        }
    }

    private fun fftInPlace(
        input: FloatArray,
        real: FloatArray,
        imag: FloatArray,
    ) {
        val n = input.size
        real.indices.forEach { index -> real[index] = input[index] }
        imag.fill(0f)
        bitReversePermute(real, imag)

        var size = 2
        while (size <= n) {
            val halfSize = size / 2
            val angle = -2.0 * PI / size
            val stepReal = cos(angle).toFloat()
            val stepImag = kotlin.math.sin(angle).toFloat()
            for (start in 0 until n step size) {
                var omegaReal = 1f
                var omegaImag = 0f
                for (offset in 0 until halfSize) {
                    val evenIndex = start + offset
                    val oddIndex = evenIndex + halfSize
                    val oddReal = real[oddIndex] * omegaReal - imag[oddIndex] * omegaImag
                    val oddImag = real[oddIndex] * omegaImag + imag[oddIndex] * omegaReal
                    val evenReal = real[evenIndex]
                    val evenImag = imag[evenIndex]
                    real[evenIndex] = evenReal + oddReal
                    imag[evenIndex] = evenImag + oddImag
                    real[oddIndex] = evenReal - oddReal
                    imag[oddIndex] = evenImag - oddImag
                    val nextOmegaReal = omegaReal * stepReal - omegaImag * stepImag
                    omegaImag = omegaReal * stepImag + omegaImag * stepReal
                    omegaReal = nextOmegaReal
                }
            }
            size *= 2
        }
    }

    private fun bitReversePermute(real: FloatArray, imag: FloatArray) {
        val n = real.size
        var j = 0
        for (i in 1 until n) {
            var bit = n shr 1
            while (j and bit != 0) {
                j = j xor bit
                bit = bit shr 1
            }
            j = j xor bit
            if (i < j) {
                real[i] = real[j].also { real[j] = real[i] }
                imag[i] = imag[j].also { imag[j] = imag[i] }
            }
        }
    }
}
