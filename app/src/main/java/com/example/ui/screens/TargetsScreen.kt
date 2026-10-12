package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.TargetEntity
import com.example.model.Priority
import com.example.model.TargetStatus
import com.example.model.TargetType
import com.example.ui.Target100ViewModel
import com.example.ui.components.TargetVerificationDialog
import com.example.ui.i18n.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TargetsScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.appLanguage.collectAsState()
    val allTargets by viewModel.allTargets.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val visibleChapters by viewModel.visibleChapters.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showCreateDialog by remember { mutableStateOf(false) }
    var promptVerificationTarget by remember { mutableStateOf<TargetEntity?>(null) }
    var alreadyCompletedTargetNotice by remember { mutableStateOf<TargetEntity?>(null) }
    var feedbackMessage by remember { mutableStateOf<String?>(null) }

    val todayStr = viewModel.repository.getTodayDateString()

    val filteredTargets = when (selectedTabIndex) {
        0 -> allTargets.filter { it.targetDate == todayStr && it.status != TargetStatus.COMPLETED.name }
        1 -> allTargets.filter { it.targetDate > todayStr && it.status != TargetStatus.COMPLETED.name }
        2 -> allTargets.filter { it.status == TargetStatus.COMPLETED.name }
        else -> allTargets.filter { it.rescheduleCount > 0 }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Target")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Feedback notification banner
            feedbackMessage?.let { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEF4444).copy(alpha = 0.12f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFEF4444).copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = msg,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFB91C1C),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { feedbackMessage = null }, modifier = Modifier.size(24.dp)) {
                            Text("✕", color = Color(0xFFB91C1C), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Tabs
            ScrollableTabRow(
                selectedTabIndex = selectedTabIndex,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                val tabs = listOf(
                    AppStrings.get("targets_today", currentLang),
                    AppStrings.get("targets_upcoming", currentLang),
                    AppStrings.get("targets_completed", currentLang),
                    AppStrings.get("target_rescheduled", currentLang)
                )
                tabs.forEachIndexed { index, tabTitle ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = tabTitle,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Rollover explanation notice
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppStrings.get("target_rollover_notice", currentLang),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Target List
            if (filteredTargets.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No targets in this category.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 4.dp, bottom = 80.dp)
                ) {
                    items(filteredTargets, key = { it.id }) { target ->
                        val subName = subjects.find { it.id == target.subjectId }?.let {
                            if (currentLang == "hi") it.nameHi else it.nameEn
                        }
                        val chapName = visibleChapters.find { it.id == target.chapterId }?.let {
                            if (currentLang == "hi") it.titleHi else it.titleEn
                        }

                        TargetItemCard(
                            target = target,
                            subjectName = subName,
                            chapterName = chapName,
                            currentLang = currentLang,
                            onComplete = {
                                if (target.status == TargetStatus.COMPLETED.name) {
                                    alreadyCompletedTargetNotice = target
                                } else {
                                    promptVerificationTarget = target
                                }
                            },
                            onDelete = { viewModel.deleteTarget(target) }
                        )
                    }
                }
            }
        }
    }

    if (promptVerificationTarget != null) {
        val tgt = promptVerificationTarget!!
        val sub = subjects.find { it.id == tgt.subjectId }
        val chap = visibleChapters.find { it.id == tgt.chapterId }
        TargetVerificationDialog(
            target = tgt,
            subjectName = if (currentLang == "hi") sub?.nameHi else sub?.nameEn,
            chapterName = if (currentLang == "hi") chap?.titleHi else chap?.titleEn,
            currentLang = currentLang,
            onTakeTest = { targetToTest ->
                promptVerificationTarget = null
                feedbackMessage = null
                viewModel.startTestForTarget(targetToTest)
            },
            onDecline = {
                // Denied: task remains incomplete until tested
                promptVerificationTarget = null
                feedbackMessage = "Verification test declined: Task '${tgt.title}' remains INCOMPLETE until tested."
            }
        )
    }

    if (alreadyCompletedTargetNotice != null) {
        val tgt = alreadyCompletedTargetNotice!!
        AlertDialog(
            onDismissRequest = { alreadyCompletedTargetNotice = null },
            title = {
                Text(
                    text = "Target Already Verified",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "This target '${tgt.title}' was already completed by passing its verification test with >=75%! Would you like to retake a test to practice again?",
                    style = MaterialTheme.typography.bodyMedium
                )
            },
            confirmButton = {
                Button(onClick = { alreadyCompletedTargetNotice = null }) {
                    Text("OK")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        val toTest = alreadyCompletedTargetNotice!!
                        alreadyCompletedTargetNotice = null
                        viewModel.startTestForTarget(toTest)
                    }
                ) {
                    Text("Retake Test")
                }
            }
        )
    }

    if (showCreateDialog) {
        CreateTargetDialog(
            subjects = subjects,
            chapters = visibleChapters,
            currentLang = currentLang,
            onDismiss = { showCreateDialog = false },
            onCreate = { title, subId, chId, type, date, priority, notes ->
                viewModel.createTarget(
                    title = title,
                    subjectId = subId,
                    chapterId = chId,
                    type = type,
                    targetDate = date,
                    priority = priority,
                    notes = notes,
                    reminderTime = "18:00"
                )
                showCreateDialog = false
            }
        )
    }
}

