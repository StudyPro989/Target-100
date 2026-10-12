package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.ConnectionTestResult
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.RevisionSessionEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.TargetEntity
import com.example.data.local.entities.TestAttemptEntity
import com.example.data.repository.Target100Repository
import com.example.model.AccentTheme
import com.example.model.AppThemeMode
import com.example.model.BadgeDefinition
import com.example.model.Difficulty
import com.example.model.QuestionModel
import com.example.model.TargetType
import com.example.model.TestResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class AppNavDestination {
    DASHBOARD,
    TARGETS,
    SYLLABUS,
    REVISION,
    AI_TESTS,
    REPORTS,
    ACHIEVEMENTS,
    SETTINGS
}

data class ActiveTestSession(
    val chapterId: Long,
    val subjectId: Long,
    val chapterTitle: String,
    val subjectTitle: String,
    val questions: List<QuestionModel>,
    val providerUsed: String,
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<Int, Int> = emptyMap(), // questionId -> selectedIndex
    val isRevisionTest: Boolean = false,
    val linkedTargetId: Long? = null,
    val startTimeMs: Long = System.currentTimeMillis()
)

class Target100ViewModel(application: Application) : AndroidViewModel(application) {

    val repository = Target100Repository(application)

    // Navigation
    private val _currentDestination = MutableStateFlow(AppNavDestination.DASHBOARD)
    val currentDestination: StateFlow<AppNavDestination> = _currentDestination.asStateFlow()

    // Settings & State
    private val _appLanguage = MutableStateFlow("en")
    val appLanguage: StateFlow<String> = _appLanguage.asStateFlow()

    private val _appThemeMode = MutableStateFlow(AppThemeMode.SYSTEM)
    val appThemeMode: StateFlow<AppThemeMode> = _appThemeMode.asStateFlow()

    private val _appAccentTheme = MutableStateFlow(AccentTheme.GOLD_RAJPUTANA)
    val appAccentTheme: StateFlow<AccentTheme> = _appAccentTheme.asStateFlow()

    private val _examName = MutableStateFlow("RBSE 10th Board Exam 2026")
    val examName: StateFlow<String> = _examName.asStateFlow()

    private val _examDaysRemaining = MutableStateFlow(145)
    val examDaysRemaining: StateFlow<Int> = _examDaysRemaining.asStateFlow()

    // Active AI Test State
    private val _activeTest = MutableStateFlow<ActiveTestSession?>(null)
    val activeTest: StateFlow<ActiveTestSession?> = _activeTest.asStateFlow()

    private val _lastTestResult = MutableStateFlow<TestResultModel?>(null)
    val lastTestResult: StateFlow<TestResultModel?> = _lastTestResult.asStateFlow()

    private val _isGeneratingTest = MutableStateFlow(false)
    val isGeneratingTest: StateFlow<Boolean> = _isGeneratingTest.asStateFlow()

    private val _testGenerationError = MutableStateFlow<String?>(null)
    val testGenerationError: StateFlow<String?> = _testGenerationError.asStateFlow()

    // Mistake Practice Mode
    private val _mistakePracticeQuestions = MutableStateFlow<List<QuestionModel>?>(null)
    val mistakePracticeQuestions: StateFlow<List<QuestionModel>?> = _mistakePracticeQuestions.asStateFlow()

    // Streak & Badges
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()

    private val _badgesStatus = MutableStateFlow<List<Pair<BadgeDefinition, Boolean>>>(emptyList())
    val badgesStatus: StateFlow<List<Pair<BadgeDefinition, Boolean>>> = _badgesStatus.asStateFlow()

    // Daily AI Quota
    private val _aiQuota = MutableStateFlow(Triple(15, 15, "Ready"))
    val aiQuota: StateFlow<Triple<Int, Int, String>> = _aiQuota.asStateFlow()

