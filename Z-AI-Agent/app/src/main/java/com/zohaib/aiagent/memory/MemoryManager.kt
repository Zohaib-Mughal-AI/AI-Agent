package com.zohaib.aiagent.memory

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey

class MemoryManager(context: Context) {
    private val key = MasterKey.Builder(context.applicationContext)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build()
    private val prefs = EncryptedSharedPreferences.create(
        context.applicationContext, "z_agent_memory", key,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    @Synchronized
    fun saveConversation(role: String, text: String) {
        val old = prefs.getString("conversation", "") ?: ""
        val next = (old + "\n" + role + ":" + text).trim().lines().takeLast(80).joinToString("\n")
        prefs.edit().putString("conversation", next).apply()
    }

    fun getConversation(): String = prefs.getString("conversation", "") ?: ""

    fun clearAll() { prefs.edit().clear().apply() }

    fun recentConversations(limit: Int): List<Pair<String, String>> = getConversation()
        .lineSequence()
        .mapNotNull { line ->
            val i = line.indexOf(':')
            if (i <= 0) null else line.substring(0, i) to line.substring(i + 1)
        }
        .takeLast(limit)
        .toList()
}
