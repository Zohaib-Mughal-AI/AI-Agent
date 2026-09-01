package com.zohaib.aiagent.notifications

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.speech.tts.TextToSpeech
import java.util.Locale

/**
 * Optional, user-enabled notification watcher. It does not upload notification
 * text. For call-like notifications it can announce the visible caller/title.
 */
class NotificationListener : NotificationListenerService() {
    private var tts: TextToSpeech? = null

    override fun onCreate() {
        super.onCreate()
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) tts?.language = Locale("ur", "PK")
        }
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        val extras = sbn.notification.extras ?: return
        val title = extras.getCharSequence(Notification.EXTRA_TITLE)?.toString().orEmpty()
        val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString().orEmpty()
        val packageName = sbn.packageName.lowercase()

        val looksLikeCall = packageName.contains("dialer") ||
            packageName.contains("phone") ||
            title.lowercase().contains("incoming call") ||
            title.contains("incoming", true) ||
            text.contains("calling", true)

        if (looksLikeCall && title.isNotBlank()) {
            tts?.speak(
                "Sir، آپ کو $title کی call آ رہی ہے۔ اگر آپ چاہیں تو کہیں receive کرو یا call cut کرو۔",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "z_call_${System.currentTimeMillis()}"
            )
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) = Unit

    override fun onDestroy() {
        tts?.shutdown()
        tts = null
        super.onDestroy()
    }
}
