package com.zohaib.aiagent.ai

import android.content.Context
import com.zohaib.aiagent.security.SecureStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/** Real Gemini REST client. The API key is supplied by the owner at runtime and stored encrypted on-device. */
interface AIClient {
    suspend fun generate(system: String, user: String, context: String): String
}

class GeminiAIClient(context: Context) : AIClient {
    private val storage = SecureStorage(context.applicationContext)

    override suspend fun generate(system: String, user: String, context: String): String = withContext(Dispatchers.IO) {
        val apiKey = storage.get(SecureStorage.KEY_GEMINI_API_KEY)
            ?: return@withContext "Sir، Gemini API key ابھی set نہیں ہوئی۔ Settings میں اپنی Google AI Studio API key add کریں۔"
        val model = storage.get(SecureStorage.KEY_GEMINI_MODEL)?.ifBlank { null } ?: "gemini-3.7-flash"

        val url = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent")
        val connection = (url.openConnection() as HttpURLConnection).apply {
            requestMethod = "POST"
            connectTimeout = 20_000
            readTimeout = 60_000
            doOutput = true
            setRequestProperty("Content-Type", "application/json")
            setRequestProperty("x-goog-api-key", apiKey)
        }

        val contents = JSONArray()
        if (context.isNotBlank()) {
            contents.put(JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().put(JSONObject().put("text", "Conversation context:\n$context")))
            })
        }
        contents.put(JSONObject().apply {
            put("role", "user")
            put("parts", JSONArray().put(JSONObject().put("text", user)))
        })

        val body = JSONObject().apply {
            put("systemInstruction", JSONObject().apply {
                put("parts", JSONArray().put(JSONObject().put("text", system)))
            })
            put("contents", contents)
            put("generationConfig", JSONObject().apply {
                put("temperature", 0.7)
                put("maxOutputTokens", 2048)
            })
        }

        connection.outputStream.use { it.write(body.toString().toByteArray(Charsets.UTF_8)) }
        val status = connection.responseCode
        val stream = if (status in 200..299) connection.inputStream else connection.errorStream
        val response = stream?.bufferedReader()?.use { it.readText() } ?: ""
        connection.disconnect()

        if (status !in 200..299) {
            val message = runCatching { JSONObject(response).optJSONObject("error")?.optString("message") }.getOrNull()
            return@withContext "Sir، Gemini request fail ہوئی۔ ${message ?: "HTTP $status"}"
        }

        val root = JSONObject(response)
        val candidates = root.optJSONArray("candidates")
        val parts = candidates?.optJSONObject(0)?.optJSONObject("content")?.optJSONArray("parts")
        val text = buildString {
            if (parts != null) for (i in 0 until parts.length()) {
                val part = parts.optJSONObject(i)
                val t = part?.optString("text").orEmpty()
                if (t.isNotBlank()) append(t)
            }
        }.trim()
        text.ifBlank { "Sir، AI نے کوئی text response نہیں دیا۔" }
    }

    suspend fun testConnection(): String = generate(
        "You are a connection tester. Reply in one short sentence.",
        "Reply only: Z Agent Gemini connection is working.",
        ""
    )
}

/** Offline fallback so the app still opens and can execute local Android actions without an API key. */
class LocalFallbackAIClient : AIClient {
    override suspend fun generate(system: String, user: String, context: String): String =
        "Sir، یہ local mode ہے۔ آپ اپنی Gemini API key Settings میں add کریں تو Z Agent real AI conversation کرے گا۔"
}
