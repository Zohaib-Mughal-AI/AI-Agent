package com.zohaib.aiagent.accounts
class ServiceRegistry { private val services=mutableSetOf<String>(); fun register(name:String){services+=name}; fun all()=services.toList() }
