package com.zohaib.aiagent.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.zohaib.aiagent.MainActivity
import com.zohaib.aiagent.ai.CommandProcessor
import com.zohaib.aiagent.core.NativeActions
import com.zohaib.aiagent.profile.AgentPersonality
import com.zohaib.aiagent.security.SecureStorage
import kotlinx.coroutines.launch

@Composable
fun AgentScreen(processor: CommandProcessor) {
    val context = LocalContext.current
    val activity = context as? MainActivity
    val scope = rememberCoroutineScope()
    val storage = remember { SecureStorage(context) }

    var input by remember { mutableStateOf("") }
    var apiKey by remember { mutableStateOf(storage.get(SecureStorage.KEY_GEMINI_API_KEY).orEmpty()) }
    var model by remember { mutableStateOf(storage.get(SecureStorage.KEY_GEMINI_MODEL) ?: "gemini-2.5-flash") }
    var wakeOnly by remember { mutableStateOf(storage.get(SecureStorage.KEY_WAKE_ONLY) != "false") }
    var messages by remember { mutableStateOf(listOf("Z Agent: ${AgentPersonality.ownerIntroduction()}")) }
    var busy by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf("") }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        testResult = if (result[Manifest.permission.RECORD_AUDIO] == true)
            "Microphone permission granted."
        else
            "Microphone permission required."
    }
    val phonePermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        testResult = if (result[Manifest.permission.ANSWER_PHONE_CALLS] == true)
            "Phone answer permission granted."
        else
            "Phone answer permission not granted."
    }

    fun runCommand(text: String, speak: Boolean = false) {
        if (text.isBlank() || busy) return
        busy = true
        messages = messages + "You: $text"
        input = ""
        scope.launch {
            val answer = runCatching { processor.process(text) }
                .getOrElse { "Sir، مسئلہ آیا: ${it.message}" }
            messages = messages + "Z Agent: $answer"
            busy = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Z-AI Agent", style = MaterialTheme.typography.headlineMedium)
            Text("FINAL24 • Personal AI • Owner: Zohaib Mughal")
        }

        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("1) AI Brain — Google Gemini", style = MaterialTheme.typography.titleMedium)
                    OutlinedTextField(
                        value = apiKey,
                        onValueChange = { apiKey = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Gemini API key") },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = model,
                        onValueChange = { model = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Gemini model") },
                        singleLine = true
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            storage.put(SecureStorage.KEY_GEMINI_API_KEY, apiKey.trim())
                            storage.put(SecureStorage.KEY_GEMINI_MODEL, model.trim())
                            testResult = "AI settings encrypted locally."
                        }) { Text("Save") }
                        OutlinedButton(onClick = {
                            scope.launch { testResult = processor.testAI() }
                        }) { Text("Test AI") }
                    }
                    OutlinedButton(onClick = {
                        NativeActions.openWeb(context, "https://aistudio.google.com/app/apikey")
                    }) { Text("Google AI Studio") }
                    if (testResult.isNotBlank()) Text(testResult)
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("2) Always-available Agent Service", style = MaterialTheme.typography.titleMedium)
                    Text("App screen بند ہونے کے بعد service foreground notification کے ساتھ چل سکتی ہے۔ Android/OEM microphone restrictions کے باعث hidden/unrestricted listening کی guarantee نہیں ہے۔")
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(onClick = {
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(arrayOf(Manifest.permission.RECORD_AUDIO))
                            } else {
                                activity?.startAgentService()
                            }
                        }) { Text("START AGENT") }
                        OutlinedButton(onClick = { activity?.stopAgentService() }) { Text("STOP") }
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = {
                            wakeOnly = !wakeOnly
                            storage.put(SecureStorage.KEY_WAKE_ONLY, wakeOnly.toString())
                        }) { Text(if (wakeOnly) "Wake phrase: ON" else "All speech: ON") }
                        OutlinedButton(onClick = { NativeActions.openOverlaySettings(context) }) { Text("Overlay") }
                    }
                }
            }
        }

        item {
            Card {
                Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("3) Permissions & Access", style = MaterialTheme.typography.titleMedium)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { NativeActions.openAccessibilitySettings(context) }) { Text("Accessibility") }
                        OutlinedButton(onClick = { NativeActions.openNotificationAccessSettings(context) }) { Text("Notifications") }
                    }
                    OutlinedButton(onClick = {
                        phonePermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.READ_PHONE_STATE,
                                Manifest.permission.ANSWER_PHONE_CALLS
                            )
                        )
                    }) { Text("Phone control permission") }
                    OutlinedButton(onClick = {
                        if (Build.VERSION.SDK_INT >= 23) {
                            context.startActivity(
                                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
                            )
                        }
                    }) { Text("Floating icon permission") }
                    Text("Accessibility صرف تب enable کریں جب آپ screen/app automation چاہتے ہوں۔ Notification access صرف تب دیں جب calls/notifications پڑھنے والی features چاہئیں۔ Phone control صرف call answer/end کے لیے دیں۔")
                }
            }
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = input,
                    onValueChange = { input = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("بات کریں…") },
                    enabled = !busy
                )
                Button(
                    onClick = { runCommand(input) },
                    enabled = input.isNotBlank() && !busy
                ) { Text("Send") }
            }
        }

        items(messages) { Text(it, Modifier.fillMaxWidth().padding(vertical = 4.dp)) }
    }
}
