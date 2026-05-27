package com.example.instrumenttrainer.data.audio

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AudioCaptureManager @Inject constructor() {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    private var audioRecord: AudioRecord? = null
    private var captureJob: Job? = null

    var sampleRate: Int = 0
        private set

    private val _frames = MutableSharedFlow<AudioFrame>(extraBufferCapacity = 1)
    val frames: SharedFlow<AudioFrame> = _frames.asSharedFlow()

    fun start() {
        if (captureJob?.isActive == true) return

        val (record, bufferSizeBytes, selectedRate) = buildInitializedAudioRecord() ?: return
        sampleRate = selectedRate
        audioRecord = record
        record.startRecording()

        val shortBufferSize = bufferSizeBytes / BYTES_PER_FRAME
        captureJob = scope.launch {
            val buffer = ShortArray(shortBufferSize)
            while (isActive) {
                val read = record.read(buffer, 0, buffer.size)
                if (read > 0) {
                    _frames.emit(AudioFrame(buffer.copyOf(read), read, selectedRate))
                }
            }
        }
    }

    fun stop() {
        captureJob?.cancel()
        captureJob = null
        audioRecord?.apply {
            try {
                stop()
            } catch (_: IllegalStateException) {
            }
            release()
        }
        audioRecord = null
        sampleRate = 0
    }

    /**
     * 44.1 kHz is not supported on every device/emulator for MIC capture.
     * Try common rates in order until [AudioRecord] initializes.
     */
    private fun buildInitializedAudioRecord(): Triple<AudioRecord, Int, Int>? {
        for (rate in PREFERRED_SAMPLE_RATES) {
            val minBuf = AudioRecord.getMinBufferSize(
                rate,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
            )
            if (minBuf <= 0) continue
            val bufferSizeBytes = minBuf * 2
            val record = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                rate,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSizeBytes,
            )
            if (record.state == AudioRecord.STATE_INITIALIZED) {
                return Triple(record, bufferSizeBytes, rate)
            }
            record.release()
        }
        return null
    }

    companion object {
        private val PREFERRED_SAMPLE_RATES = intArrayOf(22_050, 16_000, 44_100)
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        private const val BYTES_PER_FRAME = 2
    }
}
