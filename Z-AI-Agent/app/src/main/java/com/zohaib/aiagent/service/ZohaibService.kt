package com.zohaib.aiagent.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.zohaib.aiagent.MainActivity
import com.zohaib.aiagent.ai.CommandProcessor
import com.zohaib.aiagent.memory.MemoryManager
import com.zohaib.aiagent.security.SecureStorage
import com.zohaib.aiagent.voice.VoiceController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.cancel

class ZohaibService : Service() {
    companion object {
        const val CHANNEL_ID = "agent_service"
        const val NOTIFICATION_ID = 1001
        const val ACTION_START = "com.zohaib.aiagent.START"
        const val ACTION_STOP = "com.zohaib.aiagent.STOP"
    }

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
    private lateinit var voice: VoiceController
    private lateinit var processor: CommandProcessor
    private lateinit var storage: SecureStorage
    private var overlay: AgentOverlay? = null
    private var processing = false
    private var wakeOnly = true
    private val agentListener = object : VoiceController.Listener {
        override fun onTranscript(text: String) {
            if (processing) return
            processing = true
            updateNotification("Processing: $text")
            overlay?.setState("🧠 Processing")
            scope.launch {
                val answer = runCatching { processor.process(text) }
                    .getOrElse { "Sir، ایک مسئلہ آیا: ${it.message ?: "unknown error"}" }
                updateNotification("Z Agent ready")
                overlay?.setState("🔊 Speaking")
                voice.speak(answer) {
                    processing = false
                    overlay?.setState("🎙 Listening")
                    if (!isDestroyed) voice.start(agentListener, wakeOnly)
                }
            }
        }
        override fun onStatus(status: String) { overlay?.setState(status) }
    }

    override fun onCreate() {
        super.onCreate()
        createChannel()
        storage = SecureStorage(this)
        processor = CommandProcessor(this, MemoryManager(this))
        voice = VoiceController(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == ACTION_STOP) {
            stopAgent()
            return START_NOT_STICKY
        }

        startForeground(NOTIFICATION_ID, buildNotification("Z Agent is listening"))
        startAgent()
        return START_STICKY
    }

    private fun startAgent() {
        wakeOnly = storage.get(SecureStorage.KEY_WAKE_ONLY) != "false"
        if (overlay == null) {
            overlay = AgentOverlay(this) { stopAgent() }
            overlay?.showIfAllowed()
        }
        voice.start(agentListener, wakeOnly)
    }

    private fun stopAgent() {
        voice.stop()
        overlay?.remove()
        overlay = null
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun updateNotification(text: String) {
        val manager = getSystemService(NotificationManager::class.java)
        manager.notify(NOTIFICATION_ID, buildNotification(text))
    }

    private fun buildNotification(text: String): Notification {
        val pending = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_btn_speak_now)
            .setContentTitle("Z-AI Agent")
            .setContentText(text)
            .setContentIntent(pending)
            .setOngoing(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getSystemService(NotificationManager::class.java).createNotificationChannel(
                NotificationChannel(CHANNEL_ID, "Z-AI Agent", NotificationManager.IMPORTANCE_LOW)
            )
        }
    }

    override fun onDestroy() {
        voice.release()
        overlay?.remove()
        scope.coroutineContext.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
