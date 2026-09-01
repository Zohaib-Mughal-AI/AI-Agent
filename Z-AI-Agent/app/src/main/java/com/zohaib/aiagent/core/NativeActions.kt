package com.zohaib.aiagent.core

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings

object NativeActions {
    fun openApp(context: Context, packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName) ?: return false
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
        return true
    }

    fun openSettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openAccessibilitySettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openNotificationAccessSettings(context: Context) {
        context.startActivity(
            Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openOverlaySettings(context: Context) {
        context.startActivity(
            Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    }

    fun openBatterySettings(context: Context) {
        context.startActivity(Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openWeb(context: Context, url: String) {
        val uri = if (url.startsWith("http")) Uri.parse(url) else Uri.parse("https://$url")
        context.startActivity(Intent(Intent.ACTION_VIEW, uri).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

    fun openYouTubeSearch(context: Context, query: String) {
        val encoded = Uri.encode(query)
        val appIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/results?search_query=$encoded"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(appIntent)
    }

    fun openYouTubeDownloads(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/feed/downloads"))
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun composeEmail(context: Context, to: String?, subject: String?, body: String?) {
        val uri = Uri.parse("mailto:${to ?: ""}")
        context.startActivity(Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_SUBJECT, subject ?: "")
            putExtra(Intent.EXTRA_TEXT, body ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }

    fun sms(context: Context, number: String?, body: String?) {
        val uri = Uri.parse("smsto:${number ?: ""}")
        context.startActivity(Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", body ?: "")
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
    }
}
