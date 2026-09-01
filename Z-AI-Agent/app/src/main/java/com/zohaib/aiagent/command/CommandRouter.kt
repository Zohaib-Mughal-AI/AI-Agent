package com.zohaib.aiagent.command

class CommandRouter {
    fun route(input: String): ParsedCommand {
        val c = input.trim().lowercase()
        val intent = when {
            c.contains("email") || c.contains("ای میل") -> CommandIntent.EMAIL
            c.contains("whatsapp") || c.contains("telegram") || c.contains("sms") || c.contains("message") || c.contains("پیغام") -> CommandIntent.SMS
            c.contains("call") || c.contains("فون") || c.contains("dial") || c.contains("کال") -> CommandIntent.CALL
            c.contains("remind") || c.contains("reminder") || c.contains("یاد") -> CommandIntent.REMINDER
            c.contains("calendar") || c.contains("schedule") || c.contains("ٹائم ٹیبل") -> CommandIntent.CALENDAR
            c.contains("file") || c.contains("pdf") || c.contains("document") || c.contains("ڈاؤن لوڈ") -> CommandIntent.FILE
            c.contains("youtube") || c.contains("یوٹیوب") || c.contains("song") || c.contains("گانا") || c.contains("music") -> CommandIntent.MUSIC
            c.contains("notification") || c.contains("نوٹیفکیشن") -> CommandIntent.NOTIFICATION
            c.contains("assignment") || c.contains("اسائنمنٹ") -> CommandIntent.ASSIGNMENT
            c.contains("memory") || c.contains("یاد رکھ") || c.contains("بھول") -> CommandIntent.MEMORY
            c.contains("open") || c.contains("کھولو") || c.contains("khol") -> CommandIntent.OPEN_APP
            c.startsWith("http") || c.contains("website") || c.contains("browser") || c.contains("ویب") -> CommandIntent.WEB
            else -> CommandIntent.CHAT
        }
        return ParsedCommand(intent, input, 0.95f, intent in setOf(CommandIntent.EMAIL, CommandIntent.CALENDAR, CommandIntent.FILE, CommandIntent.ASSIGNMENT))
    }
}
