package com.zohaib.aiagent.tools

import android.content.Context
import com.zohaib.aiagent.core.NativeActions

class EmailTool(private val context: Context) {
    fun compose(to: String = "", subject: String = "", body: String = ""): String {
        NativeActions.composeEmail(context, to, subject, body)
        return "Sir، email compose window کھول دی ہے۔ Send دبانے سے پہلے آپ review کر سکتے ہیں۔"
    }
    fun status() = "Email compose is available through the device's email app. Reading/sending mailbox messages requires an authorized provider API (for example Gmail OAuth)."
}
