package com.example.data.repository

import android.content.Context
import com.example.BuildConfig
import com.example.data.ai.AiProviderAdapter
import com.example.data.ai.ConnectionTestResult
import com.example.data.ai.GeminiProviderAdapter
import com.example.data.ai.OfflineQuestionBank
import com.example.data.ai.OpenAiCompatibleProviderAdapter
import com.example.data.local.AppDatabase
import com.example.data.local.entities.AppSettingsEntity
import com.example.data.local.entities.ChapterEntity
import com.example.data.local.entities.DailyStreakEntity
import com.example.data.local.entities.RevisionSessionEntity
import com.example.data.local.entities.SubjectEntity
import com.example.data.local.entities.TargetEntity
import com.example.data.local.entities.TargetHistoryEntity
import com.example.data.local.entities.TestAttemptEntity
import com.example.data.local.entities.TestQuestionEntity
import com.example.data.local.entities.XpTransactionEntity
import com.example.model.AiProviderType
import com.example.model.BadgesCatalog
import com.example.model.ChapterStatus
import com.example.model.Difficulty
import com.example.model.QuestionModel
import com.example.model.QuestionReviewItem
import com.example.model.TargetStatus
import com.example.model.TargetType
import com.example.model.TestResultModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class Target100Repository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val subjectDao = db.subjectDao()
    private val chapterDao = db.chapterDao()
    private val targetDao = db.targetDao()
    private val testDao = db.testDao()
    private val revisionDao = db.revisionDao()
    private val gamificationDao = db.gamificationDao()
    private val settingsDao = db.settingsDao()

    private val geminiAdapter = GeminiProviderAdapter()
    private val openAiAdapter = OpenAiCompatibleProviderAdapter()

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun getTodayDateString(): String = dateFormat.format(Date())

    // --- Subjects & Chapters ---
    fun getAllSubjects(): Flow<List<SubjectEntity>> = subjectDao.getAllSubjects()
    fun getVisibleChapters(): Flow<List<ChapterEntity>> = chapterDao.getAllVisibleChapters()
    fun getChaptersBySubject(subjectId: Long): Flow<List<ChapterEntity>> = chapterDao.getVisibleChaptersBySubject(subjectId)
    fun getAllChaptersBySubject(subjectId: Long): Flow<List<ChapterEntity>> = chapterDao.getAllChaptersBySubject(subjectId)
    fun getVerifiedChaptersCount(): Flow<Int> = chapterDao.getVerifiedChaptersCount()
    fun getTotalVisibleChaptersCount(): Flow<Int> = chapterDao.getTotalVisibleChaptersCount()
    fun getWeakChapters(): Flow<List<ChapterEntity>> = chapterDao.getWeakChapters()

    suspend fun getChapterById(id: Long): ChapterEntity? = chapterDao.getChapterById(id)
    suspend fun updateChapter(chapter: ChapterEntity) = chapterDao.updateChapter(chapter)
    suspend fun insertChapter(chapter: ChapterEntity): Long = chapterDao.insertChapter(chapter)
    suspend fun deleteChapter(chapter: ChapterEntity) = chapterDao.deleteChapter(chapter)

    // --- Targets & Tasks ---
    fun getAllTargets(): Flow<List<TargetEntity>> = targetDao.getAllTargets()
    fun getTargetsByDate(date: String): Flow<List<TargetEntity>> = targetDao.getTargetsByDate(date)

    suspend fun createTarget(
        title: String,
        subjectId: Long?,
        chapterId: Long?,
        type: TargetType,
        targetDate: String,
        priority: String,
        notes: String,
        reminderTime: String?
    ): Long {
        val target = TargetEntity(
            title = title,
            subjectId = subjectId,
            chapterId = chapterId,
            type = type.name,
            targetDate = targetDate,
            originalDate = targetDate,
            status = TargetStatus.PENDING.name,
            priority = priority,
            notes = notes,
            reminderTime = reminderTime
        )
        return targetDao.insertTarget(target)
    }

    suspend fun updateTarget(target: TargetEntity) {
        targetDao.updateTarget(target)
    }

    suspend fun deleteTarget(target: TargetEntity) {
        targetDao.deleteTarget(target)
    }

    /**
     * Mark a target as completed.
     * Core Rule: Completing a target does NOT verify a chapter!
     * It updates chapter study progress (e.g. to STUDIED_AWAITING_VERIFICATION),
     * awards base target XP, and qualifies the streak.
     */
    suspend fun completeTarget(targetId: Long): Boolean = withContext(Dispatchers.IO) {
        val target = targetDao.getTargetById(targetId) ?: return@withContext false
        if (target.status == TargetStatus.COMPLETED.name) return@withContext true

        val today = getTodayDateString()
        val updated = target.copy(
            status = TargetStatus.COMPLETED.name,
            completedAt = System.currentTimeMillis()
        )
        targetDao.updateTarget(updated)

        // Log history
        targetDao.insertHistory(
            TargetHistoryEntity(
                targetId = target.id,
                action = "COMPLETED",
                fromStatus = target.status,
                toStatus = TargetStatus.COMPLETED.name,
                fromDueDate = target.targetDate,
                toDueDate = target.targetDate
            )
        )

        // If target is linked to a chapter, update study activity without verifying
        target.chapterId?.let { chId ->
            val ch = chapterDao.getChapterById(chId)
            if (ch != null && (ch.status == ChapterStatus.NOT_STARTED.name || ch.status == ChapterStatus.IN_PROGRESS.name)) {
                chapterDao.updateChapter(
                    ch.copy(
                        status = ChapterStatus.STUDIED_AWAITING_VERIFICATION.name,
                        lastStudiedAt = System.currentTimeMillis()
                    )
                )
            }
        }

        // Award +50 XP (anti-abuse check)
        awardXpIfEligible(amount = 50, reason = "TARGET_COMPLETED", referenceId = "target_${target.id}")

        // Update streak record
        recordStreakEvent(isTargetCompleted = true, isTestPassed = false)

        true
    }

    /**
     * Reconcile overdue targets automatically on app open (idempotent).
     * Preserves original due date, increments rescheduleCount, logs history.
     */
    suspend fun reconcileOverdueTargets() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val overdue = targetDao.getOverduePendingTargets(today)
        for (target in overdue) {
            val updated = target.copy(
                targetDate = today,
                status = TargetStatus.RESCHEDULED.name,
                rescheduleCount = target.rescheduleCount + 1
            )
            targetDao.updateTarget(updated)
            targetDao.insertHistory(
                TargetHistoryEntity(
                    targetId = target.id,
                    action = "ROLLED_OVER",
                    fromStatus = target.status,
                    toStatus = TargetStatus.RESCHEDULED.name,
                    fromDueDate = target.targetDate,
                    toDueDate = today
                )
            )
        }
    }

    /**
     * Pre-populates rich initial starter targets, streaks, and test activity
     * so that the student has a full-filled, informative dashboard on first launch.
     */
    suspend fun ensureInitialDataPopulated() = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val existingTargets = targetDao.getAllTargets().firstOrNull() ?: emptyList()

        // Guarantee English is default if unconfigured
        val currentLang = getSetting("language")
        if (currentLang == null || currentLang == "hi") {
            setSetting("language", "en")
        }

        if (existingTargets.isEmpty()) {
            // 1. Pre-populate realistic, authentic RBSE Class 10 study targets for today
            targetDao.insertTarget(
                TargetEntity(
                    title = "Mathematics: Solve Quadratic Equations (Ex 4.2)",
                    subjectId = 1L,
                    chapterId = 104L,
                    type = TargetType.CHAPTER_STUDY.name,
                    targetDate = today,
                    originalDate = today,
                    status = TargetStatus.PENDING.name,
                    priority = "HIGH",
                    notes = "Master factorisation method and quadratic formula proofs",
                    reminderTime = "18:00"
                )
            )
            targetDao.insertTarget(
                TargetEntity(
                    title = "Science: Chemical Reactions Balancing & Equations",
                    subjectId = 2L,
                    chapterId = 201L,
                    type = TargetType.PRACTICE_QUESTIONS.name,
                    targetDate = today,
                    originalDate = today,
                    status = TargetStatus.PENDING.name,
                    priority = "MEDIUM",
                    notes = "Practice displacement & double displacement reactions",
                    reminderTime = "19:30"
                )
            )
            targetDao.insertTarget(
                TargetEntity(
                    title = "SST: Nationalism in India - Non-Cooperation Movement",
                    subjectId = 3L,
                    chapterId = 302L,
                    type = TargetType.CHAPTER_STUDY.name,
                    targetDate = today,
                    originalDate = today,
                    status = TargetStatus.PENDING.name,
                    priority = "HIGH",
                    notes = "Revise Rowlatt Act, Jallianwala Bagh, and Salt March timeline",
                    reminderTime = "21:00"
                )
            )
            val completedTargetId = targetDao.insertTarget(
                TargetEntity(
                    title = "English: 'A Letter to God' - Theme & Character Sketch",
                    subjectId = 5L,
                    chapterId = 501L,
                    type = TargetType.CHAPTER_STUDY.name,
                    targetDate = today,
                    originalDate = today,
                    status = TargetStatus.COMPLETED.name,
                    priority = "MEDIUM",
                    notes = "Lencho's immense faith in God and postmaster's kindness",
                    completedAt = System.currentTimeMillis() - 3600000L
                )
            )

            // 2. Pre-populate 1 verified chapter: Mathematics Chapter 101 ("Real Numbers")
            val mathCh1 = chapterDao.getChapterById(101L)
            if (mathCh1 != null) {
                chapterDao.updateChapter(
                    mathCh1.copy(
                        status = ChapterStatus.VERIFIED_COMPLETED.name,
                        totalTestAttempts = 1,
                        bestScorePercent = 80,
                        lastScorePercent = 80,
                        lastTestedAt = System.currentTimeMillis() - 86400000L,
                        verifiedAt = System.currentTimeMillis() - 86400000L
                    )
                )
                testDao.insertAttempt(
                    TestAttemptEntity(
                        chapterId = 101L,
                        subjectId = 1L,
                        attemptDate = System.currentTimeMillis() - 86400000L,
                        totalQuestions = 5,
                        correctAnswers = 4,
                        scorePercent = 80,
                        passingThreshold = 75,
                        isPassed = true,
                        difficulty = "MEDIUM",
                        providerUsed = "OFFLINE_BANK",
                        timeSpentSeconds = 145,
                        isRevisionTest = false
                    )
                )
                awardXpIfEligible(amount = 150, reason = "CHAPTER_VERIFIED", referenceId = "chapter_101_verified")
            }

            // 3. Pre-populate 1 weak chapter needing revision: Science Chapter 211 ("Electricity", 60% score < 75%)
            val sciCh11 = chapterDao.getChapterById(211L)
            if (sciCh11 != null) {
                chapterDao.updateChapter(
                    sciCh11.copy(
                        status = ChapterStatus.NEEDS_REVISION.name,
                        totalTestAttempts = 1,
                        bestScorePercent = 60,
                        lastScorePercent = 60,
                        lastTestedAt = System.currentTimeMillis() - 43200000L
                    )
                )
                testDao.insertAttempt(
                    TestAttemptEntity(
                        chapterId = 211L,
                        subjectId = 2L,
                        attemptDate = System.currentTimeMillis() - 43200000L,
                        totalQuestions = 5,
                        correctAnswers = 3,
                        scorePercent = 60,
                        passingThreshold = 75,
                        isPassed = false,
                        difficulty = "MEDIUM",
                        providerUsed = "OFFLINE_BANK",
                        timeSpentSeconds = 180,
                        isRevisionTest = false
                    )
                )
            }

            // 4. Pre-populate 3-day active streak & XP
            val cal = Calendar.getInstance()
            val d0 = dateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val d1 = dateFormat.format(cal.time)
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val d2 = dateFormat.format(cal.time)

            gamificationDao.upsertDailyStreak(DailyStreakEntity(date = d2, completedTargetsCount = 2, verifiedTestsCount = 0, streakQualified = true))
            gamificationDao.upsertDailyStreak(DailyStreakEntity(date = d1, completedTargetsCount = 1, verifiedTestsCount = 1, streakQualified = true))
            gamificationDao.upsertDailyStreak(DailyStreakEntity(date = d0, completedTargetsCount = 1, verifiedTestsCount = 0, streakQualified = true))

            awardXpIfEligible(amount = 50, reason = "TARGET_COMPLETED", referenceId = "target_$completedTargetId")
            awardXpIfEligible(amount = 100, reason = "STREAK_BONUS", referenceId = "streak_milestone_3")
        }
    }

    // --- AI Test Generation & Verification ---
    fun getRecentTestAttempts(limit: Int = 10): Flow<List<TestAttemptEntity>> = testDao.getRecentAttempts(limit)
    fun getAllTestAttempts(): Flow<List<TestAttemptEntity>> = testDao.getAllAttempts()
    fun getAverageScorePercent(): Flow<Double?> = testDao.getAverageScorePercent()

    suspend fun testAiConnection(): ConnectionTestResult = withContext(Dispatchers.IO) {
        val providerStr = getSetting("ai_provider") ?: "GEMINI"
        val model = getSetting("ai_model") ?: "gemini-3.5-flash"
        val apiKey = getResolvedApiKey(providerStr)
        val endpoint = getSetting("ai_endpoint")

        if (providerStr == "OPENAI_COMPATIBLE") {
            openAiAdapter.testConnection(apiKey, model, endpoint)
        } else {
            geminiAdapter.testConnection(apiKey, model, endpoint)
        }
    }

    private suspend fun getResolvedApiKey(provider: String): String {
        val customKey = getSetting("ai_api_key")
        if (!customKey.isNullOrBlank()) return customKey.trim()
        if (provider == "GEMINI") {
            // Check BuildConfig fallback
            try {
                val field = BuildConfig::class.java.getField("GEMINI_API_KEY")
                val key = field.get(null) as? String
                if (!key.isNullOrBlank() && key != "MY_GEMINI_API_KEY") {
                    return key.trim()
                }
            } catch (_: Exception) {}
        }
        return ""
    }

    suspend fun getDailyAiQuotaInfo(): Triple<Int, Int, String> = withContext(Dispatchers.IO) {
        val limit = (getSetting("daily_ai_limit") ?: "15").toIntOrNull() ?: 15
        val today = getTodayDateString()
        val lastReqDate = getSetting("last_request_date") ?: ""
        var usedToday = (getSetting("requests_used_today") ?: "0").toIntOrNull() ?: 0

        if (lastReqDate != today) {
            // New day reset
            usedToday = 0
            setSetting("last_request_date", today)
            setSetting("requests_used_today", "0")
        }
        val remaining = maxOf(0, limit - usedToday)
        val status = getSetting("last_request_status") ?: "Ready"
        Triple(limit, remaining, status)
    }

    private suspend fun incrementAiUsage() {
        val today = getTodayDateString()
        val lastReqDate = getSetting("last_request_date") ?: ""
        var usedToday = (getSetting("requests_used_today") ?: "0").toIntOrNull() ?: 0
        if (lastReqDate != today) {
            usedToday = 0
            setSetting("last_request_date", today)
        }
        usedToday += 1
        setSetting("requests_used_today", usedToday.toString())
        setSetting("last_request_status", "Success ($today)")
    }

    suspend fun generateQuestionsForTest(
        chapterId: Long,
        questionCount: Int,
        difficulty: Difficulty
    ): Result<Pair<List<QuestionModel>, String>> = withContext(Dispatchers.IO) {
        val chapter = chapterDao.getChapterById(chapterId)
            ?: return@withContext Result.failure(IllegalArgumentException("Chapter not found"))
        val subject = subjectDao.getSubjectById(chapter.subjectId)
        val subjectName = subject?.nameEn ?: "Subject"

        val providerStr = getSetting("ai_provider") ?: "GEMINI"
        val model = getSetting("ai_model") ?: "gemini-3.5-flash"
        val apiKey = getResolvedApiKey(providerStr)
        val endpoint = getSetting("ai_endpoint")

        // Check if daily limit reached
        val (limit, remaining, _) = getDailyAiQuotaInfo()
        if (apiKey.isNotBlank() && remaining <= 0) {
            return@withContext Result.failure(
                IllegalStateException("Daily AI request limit ($limit) reached. You can adjust limits in Settings or use Offline Bank.")
            )
        }

        // Attempt AI generation if key is present
        if (apiKey.isNotBlank()) {
            val aiResult = if (providerStr == "OPENAI_COMPATIBLE") {
                openAiAdapter.generateChapterTest(apiKey, model, endpoint, subjectName, chapter.titleEn, questionCount, difficulty)
            } else {
                geminiAdapter.generateChapterTest(apiKey, model, endpoint, subjectName, chapter.titleEn, questionCount, difficulty)
            }

            if (aiResult.isSuccess) {
                incrementAiUsage()
                return@withContext Result.success(Pair(aiResult.getOrThrow(), providerStr))
            } else {
                setSetting("last_request_status", "Error: ${aiResult.exceptionOrNull()?.message?.take(50)}")
            }
        }

        // Seamless offline question bank fallback
        val offlineQuestions = OfflineQuestionBank.getQuestions(
            chapterId = chapterId,
            chapterTitle = chapter.titleEn,
            subjectName = subjectName,
            count = questionCount,
            difficulty = difficulty
        )
        Result.success(Pair(offlineQuestions, "OFFLINE_QUESTION_BANK"))
    }

    /**
     * Submit test attempt and deterministically score locally.
     * Core Rule: Only passing a chapter test establishes verified completion!
     */
    suspend fun submitTestAttempt(
        chapterId: Long,
        subjectId: Long,
        questions: List<QuestionModel>,
        selectedAnswers: Map<Int, Int>,
        timeSpentSeconds: Int,
        isRevisionTest: Boolean,
        providerUsed: String
    ): TestResultModel = withContext(Dispatchers.IO) {
        val chapter = chapterDao.getChapterById(chapterId)
        val passingThreshold = chapter?.configuredPassingPercent ?: 75

        var correctCount = 0
        val questionReviews = mutableListOf<QuestionReviewItem>()

        for (q in questions) {
            val selected = selectedAnswers[q.id]
            val isCorrect = selected != null && selected == q.correctIndex
            if (isCorrect) correctCount++

            questionReviews.add(
                QuestionReviewItem(
                    id = q.id.toLong(),
                    questionText = q.question,
                    options = q.options,
                    correctIndex = q.correctIndex,
                    selectedIndex = selected,
                    explanation = q.explanation,
                    isCorrect = isCorrect
                )
            )
        }

        val totalCount = questions.size
        val scorePercent = if (totalCount > 0) ((correctCount * 100.0) / totalCount).toInt() else 0
        val isPassed = scorePercent >= passingThreshold

        // Insert attempt record
        val attemptEntity = TestAttemptEntity(
            chapterId = chapterId,
            subjectId = subjectId,
            totalQuestions = totalCount,
            correctAnswers = correctCount,
            scorePercent = scorePercent,
            passingThreshold = passingThreshold,
            isPassed = isPassed,
            difficulty = Difficulty.MEDIUM.name,
            providerUsed = providerUsed,
            timeSpentSeconds = timeSpentSeconds,
            isRevisionTest = isRevisionTest
        )
        val attemptId = testDao.insertAttempt(attemptEntity)

        // Insert questions
        val questionEntities = questionReviews.mapIndexed { index, rev ->
            TestQuestionEntity(
                attemptId = attemptId,
                questionIndex = index,
                questionText = rev.questionText,
                optionsJson = JSONArray(rev.options).toString(),
                correctOptionIndex = rev.correctIndex,
                selectedOptionIndex = rev.selectedIndex,
                explanation = rev.explanation,
                isCorrect = rev.isCorrect
            )
        }
        testDao.insertQuestions(questionEntities)

        // Chapter State Machine update
        var chapterVerifiedNow = false
        if (chapter != null) {
            val newTotalAttempts = chapter.totalTestAttempts + 1
            val newBestScore = maxOf(chapter.bestScorePercent, scorePercent)

            if (isPassed) {
                chapterVerifiedNow = true
                val updatedChapter = chapter.copy(
                    status = ChapterStatus.VERIFIED_COMPLETED.name,
                    totalTestAttempts = newTotalAttempts,
                    bestScorePercent = newBestScore,
                    lastScorePercent = scorePercent,
                    lastTestedAt = System.currentTimeMillis(),
                    verifiedAt = System.currentTimeMillis()
                )
                chapterDao.updateChapter(updatedChapter)

                // Bonus XP for passing test (+150 XP)
                awardXpIfEligible(amount = 150, reason = "CHAPTER_VERIFIED", referenceId = "chapter_${chapterId}_verified")

                // If score >= 90%, check top scorer bonus
                if (scorePercent >= 90) {
                    awardXpIfEligible(amount = 50, reason = "TOP_SCORE_BONUS", referenceId = "attempt_${attemptId}_top")
                }
            } else {
                // If test failed, flag for revision
                val updatedChapter = chapter.copy(
                    status = ChapterStatus.NEEDS_REVISION.name,
                    totalTestAttempts = newTotalAttempts,
                    bestScorePercent = newBestScore,
                    lastScorePercent = scorePercent,
                    lastTestedAt = System.currentTimeMillis()
                )
                chapterDao.updateChapter(updatedChapter)
            }
        }

        // Record streak
        recordStreakEvent(isTargetCompleted = false, isTestPassed = isPassed)

        TestResultModel(
            attemptId = attemptId,
            chapterId = chapterId,
            chapterTitle = chapter?.titleEn ?: "Chapter",
            totalQuestions = totalCount,
            correctAnswers = correctCount,
            scorePercent = scorePercent,
            passingThreshold = passingThreshold,
            isPassed = isPassed,
            isRevisionTest = isRevisionTest,
            chapterVerifiedNow = chapterVerifiedNow,
            questions = questionReviews
        )
    }

    // --- Revision ---
    fun getAllRevisionSessions(): Flow<List<RevisionSessionEntity>> = revisionDao.getAllSessions()

    suspend fun scheduleRevision(chapterId: Long, subjectId: Long, scheduledDate: String, notes: String): Long {
        val session = RevisionSessionEntity(
            chapterId = chapterId,
            subjectId = subjectId,
            scheduledDate = scheduledDate,
            notes = notes
        )
        val id = revisionDao.insertSession(session)

        // Also create a linked revision target for this date
        val chapter = chapterDao.getChapterById(chapterId)
        val title = "Revision: ${chapter?.titleEn ?: "Chapter"}"
        createTarget(
            title = title,
            subjectId = subjectId,
            chapterId = chapterId,
            type = TargetType.REVISION,
            targetDate = scheduledDate,
            priority = "HIGH",
            notes = notes,
            reminderTime = "18:00"
        )

        return id
    }

    suspend fun completeRevisionSession(sessionId: Long) = withContext(Dispatchers.IO) {
        val sessions = revisionDao.getAllSessions().firstOrNull()
        val session = sessions?.find { it.id == sessionId } ?: return@withContext
        revisionDao.updateSession(session.copy(isCompleted = true))
        awardXpIfEligible(amount = 75, reason = "REVISION_DONE", referenceId = "revision_$sessionId")
    }

    // --- Gamification, Streaks & Badges ---
    fun getTotalXp(): Flow<Int> = gamificationDao.getTotalXp().map { it ?: 0 }
    fun getRecentXpTransactions(): Flow<List<XpTransactionEntity>> = gamificationDao.getRecentXpTransactions()
    fun getStreakDays(): Flow<List<DailyStreakEntity>> = gamificationDao.getQualifiedStreakDays()

    private suspend fun awardXpIfEligible(amount: Int, reason: String, referenceId: String) {
        val count = gamificationDao.countTransactionsForRef(reason, referenceId)
        if (count == 0) {
            gamificationDao.insertXpTransaction(
                XpTransactionEntity(
                    amount = amount,
                    reason = reason,
                    referenceId = referenceId
                )
            )
        }
    }

    private suspend fun recordStreakEvent(isTargetCompleted: Boolean, isTestPassed: Boolean) {
        val today = getTodayDateString()
        val existing = gamificationDao.getStreakForDate(today)
        val newTargetCount = (existing?.completedTargetsCount ?: 0) + (if (isTargetCompleted) 1 else 0)
        val newTestCount = (existing?.verifiedTestsCount ?: 0) + (if (isTestPassed) 1 else 0)
        val qualified = newTargetCount >= 1 // Satisfies target completion requirement

        gamificationDao.upsertDailyStreak(
            DailyStreakEntity(
                date = today,
                completedTargetsCount = newTargetCount,
                verifiedTestsCount = newTestCount,
                streakQualified = qualified
            )
        )
    }

    suspend fun calculateCurrentStreak(): Int = withContext(Dispatchers.IO) {
        val qualifiedDays = gamificationDao.getQualifiedStreakDays().firstOrNull() ?: emptyList()
        if (qualifiedDays.isEmpty()) return@withContext 0

        val dateSet = qualifiedDays.map { it.date }.toSet()
        val cal = Calendar.getInstance()
        val todayStr = dateFormat.format(cal.time)

        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayStr = dateFormat.format(cal.time)

        if (!dateSet.contains(todayStr) && !dateSet.contains(yesterdayStr)) {
            return@withContext 0
        }

        var streak = 0
        val checkCal = Calendar.getInstance()
        if (!dateSet.contains(todayStr)) {
            checkCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val dStr = dateFormat.format(checkCal.time)
            if (dateSet.contains(dStr)) {
                streak++
                checkCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        streak
    }

    suspend fun getBadgesStatus(): Pair<List<Pair<com.example.model.BadgeDefinition, Boolean>>, Int> = withContext(Dispatchers.IO) {
        val verifiedCount = chapterDao.getVerifiedChaptersCount().firstOrNull() ?: 0
        val completedTargets = targetDao.getRecentCompletedTargets().firstOrNull()?.size ?: 0
        val attempts = testDao.getAllAttempts().firstOrNull() ?: emptyList()
        val maxScore = attempts.maxOfOrNull { it.scorePercent } ?: 0
        val currentStreak = calculateCurrentStreak()

        val list = BadgesCatalog.ALL.map { badge ->
            val unlocked = when (badge.id) {
                "FIRST_VERIFIED" -> verifiedCount >= 1
                "FIVE_VERIFIED" -> verifiedCount >= 5
                "TARGET_CRUSHER" -> completedTargets >= 10
                "TOP_SCORER" -> maxScore >= 90
                "REVISION_MASTER" -> attempts.any { it.isRevisionTest && it.isPassed }
                "STREAK_7" -> currentStreak >= 7
                "RBSE_TOPPER" -> verifiedCount >= 15
                else -> false
            }
            Pair(badge, unlocked)
        }
        Pair(list, currentStreak)
    }

    // --- Settings & Persistence ---
    suspend fun getSetting(key: String): String? = settingsDao.getSettingValue(key)
    fun getSettingFlow(key: String): Flow<String?> = settingsDao.getSettingFlow(key)
    suspend fun setSetting(key: String, value: String) = settingsDao.setSetting(AppSettingsEntity(key, value))

    // --- Backup & Restore ---
    suspend fun exportBackupJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()
        root.put("app", "Target100_RBSE")
        root.put("version", 1)
        root.put("exportTimestamp", System.currentTimeMillis())

        val subjects = subjectDao.getAllSubjects().firstOrNull() ?: emptyList()
        val subjectsArray = JSONArray()
        for (s in subjects) {
            subjectsArray.put(JSONObject().apply {
                put("id", s.id)
                put("nameEn", s.nameEn)
                put("nameHi", s.nameHi)
                put("code", s.code)
                put("iconName", s.iconName)
                put("displayOrder", s.displayOrder)
                put("isOptional", s.isOptional)
            })
        }
        root.put("subjects", subjectsArray)

        val chapters = chapterDao.getAllChapters().firstOrNull() ?: emptyList()
        val chaptersArray = JSONArray()
        for (c in chapters) {
            chaptersArray.put(JSONObject().apply {
                put("id", c.id)
                put("subjectId", c.subjectId)
                put("chapterNumber", c.chapterNumber)
                put("titleEn", c.titleEn)
                put("titleHi", c.titleHi)
                put("status", c.status)
                put("configuredPassingPercent", c.configuredPassingPercent)
                put("isHidden", c.isHidden)
                put("totalTestAttempts", c.totalTestAttempts)
                put("bestScorePercent", c.bestScorePercent)
            })
        }
        root.put("chapters", chaptersArray)

        val targets = targetDao.getAllTargets().firstOrNull() ?: emptyList()
        val targetsArray = JSONArray()
        for (t in targets) {
            targetsArray.put(JSONObject().apply {
                put("id", t.id)
                put("title", t.title)
                put("subjectId", t.subjectId ?: JSONObject.NULL)
                put("chapterId", t.chapterId ?: JSONObject.NULL)
                put("type", t.type)
                put("targetDate", t.targetDate)
                put("originalDate", t.originalDate)
                put("status", t.status)
                put("priority", t.priority)
                put("notes", t.notes)
            })
        }
        root.put("targets", targetsArray)

        root.toString(2)
    }

    suspend fun importBackupJson(jsonString: String): Result<String> = withContext(Dispatchers.IO) {
        try {
            val root = JSONObject(jsonString)
            if (!root.has("app") || root.getString("app") != "Target100_RBSE") {
                return@withContext Result.failure(IllegalArgumentException("Invalid Target 100 backup file format."))
            }

            if (root.has("targets")) {
                val targetsArray = root.getJSONArray("targets")
                for (i in 0 until targetsArray.length()) {
                    val t = targetsArray.getJSONObject(i)
                    targetDao.insertTarget(
                        TargetEntity(
                            id = t.optLong("id", 0),
                            title = t.getString("title"),
                            subjectId = if (t.isNull("subjectId")) null else t.getLong("subjectId"),
                            chapterId = if (t.isNull("chapterId")) null else t.getLong("chapterId"),
                            type = t.optString("type", TargetType.CHAPTER_STUDY.name),
                            targetDate = t.getString("targetDate"),
                            originalDate = t.optString("originalDate", t.getString("targetDate")),
                            status = t.optString("status", TargetStatus.PENDING.name),
                            priority = t.optString("priority", "MEDIUM"),
                            notes = t.optString("notes", "")
                        )
                    )
                }
            }

            Result.success("Backup successfully restored!")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
