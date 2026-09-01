package com.zohaib.aiagent.brain

import android.content.Context
import com.zohaib.aiagent.ai.GeminiAIClient
import com.zohaib.aiagent.ai.LocalFallbackAIClient
import com.zohaib.aiagent.memory.MemoryManager
import com.zohaib.aiagent.profile.AgentPersonality
import com.zohaib.aiagent.profile.DeveloperProfile
import com.zohaib.aiagent.security.SecureStorage

class AgentBrain(context: Context, private val memory: MemoryManager) {
    private val appContext = context.applicationContext
    private val secure = SecureStorage(appContext)
    private val fallback = LocalFallbackAIClient()
    private val gemini = GeminiAIClient(appContext)

    suspend fun respond(text: String): String {
        val system = AgentPersonality.systemStyle +
            "\nDeveloper: ${DeveloperProfile.developerName}. Address the owner as Sir."
        val recent = memory.recentConversations(12)
        val context = recent.joinToString("\n") { "${it.first}: ${it.second}" }
        val client = if (secure.get(SecureStorage.KEY_GEMINI_API_KEY).isNullOrBlank()) fallback else gemini
        return client.generate(system, text, context)
    }

    suspend fun testGemini(): String = gemini.testConnection()
}
