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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
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
import com.example.model.ChapterStatus
import com.example.model.Difficulty
import com.example.ui.AppNavDestination
import com.example.ui.Target100ViewModel
import com.example.ui.i18n.AppStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun SyllabusScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.appLanguage.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val allChapters by viewModel.visibleChapters.collectAsState()

    var selectedSubjectId by remember(subjects) {
        mutableLongStateOf(subjects.firstOrNull()?.id ?: 1L)
    }

    var showAddChapterDialog by remember { mutableStateOf(false) }

    val currentSubject = subjects.find { it.id == selectedSubjectId }
    val subjectChapters = allChapters.filter { it.subjectId == selectedSubjectId }
    val verifiedInSubject = subjectChapters.count { it.status == ChapterStatus.VERIFIED_COMPLETED.name }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddChapterDialog = true },
                containerColor = MaterialTheme.colorScheme.secondary,
                contentColor = Color.White
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Chapter")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Horizontal Subject Selector Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { sub ->
                    val isSelected = sub.id == selectedSubjectId
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubjectId = sub.id },
                        label = {
                            Text(
                                text = if (currentLang == "hi") sub.nameHi else sub.nameEn,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            // Subject Header Card with Verification Progress
            if (currentSubject != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (currentLang == "hi") currentSubject.nameHi else currentSubject.nameEn,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "$verifiedInSubject / ${subjectChapters.size} ${AppStrings.get("syllabus_verified_badge", currentLang)}",
                                    color = MaterialTheme.colorScheme.primary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        val progress = if (subjectChapters.isNotEmpty()) verifiedInSubject.toFloat() / subjectChapters.size.toFloat() else 0f
                        LinearProgressIndicator(
                            progress = { progress.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.surface
                        )
                    }
                }
            }

            // Chapter Cards List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
            ) {
                items(subjectChapters, key = { it.id }) { chapter ->
                    ChapterSyllabusCard(
                        chapter = chapter,
                        currentLang = currentLang,
                        onVerifyWithTest = {
                            viewModel.startTest(
                                chapterId = chapter.id,
                                questionCount = 5,
                                difficulty = Difficulty.MEDIUM
                            )
                            viewModel.navigateTo(AppNavDestination.AI_TESTS)
                        }
                    )
                }
            }
        }
    }

    if (showAddChapterDialog && currentSubject != null) {
        AddChapterDialog(
            subjectName = if (currentLang == "hi") currentSubject.nameHi else currentSubject.nameEn,
            currentLang = currentLang,
            nextChapterNumber = subjectChapters.size + 1,
            onDismiss = { showAddChapterDialog = false },
            onAdd = { titleEn, titleHi ->
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.repository.insertChapter(
                        ChapterEntity(
                            subjectId = currentSubject.id,
                            chapterNumber = subjectChapters.size + 1,
                            titleEn = titleEn,
                            titleHi = titleHi,
                            status = ChapterStatus.NOT_STARTED.name,
                            configuredPassingPercent = 75,
                            displayOrder = subjectChapters.size + 1
                        )
                    )
                }
                showAddChapterDialog = false
            }
        )
    }
}

@Composable
fun ChapterSyllabusCard(
    chapter: ChapterEntity,
    currentLang: String,
    onVerifyWithTest: () -> Unit
) {
    val statusEnum = try {
        ChapterStatus.valueOf(chapter.status)
    } catch (_: Exception) {
        ChapterStatus.NOT_STARTED
    }

    val (statusLabel, statusColor, statusBg) = when (statusEnum) {
        ChapterStatus.VERIFIED_COMPLETED -> Triple("VERIFIED ✓", Color(0xFF10B981), Color(0xFF10B981).copy(alpha = 0.15f))
        ChapterStatus.STUDIED_AWAITING_VERIFICATION -> Triple("Awaiting Test ⏱", Color(0xFF3B82F6), Color(0xFF3B82F6).copy(alpha = 0.15f))
        ChapterStatus.NEEDS_REVISION -> Triple("Needs Revision ⚠", Color(0xFFEF4444), Color(0xFFEF4444).copy(alpha = 0.15f))
        ChapterStatus.IN_PROGRESS -> Triple("In Progress", Color(0xFFF59E0B), Color(0xFFF59E0B).copy(alpha = 0.15f))
        ChapterStatus.NOT_STARTED -> Triple("Not Started", Color(0xFF64748B), Color(0xFF64748B).copy(alpha = 0.15f))
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(modifier = Modifier.weight(1f), verticalAlignment = Alignment.Top) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${chapter.chapterNumber}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Text(
                            text = if (currentLang == "hi") chapter.titleHi else chapter.titleEn,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (currentLang == "hi" && chapter.titleEn != chapter.titleHi) {
                            Text(
                                text = chapter.titleEn,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Surface(shape = RoundedCornerShape(8.dp), color = statusBg) {
                    Text(
                        text = statusLabel,
                        color = statusColor,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Test stats & action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Threshold: ${chapter.configuredPassingPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (chapter.totalTestAttempts > 0) {
                        Text(
                            text = "Best: ${chapter.bestScorePercent}% (${chapter.totalTestAttempts} tests)",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (chapter.bestScorePercent >= chapter.configuredPassingPercent) Color(0xFF10B981) else Color(0xFFEF4444),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Button(
                    onClick = onVerifyWithTest,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (statusEnum == ChapterStatus.VERIFIED_COMPLETED) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f) else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (statusEnum == ChapterStatus.VERIFIED_COMPLETED) "Retest / Practice" else AppStrings.get("syllabus_test_btn", currentLang),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AddChapterDialog(
    subjectName: String,
    currentLang: String,
    nextChapterNumber: Int,
    onDismiss: () -> Unit,
    onAdd: (titleEn: String, titleHi: String) -> Unit
) {
    var titleEn by remember { mutableStateOf("") }
    var titleHi by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Add Chapter to $subjectName", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = titleEn,
                    onValueChange = { titleEn = it },
                    label = { Text("Chapter Title (English)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = titleHi,
                    onValueChange = { titleHi = it },
                    label = { Text("Chapter Title (हिन्दी)") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (titleEn.isNotBlank() || titleHi.isNotBlank()) {
                        val en = if (titleEn.isNotBlank()) titleEn.trim() else titleHi.trim()
                        val hi = if (titleHi.isNotBlank()) titleHi.trim() else titleEn.trim()
                        onAdd(en, hi)
                    }
                },
                enabled = titleEn.isNotBlank() || titleHi.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
