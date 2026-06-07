package com.example.instrumenttrainer.data.classifier

import org.json.JSONObject

data class ModelMetadata(
    val modelFile: String,
    val sampleRate: Int,
    val durationSec: Float,
    val nMels: Int,
    val nFft: Int,
    val hopLength: Int,
    val inputMean: Float,
    val inputStd: Float,
    val classLabels: List<String>,
    val pitchClassOffset: Int = 0,
    /** Przesunięcie wysokości dźwięku przed mel (ujemne = obniż). */
    val pitchShiftSemitones: Float = 0f,
) {
    val targetSampleCount: Int
        get() = (sampleRate * durationSec).toInt()

    companion object {
        const val DEFAULT_SAMPLE_RATE = 22_050
        private const val ASSET_FILE = "model_metadata.json"

        fun fromAssets(assets: android.content.res.AssetManager): ModelMetadata? = try {
            assets.open(ASSET_FILE).bufferedReader().use { reader ->
                fromJson(JSONObject(reader.readText()))
            }
        } catch (_: Exception) {
            null
        }

        fun fromJson(json: JSONObject): ModelMetadata {
            val labelsJson = json.getJSONArray("classLabels")
            val labels = buildList {
                for (i in 0 until labelsJson.length()) {
                    add(labelsJson.getString(i))
                }
            }
            return ModelMetadata(
                modelFile = json.getString("modelFile"),
                sampleRate = json.getInt("sampleRate"),
                durationSec = json.getDouble("durationSec").toFloat(),
                nMels = json.getInt("nMels"),
                nFft = json.getInt("nFft"),
                hopLength = json.getInt("hopLength"),
                inputMean = json.getDouble("inputMean").toFloat(),
                inputStd = json.getDouble("inputStd").toFloat(),
                classLabels = labels,
                pitchClassOffset = json.optInt("pitchClassOffset", 0),
                pitchShiftSemitones = json.optDouble("pitchShiftSemitones", 0.0).toFloat(),
            )
        }
    }
}