@Composable
fun TargetItemCard(
    target: TargetEntity,
    subjectName: String?,
    chapterName: String?,
    currentLang: String,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val isDone = target.status == TargetStatus.COMPLETED.name

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f) else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onComplete,
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDone) Color(0xFF10B981) else MaterialTheme.colorScheme.surface)
                ) {
                    Icon(
                        imageVector = if (isDone) Icons.Default.Check else Icons.Default.HourglassEmpty,
                        contentDescription = "Complete",
                        tint = if (isDone) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = target.title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                    )
                    if (target.notes.isNotBlank()) {
                        Text(
                            text = target.notes,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Tags Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Priority Tag
                val priColor = when (target.priority) {
                    "HIGH" -> Color(0xFFEF4444)
                    "MEDIUM" -> Color(0xFFF59E0B)
                    else -> Color(0xFF10B981)
                }
                Surface(shape = RoundedCornerShape(6.dp), color = priColor.copy(alpha = 0.15f)) {
                    Text(
                        text = target.priority,
                        color = priColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                // Subject tag
                if (!subjectName.isNullOrBlank()) {
                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                        Text(
                            text = subjectName,
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Due date
                Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surface) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = target.targetDate,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Rescheduled badge
                if (target.rescheduleCount > 0) {
                    Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFEF4444).copy(alpha = 0.15f)) {
                        Text(
                            text = "Rolled ${target.rescheduleCount}x",
                            color = Color(0xFFEF4444),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTargetDialog(
    subjects: List<SubjectEntity>,
    chapters: List<ChapterEntity>,
    currentLang: String,
    onDismiss: () -> Unit,
    onCreate: (title: String, subjectId: Long?, chapterId: Long?, type: TargetType, date: String, priority: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(subjects.firstOrNull()) }
    var selectedChapter by remember { mutableStateOf<ChapterEntity?>(null) }
    var selectedType by remember { mutableStateOf(TargetType.CHAPTER_STUDY) }
    var priority by remember { mutableStateOf("MEDIUM") }
    var notes by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Calendar.getInstance().time)
    var targetDate by remember { mutableStateOf(todayStr) }

    var subjectMenuExpanded by remember { mutableStateOf(false) }
    var chapterMenuExpanded by remember { mutableStateOf(false) }

    val relevantChapters = chapters.filter { it.subjectId == selectedSubject?.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = AppStrings.get("target_new_dialog_title", currentLang), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title / कार्य का नाम") },
                    placeholder = { Text(AppStrings.get("target_title_hint", currentLang)) },
                    modifier = Modifier.fillMaxWidth()
                )

                // Subject Picker
                ExposedDropdownMenuBox(
                    expanded = subjectMenuExpanded,
                    onExpandedChange = { subjectMenuExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.let { if (currentLang == "hi") it.nameHi else it.nameEn } ?: "Select Subject",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(AppStrings.get("target_subject", currentLang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectMenuExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectMenuExpanded,
                        onDismissRequest = { subjectMenuExpanded = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(if (currentLang == "hi") sub.nameHi else sub.nameEn) },
                                onClick = {
                                    selectedSubject = sub
                                    selectedChapter = null
                                    subjectMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Chapter Picker
                if (relevantChapters.isNotEmpty()) {
                    ExposedDropdownMenuBox(
                        expanded = chapterMenuExpanded,
                        onExpandedChange = { chapterMenuExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = selectedChapter?.let { if (currentLang == "hi") it.titleHi else it.titleEn } ?: "Select Chapter (Optional)",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(AppStrings.get("target_chapter", currentLang)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chapterMenuExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = chapterMenuExpanded,
                            onDismissRequest = { chapterMenuExpanded = false }
                        ) {
                            relevantChapters.forEach { chap ->
                                DropdownMenuItem(
                                    text = { Text(if (currentLang == "hi") chap.titleHi else chap.titleEn) },
                                    onClick = {
                                        selectedChapter = chap
                                        chapterMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Priority Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("LOW", "MEDIUM", "HIGH").forEach { p ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (priority == p) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .weight(1f)
                                .clickable { priority = p }
                        ) {
                            Text(
                                text = p,
                                color = if (priority == p) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text(AppStrings.get("target_notes", currentLang)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onCreate(
                            title.trim(),
                            selectedSubject?.id,
                            selectedChapter?.id,
                            selectedType,
                            targetDate,
                            priority,
                            notes.trim()
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(AppStrings.get("ai_test_cancel", currentLang))
            }
        }
    )
}
