package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AccentTheme
import com.example.model.AppThemeMode
import com.example.ui.Target100ViewModel
import com.example.ui.i18n.AppStrings
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentLang by viewModel.appLanguage.collectAsState()
    val themeMode by viewModel.appThemeMode.collectAsState()
    val accentTheme by viewModel.appAccentTheme.collectAsState()
    val examName by viewModel.examName.collectAsState()

    var customExamName by remember(examName) { mutableStateOf(examName) }
    var examDateStr by remember { mutableStateOf("2026-03-05") }

    var aiProvider by remember { mutableStateOf("GEMINI") }
    var aiModel by remember { mutableStateOf("gemini-3.5-flash") }
    var aiApiKey by remember { mutableStateOf("") }
    var aiEndpoint by remember { mutableStateOf("") }
    var dailyLimit by remember { mutableStateOf("15") }
    var keyVisible by remember { mutableStateOf(false) }

    var testConnResult by remember { mutableStateOf<String?>(null) }
    var testingConnection by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showImportDialog by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // 1. Language Toggle
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = AppStrings.get("settings_language", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        FilterChip(
                            selected = currentLang == "hi",
                            onClick = { viewModel.setLanguage("hi") },
                            label = { Text("हिन्दी (Hindi)") }
                        )
                        FilterChip(
                            selected = currentLang == "en",
                            onClick = { viewModel.setLanguage("en") },
                            label = { Text("English") }
                        )
                    }
                }
            }
        }

        // 2. Theme Mode & Accent Colors
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.DarkMode, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = AppStrings.get("settings_theme", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        AppThemeMode.values().forEach { mode ->
                            FilterChip(
                                selected = themeMode == mode,
                                onClick = { viewModel.setThemeMode(mode) },
                                label = { Text(mode.name) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = AppStrings.get("settings_accent", currentLang), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccentTheme.values().forEach { acc ->
                            val isSelected = accentTheme == acc
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(acc.primaryHex),
                                modifier = Modifier
                                    .size(44.dp)
                                    .clickable { viewModel.setAccentTheme(acc) }
                            ) {
                                if (isSelected) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 3. Board Exam Countdown Configuration
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = AppStrings.get("settings_exam_config", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = customExamName,
                        onValueChange = { customExamName = it },
                        label = { Text("Exam Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = examDateStr,
                        onValueChange = { examDateStr = it },
                        label = { Text("Target Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            viewModel.updateExamConfig(customExamName, examDateStr)
                            Toast.makeText(context, "Exam settings saved!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Save Exam Countdown")
                    }
                }
            }
        }

        // 4. AI Provider & API Key
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Key, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = AppStrings.get("settings_ai_config", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = aiProvider == "GEMINI",
                            onClick = {
                                aiProvider = "GEMINI"
                                aiModel = "gemini-3.5-flash"
                            },
                            label = { Text("Google Gemini") }
                        )
                        FilterChip(
                            selected = aiProvider == "OPENAI_COMPATIBLE",
                            onClick = {
                                aiProvider = "OPENAI_COMPATIBLE"
                                aiModel = "gpt-4o-mini"
                            },
                            label = { Text("OpenAI / Custom") }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = aiModel,
                        onValueChange = { aiModel = it },
                        label = { Text("Model (e.g. gemini-3.5-flash)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = aiApiKey,
                        onValueChange = { aiApiKey = it },
                        label = { Text("API Key (Stored locally in secure settings)") },
                        placeholder = { Text(AppStrings.get("settings_ai_key_hint", currentLang)) },
                        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { keyVisible = !keyVisible }) {
                                Icon(imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility, contentDescription = null)
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    if (aiProvider == "OPENAI_COMPATIBLE") {
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = aiEndpoint,
                            onValueChange = { aiEndpoint = it },
                            label = { Text("Endpoint URL (e.g. https://api.openai.com/v1)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = dailyLimit,
                        onValueChange = { dailyLimit = it },
                        label = { Text(AppStrings.get("settings_daily_limit", currentLang)) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                viewModel.saveAiConfig(aiProvider, aiModel, aiApiKey, aiEndpoint, dailyLimit)
                                Toast.makeText(context, "AI Settings saved!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Save AI Settings")
                        }

                        OutlinedButton(
                            onClick = {
                                testingConnection = true
                                testConnResult = null
                                viewModel.saveAiConfig(aiProvider, aiModel, aiApiKey, aiEndpoint, dailyLimit)
                                viewModel.testAiConnection { res ->
                                    testingConnection = false
                                    testConnResult = if (res.success) "✓ ${res.message}" else "✗ ${res.message}"
                                }
                            },
                            enabled = !testingConnection,
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            if (testingConnection) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp))
                            } else {
                                Text(AppStrings.get("settings_test_conn", currentLang))
                            }
                        }
                    }

                    if (testConnResult != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = testConnResult!!,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (testConnResult!!.startsWith("✓")) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // 5. Backup & Restore Data
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = AppStrings.get("settings_data_backup", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                scope.launch {
                                    val json = viewModel.exportDataJson()
                                    val intent = Intent(Intent.ACTION_SEND).apply {
                                        type = "application/json"
                                        putExtra(Intent.EXTRA_SUBJECT, "target100_backup.json")
                                        putExtra(Intent.EXTRA_TEXT, json)
                                    }
                                    context.startActivity(Intent.createChooser(intent, "Share/Save Target 100 Backup"))
                                }
                            },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Export Backup (JSON)", fontSize = 12.sp)
                        }

                        OutlinedButton(
                            onClick = { showImportDialog = true },
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Import Backup", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        // 6. Security & About
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "Privacy & Data Ownership", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "• 100% Offline-First: All targets, scores, and streak data remain on your device.\n• No account or login required.\n• API keys are saved securely in local storage and never sent to any third-party app server.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            title = { Text(text = "Import Backup Data", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Paste your Target 100 backup JSON string below:")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = importJsonText,
                        onValueChange = { importJsonText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            val res = viewModel.importDataJson(importJsonText)
                            if (res.isSuccess) {
                                Toast.makeText(context, "Backup restored successfully!", Toast.LENGTH_SHORT).show()
                                showImportDialog = false
                            } else {
                                Toast.makeText(context, "Failed: ${res.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                            }
                        }
                    },
                    enabled = importJsonText.isNotBlank()
                ) {
                    Text("Import")
                }
            },
            dismissButton = {
                TextButton(onClick = { showImportDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
