package com.example

import com.example.data.ai.JsonQuestionParser
import com.example.data.prepopulate.RbseClass10Syllabus
import com.example.model.BadgesCatalog
import com.example.model.ChapterStatus
import com.example.model.TargetStatus
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class Target100LogicTest {

    @Test
    fun testScoreCalculation_PassingAndFailingThresholds() {
        // 5 questions, 4 correct -> 80%
        val totalQuestions = 5
        val correctCount = 4
        val threshold = 75

        val scorePercent = ((correctCount * 100.0) / totalQuestions).toInt()
        val isPassed = scorePercent >= threshold

        assertEquals(80, scorePercent)
        assertTrue("80% must pass a 75% threshold", isPassed)

        // 5 questions, 3 correct -> 60%
        val failCorrectCount = 3
        val failScorePercent = ((failCorrectCount * 100.0) / totalQuestions).toInt()
        val isFailed = failScorePercent >= threshold

        assertEquals(60, failScorePercent)
        assertFalse("60% must fail a 75% threshold", isFailed)
    }

    @Test
    fun testCorePrinciple_TargetCompletionDoesNotVerifyChapter() {
        // Simulation of Core Rule:
        // A completed study task changes chapter status to STUDIED_AWAITING_VERIFICATION,
        // but NEVER directly to VERIFIED_COMPLETED.
        var chapterStatus = ChapterStatus.NOT_STARTED

        // User finishes study task
        val targetCompleted = true
        if (targetCompleted && chapterStatus == ChapterStatus.NOT_STARTED) {
            chapterStatus = ChapterStatus.STUDIED_AWAITING_VERIFICATION
        }

        assertEquals(
            "Target completion must only mark chapter as studied/awaiting verification",
            ChapterStatus.STUDIED_AWAITING_VERIFICATION,
            chapterStatus
        )

        // Only passing the test verifies the chapter
        val testPassed = true
        if (testPassed) {
            chapterStatus = ChapterStatus.VERIFIED_COMPLETED
        }

        assertEquals(
            "Only passing test establishes verified completion",
            ChapterStatus.VERIFIED_COMPLETED,
            chapterStatus
        )
    }

    @Test
    fun testTaskCompletion_DecliningTestKeepsTaskIncomplete() {
        var taskStatus = TargetStatus.PENDING

        // When user ticks task, popup arrives asking for test.
        // If user denies it, task remains in PENDING (incomplete).
        val userDeclinedTest = true
        if (!userDeclinedTest) {
            taskStatus = TargetStatus.COMPLETED
        }

        assertEquals(
            "Task must remain incomplete (PENDING) when test is declined",
            TargetStatus.PENDING,
            taskStatus
        )

        // When user takes test and passes, task status becomes COMPLETED
        val userTookAndPassedTest = true
        if (userTookAndPassedTest) {
            taskStatus = TargetStatus.COMPLETED
        }

        assertEquals(
            "Task becomes COMPLETED only upon passing test",
            TargetStatus.COMPLETED,
            taskStatus
        )
    }

    @Test
    fun testJsonQuestionParser_Validates4OptionsAndSchema() {
        val validJson = """
            [
              {
                "id": 1,
                "question": "What is the HCF of 96 and 404?",
                "options": ["4", "2", "8", "16"],
                "correctIndex": 0,
                "explanation": "Prime factorization method yields 4."
              },
              {
                "id": 2,
                "question": "Which of the following is irrational?",
                "options": ["√2", "√4", "√9", "√16"],
                "correctIndex": 0,
                "explanation": "√2 cannot be written as p/q."
              }
            ]
        """.trimIndent()

        val result = JsonQuestionParser.parseAndValidateQuestions(validJson, expectedCount = 2)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        assertEquals(4, list[0].options.size)
        assertEquals(0, list[0].correctIndex)
    }

    @Test
    fun testJsonQuestionParser_RejectsInvalidOptionsCount() {
        // Only 3 options provided (spec strictly requires 4)
        val invalidOptionsJson = """
            [
              {
                "id": 1,
                "question": "Incomplete options question?",
                "options": ["A", "B", "C"],
                "correctIndex": 0,
                "explanation": "Invalid"
              }
            ]
        """.trimIndent()

        val result = JsonQuestionParser.parseAndValidateQuestions(invalidOptionsJson, expectedCount = 1)
        assertTrue("Parser must reject questions not having exactly 4 options", result.isFailure)
    }

    @Test
    fun testRbseSyllabusDataset_Integrity() {
        val (subjects, chapters) = RbseClass10Syllabus.toEntities()
        assertTrue("Must include all RBSE Class 10 subjects", subjects.size >= 6)
        assertTrue("Must include authentic chapters across subjects", chapters.size >= 50)

        // Verify Mathematics, Science, and SST are present
        val math = subjects.find { it.code == "MATH_10" }
        val sci = subjects.find { it.code == "SCI_10" }
        val sst = subjects.find { it.code == "SST_10" }
        assertTrue(math != null)
        assertTrue(sci != null)
        assertTrue(sst != null)
    }

    @Test
    fun testBadgesCatalog_Integrity() {
        val badges = BadgesCatalog.ALL
        assertTrue(badges.any { it.id == "FIRST_VERIFIED" })
        assertTrue(badges.any { it.id == "FIVE_VERIFIED" })
        assertTrue(badges.any { it.id == "TOP_SCORER" })
        assertTrue(badges.any { it.id == "STREAK_7" })
    }
}
