package com.chhotu.gesture.engine

import android.accessibilityservice.GestureDescription
import android.graphics.Path
import com.chhotu.gesture.domain.model.ScrollSpeed
import com.chhotu.gesture.service.GestureAccessibilityService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeout
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class ContinuousScrollEngine @Inject constructor() {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var scrollJob: Job? = null
    private var isPaused = AtomicBoolean(false)
    private var currentSpeed: ScrollSpeed = ScrollSpeed.MEDIUM

    private val _isScrolling = MutableStateFlow(false)
    val isScrolling: StateFlow<Boolean> = _isScrolling.asStateFlow()

    private val _currentScrollSpeed = MutableStateFlow(ScrollSpeed.MEDIUM)
    val currentScrollSpeed: StateFlow<ScrollSpeed> = _currentScrollSpeed.asStateFlow()

    fun startContinuousScroll(direction: Boolean) {
        scrollJob?.cancel()
        _isScrolling.value = true
        scrollJob = scope.launch {
            while (isActive) {
                if (!isPaused.get()) {
                    val service = GestureAccessibilityService.instance
                    if (service != null) {
                        performSingleScroll(service, direction, currentSpeed.distancePx)
                    }
                }
                delay(currentSpeed.intervalMs)
            }
        }
    }

    fun stopContinuousScroll() {
        scrollJob?.cancel()
        scrollJob = null
        _isScrolling.value = false
    }

    fun pauseScroll() {
        isPaused.set(true)
    }

    fun resumeScroll() {
        isPaused.set(false)
    }

    fun setSpeed(speed: ScrollSpeed) {
        currentSpeed = speed
        _currentScrollSpeed.value = speed
    }

    private suspend fun performSingleScroll(
        service: GestureAccessibilityService,
        down: Boolean,
        distance: Int
    ) {
        try {
            withTimeout(1000L) {
                suspendCancellableCoroutine { continuation ->
                    val displayMetrics = service.resources.displayMetrics
                    val centerX = displayMetrics.widthPixels / 2f
                    val centerY = displayMetrics.heightPixels / 2f

                    val startY: Float
                    val endY: Float
                    if (down) {
                        startY = centerY + distance / 2f
                        endY = centerY - distance / 2f
                    } else {
                        startY = centerY - distance / 2f
                        endY = centerY + distance / 2f
                    }

                    val path = Path().apply {
                        moveTo(centerX, startY)
                        lineTo(centerX, endY)
                    }

                    val gesture = GestureDescription.Builder()
                        .addStroke(GestureDescription.StrokeDescription(path, 0, 200))
                        .build()

                    service.dispatchGesture(
                        gesture,
                        object : android.accessibilityservice.AccessibilityService.GestureResultCallback() {
                            override fun onCompleted(gestureDescription: GestureDescription?) {
                                if (continuation.isActive) continuation.resume(Unit)
                            }

                            override fun onCancelled(gestureDescription: GestureDescription?) {
                                if (continuation.isActive) continuation.resume(Unit)
                            }
                        },
                        null
                    )
                }
            }
        } catch (_: Exception) {
        }
    }
}
