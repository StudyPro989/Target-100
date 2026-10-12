package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.RevisionSessionEntity
import com.example.data.local.entities.SubjectEntity
import com.example.model.Difficulty
import com.example.ui.AppNavDestination
import com.example.ui.Target100ViewModel
import com.example.ui.i18n.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RevisionScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.appLanguage.collectAsState()
    val weakChapters by viewModel.weakChapters.collectAsState()
    val revisionSessions by viewModel.revisionSessions.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val visibleChapters by viewModel.visibleChapters.collectAsState()

    var showScheduleDialog by remember { mutableStateOf(false) }

    val todayStr = viewModel.repository.getTodayDateString()

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showScheduleDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Schedule Revision")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
        ) {
            // Header Info Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = AppStrings.get("revision_title", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = AppStrings.get("revision_subtitle", currentLang),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                            )
                        }
                    }
                }
            }

            // Section 1: Smart Automatic Revision Suggestions
            item {
                Text(
                    text = AppStrings.get("revision_suggested", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (weakChapters.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = AppStrings.get("dash_all_chapters_strong", currentLang),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(weakChapters) { chapter ->
                    val subject = subjects.find { it.id == chapter.subjectId }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.15f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (currentLang == "hi") chapter.titleHi else chapter.titleEn,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${if (currentLang == "hi") subject?.nameHi else subject?.nameEn} • Best Score: ${chapter.bestScorePercent}% (Pass: ${chapter.configuredPassingPercent}%)",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.error
                                    )
                                }

                                Button(
                                    onClick = {
                                        viewModel.scheduleRevision(
                                            chapterId = chapter.id,
                                            subjectId = chapter.subjectId,
                                            scheduledDate = todayStr,
                                            notes = "Targeted revision for weak test score"
                                        )
                                    },
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Schedule", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // Section 2: Scheduled Revision Sessions
            item {
                Text(
                    text = AppStrings.get("revision_scheduled", currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            val pendingSessions = revisionSessions.filter { !it.isCompleted }
            if (pendingSessions.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Box(modifier = Modifier.padding(16.dp), contentAlignment = Alignment.Center) {
                            Text(
                                text = "No revision sessions scheduled. Use + to schedule one!",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            } else {
                items(pendingSessions) { session ->
                    val chapter = visibleChapters.find { it.id == session.chapterId }
                    val subject = subjects.find { it.id == session.subjectId }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (currentLang == "hi") chapter?.titleHi ?: "अध्याय" else chapter?.titleEn ?: "Chapter",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Scheduled: ${session.scheduledDate} • ${if (currentLang == "hi") subject?.nameHi else subject?.nameEn}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (session.notes.isNotBlank()) {
                                        Text(
                                            text = session.notes,
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    // Take Revision Quiz
                                    Button(
                                        onClick = {
                                            viewModel.startTest(
                                                chapterId = session.chapterId,
                                                questionCount = 5,
                                                difficulty = Difficulty.MEDIUM,
                                                isRevisionTest = true
                                            )
                                            viewModel.navigateTo(AppNavDestination.AI_TESTS)
                                        },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Quiz", fontSize = 11.sp)
                                    }

                                    // Mark Done
                                    OutlinedButton(
                                        onClick = { viewModel.completeRevision(session.id) },
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showScheduleDialog) {
        ScheduleRevisionDialog(
            subjects = subjects,
            chapters = visibleChapters,
            currentLang = currentLang,
            onDismiss = { showScheduleDialog = false },
            onSchedule = { chId, subId, date, notes ->
                viewModel.scheduleRevision(chId, subId, date, notes)
                showScheduleDialog = false
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleRevisionDialog(
    subjects: List<SubjectEntity>,
    chapters: List<ChapterEntity>,
    currentLang: String,
    onDismiss: () -> Unit,
    onSchedule: (chapterId: Long, subjectId: Long, date: String, notes: String) -> Unit
) {
    var selectedSubject by remember { mutableStateOf<SubjectEntity?>(subjects.firstOrNull()) }
    var selectedChapter by remember { mutableStateOf<ChapterEntity?>(chapters.find { it.subjectId == selectedSubject?.id }) }
    var notes by remember { mutableStateOf("") }

    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val todayStr = sdf.format(Calendar.getInstance().time)
    var dateStr by remember { mutableStateOf(todayStr) }

    var subjectExpanded by remember { mutableStateOf(false) }
    var chapterExpanded by remember { mutableStateOf(false) }

    val relevantChapters = chapters.filter { it.subjectId == selectedSubject?.id }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = AppStrings.get("revision_new_btn", currentLang), fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Subject Dropdown
                ExposedDropdownMenuBox(
                    expanded = subjectExpanded,
                    onExpandedChange = { subjectExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedSubject?.let { if (currentLang == "hi") it.nameHi else it.nameEn } ?: "Select Subject",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Subject") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = subjectExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = subjectExpanded,
                        onDismissRequest = { subjectExpanded = false }
                    ) {
                        subjects.forEach { sub ->
                            DropdownMenuItem(
                                text = { Text(if (currentLang == "hi") sub.nameHi else sub.nameEn) },
                                onClick = {
                                    selectedSubject = sub
                                    selectedChapter = chapters.find { it.subjectId == sub.id }
                                    subjectExpanded = false
                                }
                            )
                        }
                    }
                }

                // Chapter Dropdown
                ExposedDropdownMenuBox(
                    expanded = chapterExpanded,
                    onExpandedChange = { chapterExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedChapter?.let { if (currentLang == "hi") it.titleHi else it.titleEn } ?: "Select Chapter",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Chapter") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = chapterExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = chapterExpanded,
                        onDismissRequest = { chapterExpanded = false }
                    ) {
                        relevantChapters.forEach { ch ->
                            DropdownMenuItem(
                                text = { Text(if (currentLang == "hi") ch.titleHi else ch.titleEn) },
                                onClick = {
                                    selectedChapter = ch
                                    chapterExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = dateStr,
                    onValueChange = { dateStr = it },
                    label = { Text("Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Revision Notes") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val ch = selectedChapter
                    val sub = selectedSubject
                    if (ch != null && sub != null) {
                        onSchedule(ch.id, sub.id, dateStr.trim(), notes.trim())
                    }
                },
                enabled = selectedChapter != null
            ) {
                Text("Schedule")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
