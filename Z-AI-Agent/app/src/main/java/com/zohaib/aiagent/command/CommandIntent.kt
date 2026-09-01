package com.zohaib.aiagent.command

enum class CommandIntent {
    CHAT, OPEN_APP, WEB, CALL, SMS, EMAIL, REMINDER, FILE, MUSIC,
    NOTIFICATION, CALENDAR, CONTACT, ASSIGNMENT, MEMORY, SYSTEM, UNKNOWN
}

data class ParsedCommand(
    val intent: CommandIntent,
    val text: String,
    val confidence: Float = 0.0f,
    val requiresExternalIntegration: Boolean = false
)
