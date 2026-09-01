package com.zohaib.aiagent.tools
import android.content.Context
import com.zohaib.aiagent.core.NativeActions
class MessageTool(private val context: Context) {
    fun composeSms(number: String = "", body: String = ""): String { NativeActions.sms(context, number, body); return "Sir، SMS compose window کھول دی ہے۔" }
    fun status() = "SMS compose is available. Third-party messaging apps require their supported intents/APIs or user-enabled accessibility automation."
}
