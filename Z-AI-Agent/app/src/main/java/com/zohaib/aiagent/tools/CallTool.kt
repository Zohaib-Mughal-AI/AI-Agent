package com.zohaib.aiagent.tools

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.telecom.TelecomManager
import androidx.core.content.ContextCompat

class CallTool(private val context: Context) {
    fun handle(command: String): String {
        val c = command.lowercase()
        return when {
            listOf("receive", "answer", "pick up", "اٹھاؤ", "ریسیو", "وصول").any { c.contains(it) } ->
                answerIncoming()
            listOf("cut", "hang up", "end call", "reject", "کاٹ", "بند کرو", "کال کاٹ").any { c.contains(it) } ->
                endCall()
            else -> openDialer(command)
        }
    }

    fun openDialer(command: String): String {
        val digits = command.filter { it.isDigit() || it == '+' }
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${if (digits.isNotBlank()) digits else ""}"))
        context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        return "Sir، dialer کھول دیا ہے۔"
    }

    private fun answerIncoming(): String {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED)
            return "Sir، incoming call answer کرنے کے لیے ANSWER_PHONE_CALLS permission درکار ہے۔"
        val telecom = context.getSystemService(TelecomManager::class.java)
        return runCatching {
            telecom.acceptRingingCall()
            "Sir، incoming call answer کرنے کی request دے دی ہے۔"
        }.getOrElse {
            "Sir، Android نے call answer action allow نہیں کیا۔ ممکن ہے phone app/default dialer restriction ہو۔"
        }
    }

    private fun endCall(): String {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED)
            return "Sir، call end کرنے کے لیے ANSWER_PHONE_CALLS permission درکار ہے۔"
        if (android.os.Build.VERSION.SDK_INT < 28)
            return "Sir، اس Android version پر programmatic call end available نہیں ہے۔"
        val telecom = context.getSystemService(TelecomManager::class.java)
        return runCatching {
            telecom.endCall()
            "Sir، call end کرنے کی request دے دی ہے۔"
        }.getOrElse {
            "Sir، Android نے call end action allow نہیں کیا۔"
        }
    }
}
