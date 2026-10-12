package com.example.ui.screens

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ChapterEntity
import com.example.model.ChapterStatus
import com.example.ui.Target100ViewModel
import com.example.ui.i18n.AppStrings
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun ReportsScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val currentLang by viewModel.appLanguage.collectAsState()
    val allTargets by viewModel.allTargets.collectAsState()
    val allAttempts by viewModel.allAttempts.collectAsState()
    val visibleChapters by viewModel.visibleChapters.collectAsState()
    val subjects by viewModel.subjects.collectAsState()
    val totalXp by viewModel.totalXp.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()

    val completedTargets = allTargets.count { it.status == "COMPLETED" }
    val totalAttempts = allAttempts.size
    val avgScore = if (totalAttempts > 0) (allAttempts.map { it.scorePercent }.average()).toInt() else 0
    val verifiedCount = visibleChapters.count { it.status == ChapterStatus.VERIFIED_COMPLETED.name }
    val weakChapters = visibleChapters.filter { it.status == ChapterStatus.NEEDS_REVISION.name || (it.totalTestAttempts > 0 && it.bestScorePercent < it.configuredPassingPercent) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp)
    ) {
        // Hero Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppStrings.get("reports_title", currentLang),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Text(
                                text = "RBSE Class 10 Verification Audit",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Button(
                            onClick = {
                                shareWeeklyReport(
                                    context = context,
                                    completedTargets = completedTargets,
                                    totalAttempts = totalAttempts,
                                    avgScore = avgScore,
                                    verifiedCount = verifiedCount,
                                    totalChapters = visibleChapters.size,
                                    totalXp = totalXp,
                                    currentStreak = currentStreak
                                )
                            },
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Export", fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 4 Grid Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportMetricItem(
                            modifier = Modifier.weight(1f),
                            title = AppStrings.get("reports_targets_completed", currentLang),
                            value = "$completedTargets"
                        )
                        ReportMetricItem(
                            modifier = Modifier.weight(1f),
                            title = AppStrings.get("reports_tests_taken", currentLang),
                            value = "$totalAttempts"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        ReportMetricItem(
                            modifier = Modifier.weight(1f),
                            title = AppStrings.get("reports_avg_score", currentLang),
                            value = "$avgScore%"
                        )
                        ReportMetricItem(
                            modifier = Modifier.weight(1f),
                            title = AppStrings.get("reports_xp_gained", currentLang),
                            value = "$totalXp XP"
                        )
                    }
                }
            }
        }

        // Subject Breakdown
        item {
            Text(
                text = "Subject-Wise Verified Progress",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(subjects) { sub ->
            val subChapters = visibleChapters.filter { it.subjectId == sub.id }
            val subVerified = subChapters.count { it.status == ChapterStatus.VERIFIED_COMPLETED.name }
            val progress = if (subChapters.isNotEmpty()) subVerified.toFloat() / subChapters.size.toFloat() else 0f

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLang == "hi") sub.nameHi else sub.nameEn,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$subVerified / ${subChapters.size} Chapters (${(progress * 100).toInt()}%)",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))
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

        // Weak vs Strong Summary
        if (weakChapters.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Warning, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Focus Chapters (${weakChapters.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            items(weakChapters) { ch ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (currentLang == "hi") ch.titleHi else ch.titleEn,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Best: ${ch.bestScorePercent}%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ReportMetricItem(
    modifier: Modifier = Modifier,
    title: String,
    value: String
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun shareWeeklyReport(
    context: Context,
    completedTargets: Int,
    totalAttempts: Int,
    avgScore: Int,
    verifiedCount: Int,
    totalChapters: Int,
    totalXp: Int,
    currentStreak: Int
) {
    val dateStr = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date())
    val text = """
========================================
    TARGET 100 • WEEKLY AUDIT REPORT
       RBSE Class 10 (Rajasthan)
========================================
Generated On: $dateStr

STUDY PERFORMANCE:
• Completed Study Targets: $completedTargets
• Chapter Tests Attempted: $totalAttempts
• Average Test Accuracy: $avgScore%
• Verified Chapters Passed: $verifiedCount of $totalChapters
• Current Daily Study Streak: $currentStreak days
• Total Earned XP: $totalXp XP

CORE PRINCIPLE VERIFICATION:
"Completing a task is not proof of learning.
A chapter is officially verified only when its
passing threshold test is successfully cleared."

Target 100 • Aiming for 100% in RBSE Class 10
========================================
    """.trimIndent()

    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Target 100 Weekly Performance Report")
        putExtra(Intent.EXTRA_TEXT, text)
    }
    context.startActivity(Intent.createChooser(intent, "Share Target 100 Report"))
}
