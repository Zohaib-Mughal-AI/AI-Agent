package com.zohaib.aiagent.owner

import com.zohaib.aiagent.profile.DeveloperProfile

data class OwnerProfile(
    val name: String = DeveloperProfile.developerName,
    val preferredAddress: String = DeveloperProfile.preferredAddress,
    val email: String = DeveloperProfile.email,
    val languages: String = DeveloperProfile.languages,
    val role: String = "Developer / Owner"
)
