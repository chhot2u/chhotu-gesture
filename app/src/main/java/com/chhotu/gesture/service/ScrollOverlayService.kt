package com.chhotu.gesture.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import com.chhotu.gesture.R
import com.chhotu.gesture.domain.model.ScrollSpeed

class ScrollOverlayService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var layoutParams: WindowManager.LayoutParams? = null

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    var onSpeedChanged: ((ScrollSpeed) -> Unit)? = null
    var onCloseClicked: (() -> Unit)? = null

    companion object {
        var instance: ScrollOverlayService? = null
            private set

        private const val ACTION_UPDATE_SPEED = "com.chhotu.gesture.UPDATE_SPEED"
        private const val ACTION_UPDATE_STATUS = "com.chhotu.gesture.UPDATE_STATUS"
        private const val EXTRA_SPEED = "speed"
        private const val EXTRA_STATUS = "status"
        private const val EXTRA_DIRECTION_UP = "direction_up"

        fun show(context: Context, directionUp: Boolean = true) {
            val intent = Intent(context, ScrollOverlayService::class.java)
            intent.putExtra(EXTRA_DIRECTION_UP, directionUp)
            context.startService(intent)
        }

        fun hide(context: Context) {
            context.stopService(Intent(context, ScrollOverlayService::class.java))
        }

        fun updateSpeed(context: Context, speed: String) {
            val intent = Intent(context, ScrollOverlayService::class.java)
            intent.action = ACTION_UPDATE_SPEED
            intent.putExtra(EXTRA_SPEED, speed)
            context.startService(intent)
        }

        fun updateStatus(context: Context, status: String) {
            val intent = Intent(context, ScrollOverlayService::class.java)
            intent.action = ACTION_UPDATE_STATUS
            intent.putExtra(EXTRA_STATUS, status)
            context.startService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        createOverlay()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_UPDATE_SPEED -> {
                val speed = intent.getStringExtra(EXTRA_SPEED) ?: "M"
                highlightSpeedButton(speed)
            }
            ACTION_UPDATE_STATUS -> {
                val status = intent.getStringExtra(EXTRA_STATUS) ?: "SCROLLING"
                overlayView?.findViewById<TextView>(R.id.tv_status)?.text = status
            }
            else -> {
                val directionUp = intent?.getBooleanExtra(EXTRA_DIRECTION_UP, true) ?: true
                updateDirection(directionUp)
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        if (overlayView != null) {
            windowManager?.removeView(overlayView)
            overlayView = null
        }
        instance = null
        super.onDestroy()
    }

    private fun createOverlay() {
        overlayView = LayoutInflater.from(this).inflate(R.layout.scroll_overlay_layout, null)

        layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            y = 100
        }

        setupDrag()
        setupButtons()

        windowManager?.addView(overlayView, layoutParams)

        highlightSpeedButton("M")
    }

    private fun setupDrag() {
        overlayView?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = layoutParams?.x ?: 0
                    initialY = layoutParams?.y ?: 0
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    layoutParams?.x = initialX + (event.rawX - initialTouchX).toInt()
                    layoutParams?.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager?.updateViewLayout(overlayView, layoutParams)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupButtons() {
        overlayView?.apply {
            findViewById<View>(R.id.btn_slow)?.setOnClickListener {
                highlightSpeedButton("S")
                onSpeedChanged?.invoke(ScrollSpeed.SLOW)
            }
            findViewById<View>(R.id.btn_medium)?.setOnClickListener {
                highlightSpeedButton("M")
                onSpeedChanged?.invoke(ScrollSpeed.MEDIUM)
            }
            findViewById<View>(R.id.btn_fast)?.setOnClickListener {
                highlightSpeedButton("F")
                onSpeedChanged?.invoke(ScrollSpeed.FAST)
            }
            findViewById<View>(R.id.btn_close)?.setOnClickListener {
                onCloseClicked?.invoke()
                stopSelf()
            }
        }
    }

    private fun highlightSpeedButton(speed: String) {
        overlayView?.apply {
            findViewById<View>(R.id.btn_slow)?.isSelected = (speed == "S")
            findViewById<View>(R.id.btn_medium)?.isSelected = (speed == "M")
            findViewById<View>(R.id.btn_fast)?.isSelected = (speed == "F")
        }
    }

    private fun updateDirection(up: Boolean) {
        overlayView?.findViewById<ImageView>(R.id.iv_direction)?.let {
            it.rotation = if (up) 0f else 180f
        }
    }

    fun showPaused() {
        overlayView?.findViewById<TextView>(R.id.tv_status)?.text = "PAUSED"
    }

    fun showScrolling() {
        overlayView?.findViewById<TextView>(R.id.tv_status)?.text = "SCROLLING"
    }
}
