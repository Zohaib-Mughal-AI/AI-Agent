package com.zohaib.aiagent.tools

import android.content.Context
import android.content.Intent
import com.zohaib.aiagent.core.NativeActions
import java.util.Locale

class AppTool(private val context: Context) {
    fun openCommonApp(command: String): String {
        val c = command.lowercase(Locale.getDefault())
        val known = when {
            "youtube" in c || "یوٹیوب" in c -> "com.google.android.youtube"
            "chrome" in c -> "com.android.chrome"
            "gmail" in c -> "com.google.android.gm"
            "telegram" in c -> "org.telegram.messenger"
            "whatsapp" in c -> "com.whatsapp"
            "drive" in c -> "com.google.android.apps.docs"
            "maps" in c || "google maps" in c -> "com.google.android.apps.maps"
            "camera" in c || "کیمرہ" in c -> "com.android.camera2"
            "calculator" in c || "کیلکولیٹر" in c -> "com.google.android.calculator"
            "mx player" in c -> "com.mxtech.videoplayer.ad"
            "settings" in c || "سیٹنگ" in c -> "android.settings.SETTINGS"
            else -> null
        }

        if (known == "android.settings.SETTINGS") {
            NativeActions.openSettings(context)
            return "Sir، Settings کھول دی ہیں۔"
        }

        if (known != null && NativeActions.openApp(context, known))
            return "Sir، requested app open کر دی ہے۔"

        // Fallback: match any launchable installed app by its visible label.
        val pm = context.packageManager
        val launcherIntent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        val apps = pm.queryIntentActivities(launcherIntent, 0)
        val query = c
            .replace(Regex("(?i)^(open|launch|start|کھولو|چلاؤ|چلائیں)"), "")
            .trim()

        val match = apps.firstOrNull { info ->
            val label = info.loadLabel(pm).toString().lowercase(Locale.getDefault())
            query.isNotBlank() && (label.contains(query) || query.contains(label))
        }

        return if (match != null && NativeActions.openApp(context, match.activityInfo.packageName)) {
            "Sir، ${match.loadLabel(pm)} open کر دی ہے۔"
        } else {
            "Sir، یہ app identify نہیں ہو سکی۔ میں app کا exact نام پوچھ سکتا ہوں۔"
        }
    }
}
