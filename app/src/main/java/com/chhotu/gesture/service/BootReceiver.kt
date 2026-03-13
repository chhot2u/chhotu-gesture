package com.chhotu.gesture.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private val Context.dataStore by preferencesDataStore(name = "settings")

class BootReceiver : BroadcastReceiver() {

    companion object {
        private val KEY_DETECTION_ENABLED = booleanPreferencesKey("detection_enabled")
    }

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val isEnabled = context.dataStore.data
                    .map { prefs -> prefs[KEY_DETECTION_ENABLED] ?: false }
                    .first()

                if (isEnabled) {
                    GestureDetectionService.start(context)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