    // Database reactive flows
    val subjects: StateFlow<List<SubjectEntity>> = repository.getAllSubjects()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val visibleChapters: StateFlow<List<ChapterEntity>> = repository.getVisibleChapters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedChaptersCount: StateFlow<Int> = repository.getVerifiedChaptersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalVisibleChaptersCount: StateFlow<Int> = repository.getTotalVisibleChaptersCount()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val weakChapters: StateFlow<List<ChapterEntity>> = repository.getWeakChapters()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allTargets: StateFlow<List<TargetEntity>> = repository.getAllTargets()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentAttempts: StateFlow<List<TestAttemptEntity>> = repository.getRecentTestAttempts(10)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allAttempts: StateFlow<List<TestAttemptEntity>> = repository.getAllTestAttempts()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val totalXp: StateFlow<Int> = repository.getTotalXp()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val revisionSessions: StateFlow<List<RevisionSessionEntity>> = repository.getAllRevisionSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadSettings()
        // Ensure initial starter data & rollover
        viewModelScope.launch(Dispatchers.IO) {
            repository.ensureInitialDataPopulated()
            repository.reconcileOverdueTargets()
            refreshStreakAndBadges()
            refreshAiQuota()
            loadSettings()
        }
    }

    fun navigateTo(destination: AppNavDestination) {
        _currentDestination.value = destination
    }

    fun setLanguage(lang: String) {
        _appLanguage.value = lang
        viewModelScope.launch {
            repository.setSetting("language", lang)
        }
    }

    fun setThemeMode(mode: AppThemeMode) {
        _appThemeMode.value = mode
        viewModelScope.launch {
            repository.setSetting("theme_mode", mode.name)
        }
    }

    fun setAccentTheme(accent: AccentTheme) {
        _appAccentTheme.value = accent
        viewModelScope.launch {
            repository.setSetting("accent_theme", accent.name)
        }
    }

    fun updateExamConfig(name: String, dateString: String) {
        _examName.value = name
        viewModelScope.launch {
            repository.setSetting("exam_name", name)
            repository.setSetting("exam_date", dateString)
            calculateExamCountdown(dateString)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            val lang = repository.getSetting("language") ?: "en"
            _appLanguage.value = lang
            repository.getSetting("theme_mode")?.let {
                try { _appThemeMode.value = AppThemeMode.valueOf(it) } catch (_: Exception) {}
            }
            repository.getSetting("accent_theme")?.let {
                try { _appAccentTheme.value = AccentTheme.valueOf(it) } catch (_: Exception) {}
            }
            repository.getSetting("exam_name")?.let { _examName.value = it }
            val examDate = repository.getSetting("exam_date") ?: "2026-03-05"
            calculateExamCountdown(examDate)
        }
    }

    private fun calculateExamCountdown(dateString: String) {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val examDate = sdf.parse(dateString) ?: return
            val today = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.time
            val diff = (examDate.time - today.time) / (1000 * 60 * 60 * 24)
            _examDaysRemaining.value = maxOf(0, diff.toInt())
        } catch (_: Exception) {
            _examDaysRemaining.value = 140
        }
    }

    fun refreshStreakAndBadges() {
        viewModelScope.launch(Dispatchers.IO) {
            val (badges, streak) = repository.getBadgesStatus()
            _badgesStatus.value = badges
            _currentStreak.value = streak
        }
    }

    fun refreshAiQuota() {
        viewModelScope.launch(Dispatchers.IO) {
            _aiQuota.value = repository.getDailyAiQuotaInfo()
        }
    }

    // --- Targets ---
    fun createTarget(
        title: String,
        subjectId: Long?,
        chapterId: Long?,
        type: TargetType,
        targetDate: String,
        priority: String,
        notes: String,
        reminderTime: String?
    ) {
        viewModelScope.launch {
            repository.createTarget(title, subjectId, chapterId, type, targetDate, priority, notes, reminderTime)
            refreshStreakAndBadges()
        }
    }

    fun completeTarget(targetId: Long) {
        viewModelScope.launch {
            repository.completeTarget(targetId)
            refreshStreakAndBadges()
        }
    }

    fun deleteTarget(target: TargetEntity) {
        viewModelScope.launch {
            repository.deleteTarget(target)
            refreshStreakAndBadges()
        }
    }

    // --- AI Test Generation & Taking ---
    fun startTest(
        chapterId: Long,
        questionCount: Int = 5,
        difficulty: Difficulty = Difficulty.MEDIUM,
        isRevisionTest: Boolean = false,
        linkedTargetId: Long? = null
    ) {
        _isGeneratingTest.value = true
        _testGenerationError.value = null
        _activeTest.value = null
        _lastTestResult.value = null

        viewModelScope.launch(Dispatchers.IO) {
            val chapter = repository.getChapterById(chapterId)
            if (chapter == null) {
                _isGeneratingTest.value = false
                _testGenerationError.value = "Chapter not found."
                return@launch
            }

            val subjectsList = subjects.value
            val subject = subjectsList.find { it.id == chapter.subjectId }
            val subjectTitle = subject?.nameEn ?: "Subject"

            val result = repository.generateQuestionsForTest(chapterId, questionCount, difficulty)
            _isGeneratingTest.value = false

            if (result.isSuccess) {
                val (questions, providerUsed) = result.getOrThrow()
                _activeTest.value = ActiveTestSession(
                    chapterId = chapterId,
                    subjectId = chapter.subjectId,
                    chapterTitle = chapter.titleEn,
                    subjectTitle = subjectTitle,
                    questions = questions,
                    providerUsed = providerUsed,
                    currentQuestionIndex = 0,
                    userAnswers = emptyMap(),
                    isRevisionTest = isRevisionTest,
                    linkedTargetId = linkedTargetId
                )
                refreshAiQuota()
            } else {
                _testGenerationError.value = result.exceptionOrNull()?.message ?: "Failed to generate test questions."
            }
        }
    }

    fun startTestForTarget(target: TargetEntity) {
        val targetChapterId = target.chapterId
            ?: visibleChapters.value.firstOrNull { it.subjectId == target.subjectId }?.id
            ?: 101L

        startTest(
            chapterId = targetChapterId,
            questionCount = 5,
            difficulty = Difficulty.MEDIUM,
            isRevisionTest = false,
            linkedTargetId = target.id
        )
        navigateTo(AppNavDestination.AI_TESTS)
    }

    fun selectAnswer(questionId: Int, optionIndex: Int) {
        val current = _activeTest.value ?: return
        val updated = current.userAnswers.toMutableMap()
        updated[questionId] = optionIndex
        _activeTest.value = current.copy(userAnswers = updated)
    }

    fun goToQuestion(index: Int) {
        val current = _activeTest.value ?: return
        if (index in current.questions.indices) {
            _activeTest.value = current.copy(currentQuestionIndex = index)
        }
    }

    fun submitCurrentTest() {
        val current = _activeTest.value ?: return
        val elapsedSec = ((System.currentTimeMillis() - current.startTimeMs) / 1000).toInt()

        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.submitTestAttempt(
                chapterId = current.chapterId,
                subjectId = current.subjectId,
                questions = current.questions,
                selectedAnswers = current.userAnswers,
                timeSpentSeconds = elapsedSec,
                isRevisionTest = current.isRevisionTest,
                providerUsed = current.providerUsed
            )
            // If test was taken to complete a target, only complete it if test is passed!
            if (result.isPassed && current.linkedTargetId != null) {
                repository.completeTarget(current.linkedTargetId)
            }
            _lastTestResult.value = result
            _activeTest.value = null
            refreshStreakAndBadges()
        }
    }

    fun retakeCurrentTest() {
        val last = _lastTestResult.value ?: return
        startTest(
            chapterId = last.chapterId,
            questionCount = last.totalQuestions,
            difficulty = Difficulty.MEDIUM,
            isRevisionTest = last.isRevisionTest
        )
    }

    fun startMistakePractice() {
        val last = _lastTestResult.value ?: return
        val incorrect = last.questions.filter { !it.isCorrect }.map {
            QuestionModel(
                id = it.id.toInt(),
                question = it.questionText,
                options = it.options,
                correctIndex = it.correctIndex,
                explanation = it.explanation
            )
        }
        _mistakePracticeQuestions.value = incorrect
    }

    fun clearMistakePractice() {
        _mistakePracticeQuestions.value = null
    }

    // --- Revision ---
    fun scheduleRevision(chapterId: Long, subjectId: Long, scheduledDate: String, notes: String) {
        viewModelScope.launch {
            repository.scheduleRevision(chapterId, subjectId, scheduledDate, notes)
            refreshStreakAndBadges()
        }
    }

    fun completeRevision(sessionId: Long) {
        viewModelScope.launch {
            repository.completeRevisionSession(sessionId)
            refreshStreakAndBadges()
        }
    }

    // --- Settings & AI Key ---
    fun testAiConnection(callback: (ConnectionTestResult) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.testAiConnection()
            callback(result)
        }
    }

    fun saveAiConfig(provider: String, model: String, apiKey: String, endpoint: String, dailyLimit: String) {
        viewModelScope.launch {
            repository.setSetting("ai_provider", provider)
            repository.setSetting("ai_model", model)
            repository.setSetting("ai_api_key", apiKey)
            repository.setSetting("ai_endpoint", endpoint)
            repository.setSetting("daily_ai_limit", dailyLimit)
            refreshAiQuota()
        }
    }

    suspend fun exportDataJson(): String = repository.exportBackupJson()
    suspend fun importDataJson(json: String): Result<String> = repository.importBackupJson(json)
}
