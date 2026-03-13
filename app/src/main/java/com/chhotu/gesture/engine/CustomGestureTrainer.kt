package com.chhotu.gesture.engine

import com.chhotu.gesture.domain.model.HandLandmark
import org.json.JSONArray
import org.json.JSONObject
import javax.inject.Inject
import kotlin.math.sqrt

class CustomGestureTrainer @Inject constructor(
    private val normalizer: LandmarkNormalizer
) {

    data class TrainingSample(
        val landmarks: List<HandLandmark>,
        val gestureId: String
    )

    private val samples = mutableListOf<TrainingSample>()
    private var trained = false
    private val k = 5
    private val minSamplesPerGesture = 10

    fun addSample(sample: TrainingSample) {
        samples.add(sample)
        trained = false
    }

    fun train(): Boolean {
        val grouped = samples.groupBy { it.gestureId }
        val allHaveEnough = grouped.all { (_, s) -> s.size >= minSamplesPerGesture }

        if (grouped.isEmpty() || !allHaveEnough) return false

        trained = true
        return true
    }

    fun predict(landmarks: List<HandLandmark>): Pair<String, Float>? {
        if (!trained || samples.isEmpty()) return null

        val normalized = normalizer.normalize(landmarks)

        val distances = samples.map { sample ->
            val normalizedSample = normalizer.normalize(sample.landmarks)
            val dist = euclideanDistance(normalized, normalizedSample)
            Pair(sample.gestureId, dist)
        }.sortedBy { it.second }

        val nearest = distances.take(k)
        if (nearest.isEmpty()) return null

        val votes = nearest.groupBy { it.first }
        val bestGesture = votes.maxByOrNull { it.value.size } ?: return null

        val voteCount = bestGesture.value.size.toFloat()
        val avgDist = bestGesture.value.map { it.second }.average().toFloat()
        val confidence = (voteCount / k) * (1f / (1f + avgDist))

        return Pair(bestGesture.key, confidence.coerceIn(0f, 1f))
    }

    fun exportModel(): String {
        val jsonArray = JSONArray()
        for (sample in samples) {
            val sampleObj = JSONObject()
            sampleObj.put("gestureId", sample.gestureId)

            val landmarksArray = JSONArray()
            for (lm in sample.landmarks) {
                val lmObj = JSONObject()
                lmObj.put("index", lm.index)
                lmObj.put("x", lm.x.toDouble())
                lmObj.put("y", lm.y.toDouble())
                lmObj.put("z", lm.z.toDouble())
                landmarksArray.put(lmObj)
            }
            sampleObj.put("landmarks", landmarksArray)
            jsonArray.put(sampleObj)
        }
        return jsonArray.toString()
    }

    fun importModel(json: String): Boolean {
        return try {
            val jsonArray = JSONArray(json)
            val imported = mutableListOf<TrainingSample>()

            for (i in 0 until jsonArray.length()) {
                val sampleObj = jsonArray.getJSONObject(i)
                val gestureId = sampleObj.getString("gestureId")
                val landmarksArray = sampleObj.getJSONArray("landmarks")

                val landmarks = mutableListOf<HandLandmark>()
                for (j in 0 until landmarksArray.length()) {
                    val lmObj = landmarksArray.getJSONObject(j)
                    landmarks.add(
                        HandLandmark(
                            index = lmObj.getInt("index"),
                            x = lmObj.getDouble("x").toFloat(),
                            y = lmObj.getDouble("y").toFloat(),
                            z = lmObj.getDouble("z").toFloat()
                        )
                    )
                }
                imported.add(TrainingSample(landmarks, gestureId))
            }

            samples.clear()
            samples.addAll(imported)
            trained = false
            true
        } catch (e: Exception) {
            false
        }
    }

    private fun euclideanDistance(a: List<HandLandmark>, b: List<HandLandmark>): Float {
        val size = minOf(a.size, b.size)
        var sum = 0f
        for (i in 0 until size) {
            val dx = a[i].x - b[i].x
            val dy = a[i].y - b[i].y
            val dz = a[i].z - b[i].z
            sum += dx * dx + dy * dy + dz * dz
        }
        return sqrt(sum)
    }
}
