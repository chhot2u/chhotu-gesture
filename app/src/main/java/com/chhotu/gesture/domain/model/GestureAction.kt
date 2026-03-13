package com.chhotu.gesture.domain.model

/**
 * Represents a system action that can be triggered by a gesture.
 */
data class GestureAction(
    val id: String,
    val name: String,
    val description: String,
    val category: ActionCategory,
    val iconName: String = "touch_app"
)

enum class ActionCategory {
    MEDIA,
    SYSTEM,
    NAVIGATION,
    CUSTOM
}

/**
 * Pre-defined system actions available in Chhotu Gesture.
 */
object DefaultActions {
    val PLAY_PAUSE = GestureAction(
        id = "media.play_pause",
        name = "Play / Pause",
        description = "Toggle media playback",
        category = ActionCategory.MEDIA,
        iconName = "play_circle"
    )
    val VOLUME_UP = GestureAction(
        id = "system.volume_up",
        name = "Volume Up",
        description = "Increase media volume",
        category = ActionCategory.SYSTEM,
        iconName = "volume_up"
    )
    val VOLUME_DOWN = GestureAction(
        id = "system.volume_down",
        name = "Volume Down",
        description = "Decrease media volume",
        category = ActionCategory.SYSTEM,
        iconName = "volume_down"
    )
    val MUTE = GestureAction(
        id = "system.mute",
        name = "Mute",
        description = "Mute/unmute media volume",
        category = ActionCategory.SYSTEM,
        iconName = "volume_off"
    )
    val NEXT_TRACK = GestureAction(
        id = "media.next_track",
        name = "Next Track",
        description = "Skip to next track",
        category = ActionCategory.MEDIA,
        iconName = "skip_next"
    )
    val PREV_TRACK = GestureAction(
        id = "media.prev_track",
        name = "Previous Track",
        description = "Go to previous track",
        category = ActionCategory.MEDIA,
        iconName = "skip_previous"
    )
    val BRIGHTNESS_UP = GestureAction(
        id = "system.brightness_up",
        name = "Brightness Up",
        description = "Increase screen brightness",
        category = ActionCategory.SYSTEM,
        iconName = "brightness_high"
    )
    val BRIGHTNESS_DOWN = GestureAction(
        id = "system.brightness_down",
        name = "Brightness Down",
        description = "Decrease screen brightness",
        category = ActionCategory.SYSTEM,
        iconName = "brightness_low"
    )
    val SCROLL_UP = GestureAction(
        id = "nav.scroll_up",
        name = "Scroll Up",
        description = "Scroll the screen upward",
        category = ActionCategory.NAVIGATION,
        iconName = "arrow_upward"
    )
    val SCROLL_DOWN = GestureAction(
        id = "nav.scroll_down",
        name = "Scroll Down",
        description = "Scroll the screen downward",
        category = ActionCategory.NAVIGATION,
        iconName = "arrow_downward"
    )
    val GO_BACK = GestureAction(
        id = "nav.go_back",
        name = "Go Back",
        description = "Navigate back",
        category = ActionCategory.NAVIGATION,
        iconName = "arrow_back"
    )
    val GO_HOME = GestureAction(
        id = "nav.go_home",
        name = "Go Home",
        description = "Go to home screen",
        category = ActionCategory.NAVIGATION,
        iconName = "home"
    )
    val FLASHLIGHT = GestureAction(
        id = "system.flashlight",
        name = "Toggle Flashlight",
        description = "Turn flashlight on/off",
        category = ActionCategory.SYSTEM,
        iconName = "flashlight_on"
    )

    fun all(): List<GestureAction> = listOf(
        PLAY_PAUSE, VOLUME_UP, VOLUME_DOWN, MUTE,
        NEXT_TRACK, PREV_TRACK,
        BRIGHTNESS_UP, BRIGHTNESS_DOWN,
        SCROLL_UP, SCROLL_DOWN, GO_BACK, GO_HOME,
        FLASHLIGHT
    )
}
