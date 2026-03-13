package com.chhotu.gesture.actions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationActions @Inject constructor(
    registry: ActionRegistry
) {

    init {
        registry.register("nav.scroll_up") {
            true
        }

        registry.register("nav.scroll_down") {
            true
        }

        registry.register("nav.go_back") {
            true
        }

        registry.register("nav.go_home") {
            true
        }
    }
}
