package com.zohaib.aiagent.tools

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.zohaib.aiagent.accessibility.AccessibilityBridge
import com.zohaib.aiagent.core.NativeActions

class MediaTool(private val context: Context) {
    fun playYouTube(query: String): String {
        val clean = query.trim()
        if (clean.isBlank()) {
            NativeActions.openApp(context, "com.google.android.youtube")
            return "Sir، YouTube کھول دیا ہے۔"
        }

        NativeActions.openYouTubeDownloads(context)
        Handler(Looper.getMainLooper()).postDelayed({
            val found = AccessibilityBridge.clickVisibleText(clean)
            if (!found) NativeActions.openYouTubeSearch(context, clean)
        }, 2500)

        return "Sir، پہلے YouTube Downloads میں \"$clean\" چیک کر رہا ہوں؛ visible match نہ ملا تو search کھول دوں گا۔"
    }

    fun playMxPlayer(query: String): String {
        val clean = query.trim()
        if (!NativeActions.openApp(context, "com.mxtech.videoplayer.ad")) {
            return "Sir، MX Player installed نہیں ملا۔"
        }
        if (clean.isNotBlank()) {
            Handler(Looper.getMainLooper()).postDelayed({
                AccessibilityBridge.clickVisibleText(clean)
            }, 1800)
        }
        return "Sir، MX Player کھول دیا ہے اور \"$clean\" کو visible library میں تلاش کرنے کی کوشش کروں گا۔"
    }

    fun searchYouTube(query: String): String {
        NativeActions.openYouTubeSearch(context, query)
        return "Sir، YouTube search کھول دی ہے۔"
    }
}
