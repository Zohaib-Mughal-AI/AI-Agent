package com.zohaib.aiagent.brain

/** Pluggable AI brain. Keep API secrets outside the APK. */
interface AIProvider {
    suspend fun reply(systemPrompt: String, conversation: List<Pair<String, String>>, userText: String): String
}

class LocalFallbackProvider : AIProvider {
    override suspend fun reply(systemPrompt: String, conversation: List<Pair<String, String>>, userText: String): String =
        "Sir، میں نے آپ کی بات سمجھنے کی کوشش کی ہے۔ اس وقت external AI provider connect نہیں ہے۔ AI provider connect کرنے کے بعد میں زیادہ advanced conversation، planning اور tool orchestration کر سکوں گا۔"
}
