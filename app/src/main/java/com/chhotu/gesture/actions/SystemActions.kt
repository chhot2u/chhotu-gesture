package com.chhotu.gesture.actions

import android.content.Context
import android.hardware.camera2.CameraManager
import android.media.AudioManager
import android.provider.Settings
import android.widget.Toast
import com.chhotu.gesture.util.PermissionManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SystemActions @Inject constructor(
    @ApplicationContext private val context: Context,
    registry: ActionRegistry
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    private val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private var isFlashlightOn = false

    init {
        registry.register("system.volume_up") {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_RAISE,
                AudioManager.FLAG_SHOW_UI
            )
            true
        }

        registry.register("system.volume_down") {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_LOWER,
                AudioManager.FLAG_SHOW_UI
            )
            true
        }

        registry.register("system.mute") {
            audioManager.adjustStreamVolume(
                AudioManager.STREAM_MUSIC,
                AudioManager.ADJUST_TOGGLE_MUTE,
                AudioManager.FLAG_SHOW_UI
            )
            true
        }

        // FIX SPEC-006: Check WRITE_SETTINGS permission before brightness changes
        registry.register("system.brightness_up") {
            if (!PermissionManager.hasWriteSettingsPermission(context)) {
                Toast.makeText(context, "Grant Write Settings permission first", Toast.LENGTH_SHORT).show()
                PermissionManager.requestWriteSettingsPermission(context)
                false
            } else {
                try {
                    val current = Settings.System.getInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        128
                    )
                    val newBrightness = (current + 25).coerceAtMost(255)
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        newBrightness
                    )
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        registry.register("system.brightness_down") {
            if (!PermissionManager.hasWriteSettingsPermission(context)) {
                Toast.makeText(context, "Grant Write Settings permission first", Toast.LENGTH_SHORT).show()
                PermissionManager.requestWriteSettingsPermission(context)
                false
            } else {
                try {
                    val current = Settings.System.getInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        128
                    )
                    val newBrightness = (current - 25).coerceAtLeast(0)
                    Settings.System.putInt(
                        context.contentResolver,
                        Settings.System.SCREEN_BRIGHTNESS,
                        newBrightness
                    )
                    true
                } catch (e: Exception) {
                    false
                }
            }
        }

        registry.register("system.flashlight") {
            try {
                val cameraId = cameraManager.cameraIdList.firstOrNull() ?: return@register false
                isFlashlightOn = !isFlashlightOn
                cameraManager.setTorchMode(cameraId, isFlashlightOn)
                true
            } catch (e: Exception) {
                false
            }
        }
    }
}
