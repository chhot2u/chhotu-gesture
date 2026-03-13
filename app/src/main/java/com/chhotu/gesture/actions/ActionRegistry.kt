package com.chhotu.gesture.actions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionRegistry @Inject constructor() {

    private val actions = mutableMapOf<String, suspend () -> Boolean>()

    fun register(actionId: String, action: suspend () -> Boolean) {
        actions[actionId] = action
    }

    fun unregister(actionId: String) {
        actions.remove(actionId)
    }

    fun getAction(actionId: String): (suspend () -> Boolean)? = actions[actionId]

    fun getRegisteredActionIds(): Set<String> = actions.keys.toSet()
}
