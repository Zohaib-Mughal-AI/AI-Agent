package com.zohaib.aiagent.tools
import android.content.Context
import com.zohaib.aiagent.core.NativeActions
class WebTool(private val context: Context) {
    fun open(url: String): String { NativeActions.openWeb(context,url); return "Sir، browser کھول دیا ہے۔" }
    fun status() = "Browser launching is available. AI web research requires a connected search/LLM service."
}
