package com.zohaib.aiagent.integrations

data class Integration(val id: String, val name: String, val capability: String, var connected: Boolean = false)

object IntegrationRegistry {
    val supported = listOf(
        Integration("gmail", "Gmail", "OAuth/API email read, search, draft and send"),
        Integration("google_calendar", "Google Calendar", "events and reminders through authorized API"),
        Integration("drive", "Google Drive", "authorized file access"),
        Integration("github", "GitHub", "authorized repository operations"),
        Integration("telegram", "Telegram", "app/API dependent messaging"),
        Integration("whatsapp", "WhatsApp", "authorized UI/app capabilities only"),
        Integration("web", "Web", "browser and web actions"),
        Integration("android", "Android", "native intents, notifications and permissions")
    )
}
