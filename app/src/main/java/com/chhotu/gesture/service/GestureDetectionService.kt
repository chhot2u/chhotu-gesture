package com.chhotu.gesture.service

import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.IBinder
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.lifecycleScope
import com.chhotu.gesture.engine.GestureDetector
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class GestureDetectionService : LifecycleService() {

    @Inject
    lateinit var notificationHelper: NotificationHelper

    @Inject
    lateinit var gestureDetector: GestureDetector

    private var detectionJob: Job? = null
    private var isIdle = false
    private var lastHandDetectedTime = System.currentTimeMillis()

    private val activeFpsDelay = 1000L / 30
    private val idleFpsDelay = 1000L / 5
    private val idleTimeoutMs = 5000L

    companion object {
        fun start(context: Context) {
            val intent = Intent(context, GestureDetectionService::class.java)
            context.startForegroundService(intent)
        }

        fun stop(context: Context) {
            val intent = Intent(context, GestureDetectionService::class.java)
            context.stopService(intent)
        }
    }

    override fun onCreate() {
        super.onCreate()
        val notification = notificationHelper.buildDetectionNotification()
        ServiceCompat.startForeground(
            this,
            NotificationHelper.NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_CAMERA
        )
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        startDetectionLoop()
        return START_STICKY
    }

    override fun onDestroy() {
        detectionJob?.cancel()
        detectionJob = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder? {
        super.onBind(intent)
        return null
    }

    private fun startDetectionLoop() {
        if (detectionJob?.isActive == true) return

        detectionJob = lifecycleScope.launch {
            while (isActive) {
                val handDetected = gestureDetector.detectFrame()

                if (handDetected) {
                    lastHandDetectedTime = System.currentTimeMillis()
                    if (isIdle) {
                        isIdle = false
                    }
                } else {
                    val elapsed = System.currentTimeMillis() - lastHandDetectedTime
                    if (!isIdle && elapsed > idleTimeoutMs) {
                        isIdle = true
                    }
                }

                val frameDelay = if (isIdle) idleFpsDelay else activeFpsDelay
                delay(frameDelay)
            }
        }
    }
}
