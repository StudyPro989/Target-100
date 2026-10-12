package com.example.model

enum class ChapterStatus {
    NOT_STARTED,
    IN_PROGRESS,
    STUDIED_AWAITING_VERIFICATION,
    VERIFIED_COMPLETED,
    NEEDS_REVISION
}

enum class TargetType {
    CHAPTER_STUDY,
    REVISION,
    PRACTICE_QUESTIONS,
    MOCK_TEST,
    CUSTOM_TASK
}

enum class TargetStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    RESCHEDULED,
    CANCELLED
}

enum class Priority {
    LOW,
    MEDIUM,
    HIGH
}

enum class Difficulty {
    EASY,
    MEDIUM,
    HARD
}

enum class AiProviderType {
    GEMINI,
    OPENAI_COMPATIBLE
}

enum class AppThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class AccentTheme(val titleEn: String, val titleHi: String, val primaryHex: Long) {
    GOLD_RAJPUTANA("Royal Gold (Rajputana)", "शाही स्वर्ण (राजपूताना)", 0xFFD97706),
    MARWAR_BLUE("Marwar Blue (Jodhpur)", "मारवाड़ नीला (जोधपुर)", 0xFF1D4ED8),
    JAIPUR_EMERALD("Jaipur Emerald", "जयपुर पन्ना (हरा)", 0xFF059669),
    MEWAR_PURPLE("Mewar Purple", "मेवाड़ जामुनी", 0xFF7C3AED)
}
