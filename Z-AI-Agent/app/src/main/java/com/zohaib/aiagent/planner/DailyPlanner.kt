package com.zohaib.aiagent.planner

import java.time.LocalDate

data class PlanItem(val title: String, val time: String? = null, val completed: Boolean = false)

class DailyPlanner {
    private val plans = mutableMapOf<LocalDate, MutableList<PlanItem>>()
    fun add(date: LocalDate, item: PlanItem) { plans.getOrPut(date) { mutableListOf() }.add(item) }
    fun today(): List<PlanItem> = plans[LocalDate.now()]?.toList() ?: emptyList()
}
