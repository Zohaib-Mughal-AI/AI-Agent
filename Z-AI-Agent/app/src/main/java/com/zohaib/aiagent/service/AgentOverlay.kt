package com.zohaib.aiagent.service

import android.graphics.Color
import android.graphics.PixelFormat
import android.provider.Settings
import android.view.Gravity
import android.view.WindowManager
import android.widget.TextView

/** Small visible status bubble. It is shown only after the owner grants overlay access. */
class AgentOverlay(
    private val service: ZohaibService,
    private val onStop: () -> Unit
) {
    private val windowManager = service.getSystemService(WindowManager::class.java)
    private var view: TextView? = null

    fun showIfAllowed() {
        if (!Settings.canDrawOverlays(service) || view != null) return

        val bubble = TextView(service).apply {
            text = "● Z-AI\nListening"
            setTextColor(Color.WHITE)
            setBackgroundColor(Color.DKGRAY)
            setPadding(18, 12, 18, 12)
            textSize = 12f
            setOnLongClickListener {
                onStop()
                true
            }
        }

        val type = if (android.os.Build.VERSION.SDK_INT >= 26)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            WindowManager.LayoutParams.TYPE_PHONE

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.END
            x = 16
            y = 90
        }

        runCatching { windowManager.addView(bubble, params); view = bubble }
    }

    fun setState(state: String) {
        view?.post { view?.text = "● Z-AI\n$state" }
    }

    fun remove() {
        view?.let { runCatching { windowManager.removeView(it) } }
        view = null
    }
}
