package com.chhotu.gesture.actions

import com.chhotu.gesture.engine.ContinuousScrollEngine
import com.chhotu.gesture.service.GestureAccessibilityService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationActions @Inject constructor(
    registry: ActionRegistry,
    private val scrollEngine: ContinuousScrollEngine
) {

    init {
        registry.register("nav.scroll_up") {
            val service = GestureAccessibilityService.instance
            if (service != null) {
                scrollEngine.startContinuousScroll(direction = false)
                true
            } else false
        }

        registry.register("nav.scroll_down") {
            val service = GestureAccessibilityService.instance
            if (service != null) {
                scrollEngine.startContinuousScroll(direction = true)
                true
            } else false
        }

        registry.register("nav.go_back") {
            GestureAccessibilityService.instance?.performBack() ?: false
        }

        registry.register("nav.go_home") {
            GestureAccessibilityService.instance?.performHome() ?: false
        }
    }
}
