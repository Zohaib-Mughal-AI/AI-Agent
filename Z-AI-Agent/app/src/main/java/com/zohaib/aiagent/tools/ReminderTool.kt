package com.zohaib.aiagent.tools
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.zohaib.aiagent.service.ReminderReceiver
class ReminderTool(private val context:Context){ fun createBasicReminder(text:String):String{ val am=context.getSystemService(Context.ALARM_SERVICE) as AlarmManager; val i=Intent(context,ReminderReceiver::class.java).putExtra("text",text); val pi=PendingIntent.getBroadcast(context,System.currentTimeMillis().toInt(),i,PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE); am.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+60_000,pi); return "Reminder 1 minute baad set kar diya. Android settings mein Alarms & reminders access zaroor den." } }
