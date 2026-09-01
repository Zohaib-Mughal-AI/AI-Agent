package com.zohaib.aiagent.ai

import android.content.Context
import com.zohaib.aiagent.command.CommandIntent
import com.zohaib.aiagent.command.CommandRouter
import com.zohaib.aiagent.memory.MemoryManager
import com.zohaib.aiagent.profile.DeveloperProfile
import com.zohaib.aiagent.tools.*

/**
 * Deterministic local command layer. Every external/device action is queued
 * for explicit confirmation. Normal conversation does not need confirmation.
 */
class CommandProcessor(context: Context, private val memory: MemoryManager) {
    private val appContext = context.applicationContext
    private val router = CommandRouter()
    private val brain = AgentBrain(appContext, memory)
    private val appTool = AppTool(appContext)
    private val email = EmailTool(appContext)
    private val msg = MessageTool(appContext)
    private val call = CallTool(appContext)
    private val web = WebTool(appContext)
    private val reminders = ReminderTool(appContext)
    private val media = MediaTool(appContext)

    @Volatile private var pending: (() -> String)? = null
    @Volatile private var pendingDescription: String? = null

    suspend fun process(command: String): String {
        val input = command.trim()
        if (input.isBlank()) return "Sir، میں سن رہا ہوں۔"
        memory.saveConversation("user", input)
        val c = input.lowercase()

        if (pending != null && isConfirmation(c)) {
            val action = pending
            val description = pendingDescription
            pending = null
            pendingDescription = null
            val result = runCatching { action?.invoke() ?: "Sir، کوئی pending action نہیں ہے۔" }
                .getOrElse { "Sir، action execute نہیں ہو سکا: ${it.message}" }
            memory.saveConversation("assistant", result)
            return result
        }

        if (pending != null && isRejection(c)) {
            pending = null
            pendingDescription = null
            val result = "Sir، ٹھیک ہے۔ Pending action cancel کر دیا۔"
            memory.saveConversation("assistant", result)
            return result
        }

        if (c.contains("who made") || c.contains("who developed") || c.contains("کس نے بنایا") || c.contains("developer")) {
            return rememberAndReturn("Sir، مجھے ${DeveloperProfile.developerName} نے develop کیا ہے۔ میں ${DeveloperProfile.agentName} ہوں۔")
        }
        if (c.contains("who am i") || c.contains("میں کون") || c.contains("mera naam")) {
            return rememberAndReturn("Sir، آپ ${DeveloperProfile.developerName} ہیں۔")
        }
        if (c.contains("who are you") || c.contains("تم کون") || c.contains("تمہارا نام")) {
            return rememberAndReturn("Sir، میں ${DeveloperProfile.agentName} ہوں، آپ کا personal AI assistant۔")
        }

        val parsed = router.route(input)
        val response = when {
            parsed.intent == CommandIntent.MUSIC && (c.contains("mx player") || c.contains("mx") ) ->
                requestConfirmation(
                    "MX Player میں media کھولنے",
                    action = { media.playMxPlayer(extractMediaQuery(input)) }
                )
            parsed.intent == CommandIntent.MUSIC && (c.contains("youtube") || c.contains("یوٹیوب") || c.contains("song") || c.contains("گانا")) ->
                requestConfirmation(
                    "YouTube میں ویڈیو/گانا تلاش اور چلانے",
                    action = {
                        val query = extractMediaQuery(input)
                        if (c.contains("download") || c.contains("downloaded") || c.contains("offline") || c.contains("ڈاؤن لوڈ") || c.contains("آف لائن"))
                            media.playYouTube(query)
                        else media.searchYouTube(query)
                    }
                )
            parsed.intent == CommandIntent.OPEN_APP ->
                requestConfirmation("app کھولنے", action = { appTool.openCommonApp(input) })
            parsed.intent == CommandIntent.EMAIL ->
                requestConfirmation("email compose کرنے", action = { email.compose(body = input) })
            parsed.intent == CommandIntent.SMS ->
                requestConfirmation("message compose کرنے", action = { msg.composeSms(body = input) })
            parsed.intent == CommandIntent.CALL ->
                requestConfirmation("phone call action", action = { call.handle(input) })
            parsed.intent == CommandIntent.WEB ->
                requestConfirmation("browser/web page کھولنے", action = { web.open(input) })
            parsed.intent == CommandIntent.REMINDER ->
                requestConfirmation("reminder بنانے", action = { reminders.createBasicReminder(input) })
            parsed.intent == CommandIntent.MEMORY && (c.contains("forget") || c.contains("بھول")) -> {
                memory.clearAll()
                "Sir، local conversation memory clear کر دی گئی ہے۔"
            }
            else -> brain.respond(input)
        }

        memory.saveConversation("assistant", response)
        return response
    }

    private fun requestConfirmation(description: String, action: () -> String): String {
        pendingDescription = description
        pending = action
        return "Sir، کیا میں $description کروں؟ اگر اجازت ہے تو \"ہاں، کر دو\" کہیں۔"
    }

    private fun isConfirmation(c: String): Boolean =
        listOf("yes", "yeah", "ok", "okay", "confirm", "do it", "go ahead", "ہاں", "جی", "کر دو", "کر دیں", "اجازت ہے", "ٹھیک ہے")
            .any { c == it || c.contains(it) }

    private fun isRejection(c: String): Boolean =
        listOf("no", "cancel", "stop", "نہیں", "منسوخ", "رہنے دو", "مت کرو").any { c == it || c.contains(it) }

    private fun extractMediaQuery(input: String): String {
        val c = input.lowercase()
        val patterns = listOf(
            Regex("""(?:song|گانا|music)\s+(.+?)(?:\s+(?:play|چلا|on|کر دو|download|downloaded|offline).*)?$""", RegexOption.IGNORE_CASE),
            Regex("""(?:youtube|یوٹیوب)\s+(.+?)(?:\s+(?:play|چلا|search|تلاش|download|downloaded|offline).*)?$""", RegexOption.IGNORE_CASE)
        )
        patterns.firstNotNullOfOrNull { it.find(input)?.groupValues?.getOrNull(1)?.trim() }
            ?.takeIf { it.isNotBlank() }
            ?.let { return it }
        return input.replace(Regex("(?i)youtube|یوٹیوب|play|چلا دو|چلاؤ|download|downloaded|offline|song|گانا|music"), " ")
            .trim()
            .ifBlank { "requested video" }
    }

    private fun rememberAndReturn(text: String): String {
        memory.saveConversation("assistant", text)
        return text
    }

    suspend fun testAI(): String = brain.testGemini()
}
