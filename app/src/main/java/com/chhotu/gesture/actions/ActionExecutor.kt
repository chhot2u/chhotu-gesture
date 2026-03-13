package com.chhotu.gesture.actions

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActionExecutor @Inject constructor(
    private val registry: ActionRegistry
) {

    suspend fun execute(actionId: String): Boolean {
        val action = registry.getAction(actionId) ?: return false
        return try {
            action()
        } catch (_: Exception) {
            false
        }
    }

    fun isActionAvailable(actionId: String): Boolean =
        registry.getAction(actionId) != null
}
