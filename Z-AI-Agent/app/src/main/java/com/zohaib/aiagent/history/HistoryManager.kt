package com.zohaib.aiagent.history
class HistoryManager { private val items=mutableListOf<ActionHistory>(); fun add(a:String){items+=ActionHistory(a)}; fun all()=items.toList(); fun clear(){items.clear()} }
