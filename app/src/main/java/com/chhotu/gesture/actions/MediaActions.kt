package com.chhotu.gesture.actions

import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MediaActions @Inject constructor(
    @ApplicationContext private val context: Context,
    registry: ActionRegistry
) {

    private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

    init {
        registry.register("media.play_pause") {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE)
            true
        }

        registry.register("media.next_track") {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_NEXT)
            true
        }

        registry.register("media.prev_track") {
            dispatchMediaKey(KeyEvent.KEYCODE_MEDIA_PREVIOUS)
            true
        }
    }

    private fun dispatchMediaKey(keyCode: Int) {
        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, keyCode)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, keyCode)
        audioManager.dispatchMediaKeyEvent(downEvent)
        audioManager.dispatchMediaKeyEvent(upEvent)
    }
}
