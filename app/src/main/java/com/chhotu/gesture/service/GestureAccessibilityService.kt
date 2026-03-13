package com.chhotu.gesture.service

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.view.accessibility.AccessibilityEvent
import com.chhotu.gesture.engine.ContinuousScrollEngine

class GestureAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())

    companion object {
        var instance: GestureAccessibilityService? = null
            private set
        var isRunning: Boolean = false
            private set

        var scrollEngine: ContinuousScrollEngine? = null

        @Volatile
        var isUserTouchActive: Boolean = false

        private const val GESTURE_COOLDOWN_MS = 400L
        private var lastGestureTime: Long = 0L

        private fun canDispatch(): Boolean {
            if (isUserTouchActive) return false
            val now = System.currentTimeMillis()
            if (now - lastGestureTime < GESTURE_COOLDOWN_MS) return false
            lastGestureTime = now
            return true
        }
    }

    fun performBack(): Boolean {
        if (!canDispatch()) return false
        return performGlobalAction(GLOBAL_ACTION_BACK)
    }

    fun performHome(): Boolean {
        if (!canDispatch()) return false
        return performGlobalAction(GLOBAL_ACTION_HOME)
    }

    fun performRecentApps(): Boolean {
        if (!canDispatch()) return false
        return performGlobalAction(GLOBAL_ACTION_RECENTS)
    }

    fun performScroll(down: Boolean) {
        if (!canDispatch()) return

        val displayMetrics = resources.displayMetrics
        val centerX = displayMetrics.widthPixels / 2f
        val startY: Float
        val endY: Float

        if (down) {
            startY = displayMetrics.heightPixels * 0.6f
            endY = displayMetrics.heightPixels * 0.3f
        } else {
            startY = displayMetrics.heightPixels * 0.3f
            endY = displayMetrics.heightPixels * 0.6f
        }

        val path = Path().apply {
            moveTo(centerX, startY)
            lineTo(centerX, endY)
        }

        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 300))
            .build()

        dispatchGesture(gesture, null, null)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        when (event?.eventType) {
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_START -> {
                isUserTouchActive = true
                scrollEngine?.pauseScroll()
            }
            AccessibilityEvent.TYPE_TOUCH_INTERACTION_END -> {
                handler.removeCallbacksAndMessages(null)
                handler.postDelayed({
                    isUserTouchActive = false
                    scrollEngine?.resumeScroll()
                }, 300L)
            }
        }
    }

    override fun onInterrupt() {}

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        isRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
        instance = null
        isRunning = false
    }
}
