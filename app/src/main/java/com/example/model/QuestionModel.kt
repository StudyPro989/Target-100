package com.example.model

data class QuestionModel(
    val id: Int,
    val question: String,
    val options: List<String>,
    val correctIndex: Int,
    val explanation: String
)

data class TestResultModel(
    val attemptId: Long,
    val chapterId: Long,
    val chapterTitle: String,
    val totalQuestions: Int,
    val correctAnswers: Int,
    val scorePercent: Int,
    val passingThreshold: Int,
    val isPassed: Boolean,
    val isRevisionTest: Boolean,
    val chapterVerifiedNow: Boolean,
    val questions: List<QuestionReviewItem>
)

data class QuestionReviewItem(
    val id: Long,
    val questionText: String,
    val options: List<String>,
    val correctIndex: Int,
    val selectedIndex: Int?,
    val explanation: String,
    val isCorrect: Boolean
)

data class BadgeDefinition(
    val id: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val iconName: String,
    val xpBonus: Int
)

object BadgesCatalog {
    val ALL = listOf(
        BadgeDefinition(
            id = "FIRST_VERIFIED",
            titleEn = "First Milestone",
            titleHi = "पहला पड़ाव",
            descriptionEn = "Passed your first chapter verification test",
            descriptionHi = "पहला अध्याय सत्यापन टेस्ट पास किया",
            iconName = "check_circle",
            xpBonus = 100
        ),
        BadgeDefinition(
            id = "FIVE_VERIFIED",
            titleEn = "High 5 Verified",
            titleHi = "पंच गौरव",
            descriptionEn = "Verified 5 chapters successfully",
            descriptionHi = "5 अध्यायों को सफलतापूर्वक सत्यापित किया",
            iconName = "stars",
            xpBonus = 250
        ),
        BadgeDefinition(
            id = "TARGET_CRUSHER",
            titleEn = "Target Crusher",
            titleHi = "लक्ष्य विजेता",
            descriptionEn = "Completed 10 daily study targets",
            descriptionHi = "10 दैनिक अध्ययन लक्ष्य पूर्ण किए",
            iconName = "flag",
            xpBonus = 150
        ),
        BadgeDefinition(
            id = "TOP_SCORER",
            titleEn = "Centurion Spirit",
            titleHi = "शतक भावना",
            descriptionEn = "Scored 90% or higher in any chapter test",
            descriptionHi = "किसी भी अध्याय परीक्षा में 90% या अधिक अंक प्राप्त किए",
            iconName = "emoji_events",
            xpBonus = 200
        ),
        BadgeDefinition(
            id = "REVISION_MASTER",
            titleEn = "Revision Master",
            titleHi = "पुनरावृत्ति गुरु",
            descriptionEn = "Successfully revised a weak chapter and passed its quiz",
            descriptionHi = "कमजोर अध्याय की पुनरावृत्ति कर टेस्ट पास किया",
            iconName = "refresh",
            xpBonus = 150
        ),
        BadgeDefinition(
            id = "STREAK_7",
            titleEn = "7-Day Warrior",
            titleHi = "7-दिवसीय योद्धा",
            descriptionEn = "Maintained a 7-day study streak",
            descriptionHi = "7 दिन का निरंतर अध्ययन क्रम (Streak) बनाया",
            iconName = "local_fire_department",
            xpBonus = 300
        ),
        BadgeDefinition(
            id = "RBSE_TOPPER",
            titleEn = "RBSE 100 Champion",
            titleHi = "आरबीएसई 100 चैंपियन",
            descriptionEn = "Verified 15+ chapters with test excellence",
            descriptionHi = "15 से अधिक अध्यायों को उत्कृष्टता के साथ सत्यापित किया",
            iconName = "workspace_premium",
            xpBonus = 500
        )
    )
}
