package com.zohaib.aiagent.ai

data class PlanItem(val time: String, val task: String)
class AgentPlanner {
    fun dailyPlan(tasks: List<String>): List<PlanItem> = tasks.mapIndexed { i, task -> PlanItem("${8+i}:00", task) }
}
