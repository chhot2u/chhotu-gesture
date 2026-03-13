package com.chhotu.gesture.engine

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageProxy
import com.chhotu.gesture.domain.model.HandLandmark
import com.chhotu.gesture.domain.model.Handedness
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarker
import com.google.mediapipe.tasks.vision.handlandmarker.HandLandmarkerResult

interface HandTrackerCallback {
    fun onHandDetected(landmarks: List<HandLandmark>, handedness: Handedness)
    fun onNoHandDetected()
}

class HandTracker {

    private var handLandmarker: HandLandmarker? = null
    private var callback: HandTrackerCallback? = null

    fun setCallback(callback: HandTrackerCallback) {
        this.callback = callback
    }

    fun initialize(context: Context) {
        val baseOptions = BaseOptions.builder()
            .setModelAssetPath("hand_landmarker.task")
            .setDelegate(Delegate.GPU)
            .build()

        val options = HandLandmarker.HandLandmarkerOptions.builder()
            .setBaseOptions(baseOptions)
            .setRunningMode(RunningMode.LIVE_STREAM)
            .setNumHands(1)
            .setMinHandDetectionConfidence(0.5f)
            .setMinHandPresenceConfidence(0.5f)
            .setMinTrackingConfidence(0.5f)
            .setResultListener { result, _ -> handleResult(result) }
            .setErrorListener { error -> error.printStackTrace() }
            .build()

        handLandmarker = HandLandmarker.createFromOptions(context, options)
    }

    fun processImage(imageProxy: ImageProxy) {
        val bitmap = imageProxyToBitmap(imageProxy) ?: run {
            imageProxy.close()
            return
        }

        val mpImage = BitmapImageBuilder(bitmap).build()
        val timestampMs = imageProxy.imageInfo.timestamp / 1000

        handLandmarker?.detectAsync(mpImage, timestampMs)
        imageProxy.close()
    }

    fun close() {
        handLandmarker?.close()
        handLandmarker = null
    }

    private fun handleResult(result: HandLandmarkerResult) {
        if (result.landmarks().isEmpty()) {
            callback?.onNoHandDetected()
            return
        }

        val mpLandmarks = result.landmarks()[0]
        val landmarks = mpLandmarks.mapIndexed { index, lm ->
            HandLandmark(
                index = index,
                x = lm.x(),
                y = lm.y(),
                z = lm.z()
            )
        }

        val handedness = if (result.handednesses().isNotEmpty() &&
            result.handednesses()[0].isNotEmpty()
        ) {
            val label = result.handednesses()[0][0].categoryName()
            if (label.equals("Left", ignoreCase = true)) Handedness.LEFT else Handedness.RIGHT
        } else {
            Handedness.RIGHT
        }

        callback?.onHandDetected(landmarks, handedness)
    }

    @Suppress("UnsafeOptInUsageError")
    private fun imageProxyToBitmap(imageProxy: ImageProxy): Bitmap? {
        val image = imageProxy.image ?: return null
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)
        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = android.graphics.YuvImage(
            nv21,
            android.graphics.ImageFormat.NV21,
            image.width,
            image.height,
            null
        )

        val out = java.io.ByteArrayOutputStream()
        yuvImage.compressToJpeg(
            android.graphics.Rect(0, 0, image.width, image.height),
            100,
            out
        )

        val bytes = out.toByteArray()
        val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        return if (rotationDegrees != 0) {
            val matrix = Matrix()
            matrix.postRotate(rotationDegrees.toFloat())
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        } else {
            bitmap
        }
    }
}
