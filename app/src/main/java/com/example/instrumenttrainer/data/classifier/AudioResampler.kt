package com.example.instrumenttrainer.data.classifier

import kotlin.math.pow

internal object AudioResampler {

    fun resample(
        input: ShortArray,
        length: Int,
        sourceRate: Int,
        targetRate: Int,
    ): FloatArray {
        if (length <= 0) return FloatArray(0)
        if (sourceRate == targetRate) {
            return FloatArray(length) { index ->
                input[index].toFloat() / Short.MAX_VALUE
            }
        }

        val ratio = targetRate.toFloat() / sourceRate.toFloat()
        val outputLength = (length * ratio).toInt().coerceAtLeast(1)
        val output = FloatArray(outputLength)
        for (i in 0 until outputLength) {
            val sourcePosition = i / ratio
            val leftIndex = sourcePosition.toInt().coerceIn(0, length - 1)
            val rightIndex = (leftIndex + 1).coerceAtMost(length - 1)
            val fraction = sourcePosition - leftIndex
            val left = input[leftIndex].toFloat()
            val right = input[rightIndex].toFloat()
            val sample = left + (right - left) * fraction
            output[i] = (sample / Short.MAX_VALUE).coerceIn(-1f, 1f)
        }
        return output
    }

    /** Negative [semitones] lowers perceived pitch (compensates for sharper source tuning). */
    fun pitchShift(input: FloatArray, semitones: Float): FloatArray {
        if (input.isEmpty() || kotlin.math.abs(semitones) < 0.01f) {
            return input
        }
        val ratio = 2.0.pow(semitones / 12.0).toFloat()
        val stretchedLength = (input.size / ratio).toInt().coerceAtLeast(1)
        val stretched = FloatArray(stretchedLength)
        for (i in 0 until stretchedLength) {
            val sourcePosition = i * ratio
            val leftIndex = sourcePosition.toInt().coerceIn(0, input.size - 1)
            val rightIndex = (leftIndex + 1).coerceAtMost(input.size - 1)
            val fraction = sourcePosition - leftIndex
            val sample = input[leftIndex] + (input[rightIndex] - input[leftIndex]) * fraction
            stretched[i] = sample.coerceIn(-1f, 1f)
        }
        return if (stretched.size >= input.size) {
            stretched.copyOfRange(0, input.size)
        } else {
            FloatArray(input.size).also { out -> stretched.copyInto(out) }
        }
    }
}
