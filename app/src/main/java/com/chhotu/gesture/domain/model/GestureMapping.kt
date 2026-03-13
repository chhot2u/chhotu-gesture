package com.chhotu.gesture.domain.model

/**
 * Maps a gesture to a specific action.
 */
data class GestureMapping(
    val gestureId: String,
    val actionId: String,
    val isEnabled: Boolean = true
)

/**
 * Default gesture-to-action mappings for built-in gestures.
 */
object DefaultMappings {
    fun all(): List<GestureMapping> = listOf(
        GestureMapping(gestureId = "open_hand", actionId = "media.play_pause"),
        GestureMapping(gestureId = "fist", actionId = "system.mute"),
        GestureMapping(gestureId = "thumbs_up", actionId = "system.volume_up"),
        GestureMapping(gestureId = "peace", actionId = "media.next_track"),
        GestureMapping(gestureId = "pointing", actionId = "nav.scroll_down")
    )
}
