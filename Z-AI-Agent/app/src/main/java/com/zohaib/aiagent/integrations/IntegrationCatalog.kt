package com.zohaib.aiagent.integrations

/** Declares integration points; credentials/tokens must be supplied by the user at runtime. */
object IntegrationCatalog {
    val supportedTargets = listOf(
        "Gmail", "Google Calendar", "Google Drive", "Contacts", "Phone", "SMS",
        "WhatsApp", "Telegram", "Chrome", "YouTube", "Files", "Notifications",
        "Accessibility", "Text-to-Speech", "Speech Recognition"
    )

    fun requiresOAuth(service: String): Boolean = service in setOf("Gmail", "Google Calendar", "Google Drive")
}
