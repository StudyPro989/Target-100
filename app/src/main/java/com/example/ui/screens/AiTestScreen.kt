package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NavigateBefore
import androidx.compose.material.icons.filled.NavigateNext
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import com.example.model.Difficulty
import com.example.model.QuestionModel
import com.example.model.QuestionReviewItem
import com.example.model.TestResultModel
import com.example.ui.ActiveTestSession
import com.example.ui.AppNavDestination
import com.example.ui.Target100ViewModel
import com.example.ui.i18n.AppStrings

@Composable
fun AiTestScreen(
    viewModel: Target100ViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.appLanguage.collectAsState()
    val activeTest by viewModel.activeTest.collectAsState()
    val lastResult by viewModel.lastTestResult.collectAsState()
    val isGenerating by viewModel.isGeneratingTest.collectAsState()
    val genError by viewModel.testGenerationError.collectAsState()
    val mistakeQuestions by viewModel.mistakePracticeQuestions.collectAsState()

    when {
        activeTest != null -> {
            ActiveTestRunner(
                session = activeTest!!,
                currentLang = currentLang,
                onSelectAnswer = { qId, optIdx -> viewModel.selectAnswer(qId, optIdx) },
                onNavigateQuestion = { idx -> viewModel.goToQuestion(idx) },
                onSubmit = { viewModel.submitCurrentTest() }
            )
        }
        lastResult != null -> {
            TestResultView(
                result = lastResult!!,
                currentLang = currentLang,
                onRetake = { viewModel.retakeCurrentTest() },
                onPracticeMistakes = { viewModel.startMistakePractice() },
                onBackToSyllabus = {
                    viewModel.navigateTo(AppNavDestination.SYLLABUS)
                }
            )
        }
        mistakeQuestions != null -> {
            MistakePracticeView(
                questions = mistakeQuestions!!,
                currentLang = currentLang,
                onFinish = { viewModel.clearMistakePractice() }
            )
        }
        else -> {
            TestConfigView(
                viewModel = viewModel,
                isGenerating = isGenerating,
                errorMessage = genError,
                currentLang = currentLang
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestConfigView(
    viewModel: Target100ViewModel,
    isGenerating: Boolean,
    errorMessage: String?,
    currentLang: String
) {
    val subjects by viewModel.subjects.collectAsState()
    val chapters by viewModel.visibleChapters.collectAsState()
    val quota by viewModel.aiQuota.collectAsState()

    var selectedSubject by remember(subjects) { mutableStateOf(subjects.firstOrNull()) }
    val relevantChapters = chapters.filter { it.subjectId == selectedSubject?.id }
    var selectedChapter by remember(relevantChapters) { mutableStateOf(relevantChapters.firstOrNull()) }

    var questionCount by remember { mutableIntStateOf(5) }
    var difficulty by remember { mutableStateOf(Difficulty.MEDIUM) }

    var subjectExpanded by remember { mutableStateOf(false) }
    var chapterExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Core Rule Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppStrings.get("ai_tests_rule_notice", currentLang),
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Daily AI Quota Indicator
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Daily AI Quota: ${quota.second} of ${quota.first} remaining",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)) {
                        Text(
                            text = "Offline Bank Ready",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Subject Selector
        item {
            Text(text = AppStrings.get("ai_test_select_subject", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = subjectExpanded,
                onExpandedChange = { subjectExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedSubject?.let { if (currentLang == "hi") it.nameHi else it.nameEn } ?: "Select Subject",
                    onValueChange = {},
                    readOnly = true,
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
        }

        // Chapter Selector
        item {
            Text(text = AppStrings.get("ai_test_select_chapter", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            ExposedDropdownMenuBox(
                expanded = chapterExpanded,
                onExpandedChange = { chapterExpanded = it }
            ) {
                OutlinedTextField(
                    value = selectedChapter?.let { if (currentLang == "hi") it.titleHi else it.titleEn } ?: "Select Chapter",
                    onValueChange = {},
                    readOnly = true,
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
        }

        // Question Count Filter Chips (5, 10, 20, 30)
        item {
            Text(text = AppStrings.get("ai_test_questions_count", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf(5, 10, 20).forEach { cnt ->
                    FilterChip(
                        selected = questionCount == cnt,
                        onClick = { questionCount = cnt },
                        label = { Text("$cnt MCQs") }
                    )
                }
            }
        }

        // Difficulty Chips (Easy, Medium, Hard)
        item {
            Text(text = AppStrings.get("ai_test_difficulty", currentLang), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Difficulty.values().forEach { diff ->
                    FilterChip(
                        selected = difficulty == diff,
                        onClick = { difficulty = diff },
                        label = { Text(diff.name) }
                    )
                }
            }
        }

        // Error message if any
        if (!errorMessage.isNullOrBlank()) {
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Text(
                        text = errorMessage,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }

        // Start Test Button
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val ch = selectedChapter
                    if (ch != null) {
                        viewModel.startTest(
                            chapterId = ch.id,
                            questionCount = questionCount,
                            difficulty = difficulty
                        )
                    }
                },
                enabled = selectedChapter != null && !isGenerating,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = AppStrings.get("ai_test_generating", currentLang))
                } else {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = AppStrings.get("ai_test_start", currentLang), fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ActiveTestRunner(
    session: ActiveTestSession,
    currentLang: String,
    onSelectAnswer: (questionId: Int, optionIndex: Int) -> Unit,
    onNavigateQuestion: (index: Int) -> Unit,
    onSubmit: () -> Unit
) {
    val qIndex = session.currentQuestionIndex
    val currentQuestion = session.questions.getOrNull(qIndex) ?: return
    val selectedOption = session.userAnswers[currentQuestion.id]

    var showSubmitConfirm by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header: Subject, Chapter & Progress
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = session.chapterTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${AppStrings.get("ai_test_taking_q", currentLang)} ${qIndex + 1} of ${session.questions.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            }

            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Text(
                    text = "${session.userAnswers.size} / ${session.questions.size} Answered",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))
        val progress = (qIndex + 1).toFloat() / session.questions.size.toFloat()
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Question Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = currentQuestion.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(currentQuestion.options) { optIdx, optText ->
                    val isSelected = selectedOption == optIdx
                    val optionLetters = listOf("A", "B", "C", "D")

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 5.dp)
                            .clickable { onSelectAnswer(currentQuestion.id, optIdx) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                        ),
                        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) Color.White.copy(alpha = 0.3f) else MaterialTheme.colorScheme.primaryContainer
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = optionLetters.getOrElse(optIdx) { "${optIdx + 1}" },
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onPrimaryContainer,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = optText,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Pagination & Navigation Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { onNavigateQuestion(qIndex - 1) },
                enabled = qIndex > 0,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.NavigateBefore, contentDescription = null)
                Text(text = AppStrings.get("ai_test_prev", currentLang))
            }

            if (qIndex == session.questions.size - 1) {
                Button(
                    onClick = { showSubmitConfirm = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = AppStrings.get("ai_test_submit", currentLang), fontWeight = FontWeight.Bold)
                }
            } else {
                Button(
                    onClick = { onNavigateQuestion(qIndex + 1) },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = AppStrings.get("ai_test_next", currentLang))
                    Icon(imageVector = Icons.Default.NavigateNext, contentDescription = null)
                }
            }
        }
    }

    if (showSubmitConfirm) {
        AlertDialog(
            onDismissRequest = { showSubmitConfirm = false },
            title = { Text(text = AppStrings.get("ai_test_submit", currentLang), fontWeight = FontWeight.Bold) },
            text = {
                val answeredCount = session.userAnswers.size
                val total = session.questions.size
                Text(
                    text = "${AppStrings.get("ai_test_confirm_submit", currentLang)}\n\nAnswered: $answeredCount of $total questions."
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSubmitConfirm = false
                        onSubmit()
                    }
                ) {
                    Text(AppStrings.get("ai_test_confirm_yes", currentLang))
                }
            },
            dismissButton = {
                TextButton(onClick = { showSubmitConfirm = false }) {
                    Text(AppStrings.get("ai_test_cancel", currentLang))
                }
            }
        )
    }
}

@Composable
fun TestResultView(
    result: TestResultModel,
    currentLang: String,
    onRetake: () -> Unit,
    onPracticeMistakes: () -> Unit,
    onBackToSyllabus: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Score & Verification Result Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.isPassed) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFEF4444).copy(alpha = 0.15f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = if (result.isPassed) Icons.Default.CheckCircle else Icons.Default.Close,
                        contentDescription = null,
                        tint = if (result.isPassed) Color(0xFF10B981) else Color(0xFFEF4444),
                        modifier = Modifier.size(54.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (result.isPassed) AppStrings.get("results_passed", currentLang) else AppStrings.get("results_failed", currentLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (result.isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "${result.scorePercent}%",
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Black,
                        color = if (result.isPassed) Color(0xFF10B981) else Color(0xFFEF4444)
                    )

                    Text(
                        text = "${result.correctAnswers} / ${result.totalQuestions} Correct • Required Threshold: ${result.passingThreshold}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (result.chapterVerifiedNow) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF10B981)
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Stars, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "OFFICIALLY VERIFIED & TASK COMPLETED • +150 XP",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    } else if (!result.isPassed) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEF4444).copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "TASK REMAINS INCOMPLETE UNTIL TEST IS PASSED",
                                color = Color(0xFFDC2626),
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row (Practice Mistakes / Retake Fresh / Back to Syllabus)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (!result.isPassed) {
                    Button(
                        onClick = onPracticeMistakes,
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(AppStrings.get("results_mistake_practice", currentLang), fontSize = 12.sp)
                    }
                }

                Button(
                    onClick = onRetake,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppStrings.get("results_fresh_test", currentLang), fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = onBackToSyllabus,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(AppStrings.get("results_back", currentLang), fontSize = 12.sp)
                }
            }
        }

        // Detailed Question Review
        item {
            Text(
                text = AppStrings.get("results_review", currentLang),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        itemsIndexed(result.questions) { idx, q ->
            QuestionReviewCard(index = idx + 1, item = q)
        }
    }
}

@Composable
fun QuestionReviewCard(index: Int, item: QuestionReviewItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (item.isCorrect) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(
                    text = "Q$index: ${item.questionText}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    imageVector = if (item.isCorrect) Icons.Default.CheckCircle else Icons.Default.Close,
                    contentDescription = null,
                    tint = if (item.isCorrect) Color(0xFF10B981) else Color(0xFFEF4444),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Options display
            item.options.forEachIndexed { optIdx, optText ->
                val isCorrectAnswer = optIdx == item.correctIndex
                val isStudentAnswer = optIdx == item.selectedIndex

                val bgColor = when {
                    isCorrectAnswer -> Color(0xFF10B981).copy(alpha = 0.15f)
                    isStudentAnswer && !item.isCorrect -> Color(0xFFEF4444).copy(alpha = 0.15f)
                    else -> MaterialTheme.colorScheme.surface
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = bgColor,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${listOf("A", "B", "C", "D")[optIdx]}. $optText",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = if (isCorrectAnswer || isStudentAnswer) FontWeight.Bold else FontWeight.Normal,
                            color = when {
                                isCorrectAnswer -> Color(0xFF10B981)
                                isStudentAnswer && !item.isCorrect -> Color(0xFFEF4444)
                                else -> MaterialTheme.colorScheme.onSurface
                            }
                        )
                        if (isCorrectAnswer) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Correct Answer", color = Color(0xFF10B981), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        } else if (isStudentAnswer) {
                            Spacer(modifier = Modifier.weight(1f))
                            Text(text = "Your Answer", color = Color(0xFFEF4444), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Explanation: ${item.explanation}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun MistakePracticeView(
    questions: List<QuestionModel>,
    currentLang: String,
    onFinish: () -> Unit
) {
    var currentIndex by remember { mutableIntStateOf(0) }
    var selectedOption by remember { mutableStateOf<Int?>(null) }
    var showExplanation by remember { mutableStateOf(false) }

    val currentQ = questions.getOrNull(currentIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mistake Practice (${currentIndex + 1} of ${questions.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Button(onClick = onFinish, shape = RoundedCornerShape(10.dp)) {
                Text("Done")
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            LazyColumn(modifier = Modifier.padding(16.dp)) {
                item {
                    Text(
                        text = currentQ.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                itemsIndexed(currentQ.options) { optIdx, optText ->
                    val isCorrect = optIdx == currentQ.correctIndex
                    val isSelected = selectedOption == optIdx

                    val bg = when {
                        showExplanation && isCorrect -> Color(0xFF10B981).copy(alpha = 0.2f)
                        showExplanation && isSelected && !isCorrect -> Color(0xFFEF4444).copy(alpha = 0.2f)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !showExplanation) {
                                selectedOption = optIdx
                                showExplanation = true
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = bg)
                    ) {
                        Text(
                            text = "${listOf("A", "B", "C", "D")[optIdx]}. $optText",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = if (isSelected || (showExplanation && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                if (showExplanation) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Explanation:",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = currentQ.explanation,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = {
                    if (currentIndex > 0) {
                        currentIndex--
                        selectedOption = null
                        showExplanation = false
                    }
                },
                enabled = currentIndex > 0
            ) {
                Text("Previous")
            }

            Button(
                onClick = {
                    if (currentIndex < questions.size - 1) {
                        currentIndex++
                        selectedOption = null
                        showExplanation = false
                    } else {
                        onFinish()
                    }
                }
            ) {
                Text(if (currentIndex < questions.size - 1) "Next" else "Finish Practice")
            }
        }
    }
}
