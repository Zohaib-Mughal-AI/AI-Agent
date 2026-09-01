package com.zohaib.aiagent

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.MaterialTheme
import androidx.core.content.ContextCompat
import com.zohaib.aiagent.ai.CommandProcessor
import com.zohaib.aiagent.memory.MemoryManager
import com.zohaib.aiagent.ui.AgentScreen

class MainActivity : ComponentActivity() {
    private val memory by lazy { MemoryManager(this) }
    private val processor by lazy { CommandProcessor(this, memory) }

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestCorePermissions()
        setContent { MaterialTheme { AgentScreen(processor) } }
    }

    fun startAgentService() {
        val intent = Intent(this, com.zohaib.aiagent.service.ZohaibService::class.java)
            .setAction(com.zohaib.aiagent.service.ZohaibService.ACTION_START)
        ContextCompat.startForegroundService(this, intent)
    }

    fun stopAgentService() {
        startService(
            Intent(this, com.zohaib.aiagent.service.ZohaibService::class.java)
                .setAction(com.zohaib.aiagent.service.ZohaibService.ACTION_STOP)
        )
    }

    private fun requestCorePermissions() {
        val permissions = mutableListOf(Manifest.permission.RECORD_AUDIO)
        if (Build.VERSION.SDK_INT >= 33) permissions += Manifest.permission.POST_NOTIFICATIONS
        val missing = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != android.content.pm.PackageManager.PERMISSION_GRANTED
        }
        if (missing.isNotEmpty()) permissionLauncher.launch(missing.toTypedArray())
    }
}
