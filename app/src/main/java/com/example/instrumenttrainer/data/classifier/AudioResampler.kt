package com.example.instrumenttrainer.data.classifier

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

        val ratio = targetRate.toDouble() / sourceRate.toDouble()
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
}
