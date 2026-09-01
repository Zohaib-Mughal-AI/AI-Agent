package com.zohaib.aiagent.automation

/** Central safety gate. High-impact actions should require explicit confirmation. */
object AutomationGuard {
    enum class Risk { LOW, MEDIUM, HIGH }
    fun requiresConfirmation(risk: Risk): Boolean = risk != Risk.LOW
}
