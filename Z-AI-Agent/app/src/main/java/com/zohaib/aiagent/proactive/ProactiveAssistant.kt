package com.zohaib.aiagent.proactive

import com.zohaib.aiagent.config.ZAgentConfig

/** Generates proactive prompts; scheduling/execution is handled by Android services. */
class ProactiveAssistant {
    fun morningBriefing(tasks: List<String>): String = buildString {
        append("Good morning, ${ZAgentConfig.ownerAddress}. ")
        if (tasks.isEmpty()) append("Aaj ke liye abhi koi saved task nahi hai.")
        else append("Aaj ke important tasks: ").append(tasks.joinToString(", "))
    }

    fun reminder(task: String): String = "${ZAgentConfig.ownerAddress}, reminder: $task"
}
