package com.zohaib.aiagent.voice

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import java.util.Locale

/**
 * Continuous speech loop. It listens one utterance at a time and immediately
 * schedules the next recognition session. The Android OS/OEM may stop or
 * restrict microphone access; the UI notification makes the listening state visible.
 */
class VoiceController(private val context: Context) : TextToSpeech.OnInitListener {
    interface Listener {
        fun onTranscript(text: String)
        fun onStatus(status: String)
    }

    private val main = Handler(Looper.getMainLooper())
    private var tts: TextToSpeech? = TextToSpeech(context, this)
    private var recognizer: SpeechRecognizer? = null
    private var running = false
    private var listener: Listener? = null
    private var wakeOnly = true
    private var speaking = false

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("ur", "PK")
            tts?.setSpeechRate(0.98f)
        }
    }

    fun speak(text: String, onDone: (() -> Unit)? = null) {
        val engine = tts ?: return
        speaking = true
        engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit
            override fun onDone(utteranceId: String?) {
                speaking = false
                main.post { onDone?.invoke() }
            }
            override fun onError(utteranceId: String?) {
                speaking = false
                main.post { onDone?.invoke() }
            }
        })
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "z_agent_${System.currentTimeMillis()}")
    }

    fun start(listener: Listener, wakeOnly: Boolean) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            listener.onStatus("Speech recognition service available نہیں ہے۔")
            return
        }
        this.listener = listener
        this.wakeOnly = wakeOnly
        running = true
        listener.onStatus(if (wakeOnly) "Listening — wake phrase mode" else "Listening — all speech mode")
        createRecognizerIfNeeded()
        listenOnce()
    }

    fun stop() {
        running = false
        speaking = false
        recognizer?.cancel()
        listener?.onStatus("Voice listening بند ہے۔")
    }

    private fun createRecognizerIfNeeded() {
        if (recognizer != null) return
        recognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
            setRecognitionListener(object : RecognitionListener {
                override fun onReadyForSpeech(params: Bundle?) { listener?.onStatus("سن رہا ہوں…") }
                override fun onBeginningOfSpeech() { listener?.onStatus("Listening…") }
                override fun onRmsChanged(rmsdB: Float) = Unit
                override fun onBufferReceived(buffer: ByteArray?) = Unit
                override fun onEndOfSpeech() { listener?.onStatus("Processing…") }

                override fun onError(error: Int) {
                    if (running && !speaking) {
                        listener?.onStatus("Voice restart…")
                        main.postDelayed({ listenOnce() }, 500)
                    }
                }

                override fun onResults(results: Bundle?) {
                    val text = results
                        ?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                        ?.firstOrNull()
                        .orEmpty()

                    if (text.isNotBlank()) {
                        val normalized = text.lowercase(Locale.getDefault())
                        val wake = listOf(
                            "z agent", "z-agent", "zohaib agent", "zohaib",
                            "زیڈ ایجنٹ", "زوہیب ایجنٹ", "زوہیب", "ایجنٹ"
                        ).any { normalized.contains(it) }

                        if (!wakeOnly || wake) {
                            listener?.onTranscript(text)
                        }
                    }

                    if (running && !speaking) {
                        main.postDelayed({ listenOnce() }, 350)
                    }
                }

                override fun onPartialResults(partialResults: Bundle?) = Unit
                override fun onEvent(eventType: Int, params: Bundle?) = Unit
            })
        }
    }

    private fun listenOnce() {
        if (!running || speaking) return
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_LANGUAGE, "ur-PK")
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "ur-PK")
            putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, false)
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3)
        }
        runCatching { recognizer?.startListening(intent) }
            .onFailure { listener?.onStatus("Voice start error: ${it.message}") }
    }

    fun release() {
        running = false
        recognizer?.destroy()
        recognizer = null
        tts?.shutdown()
        tts = null
    }
}
