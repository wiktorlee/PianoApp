package com.example.instrumenttrainer.data.audio

data class AudioFrame(
    val buffer: ShortArray,
    val length: Int,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as AudioFrame
        if (length != other.length) return false
        return buffer.contentEquals(other.buffer)
    }

    override fun hashCode(): Int {
        var result = buffer.contentHashCode()
        result = 31 * result + length
        return result
    }
}
