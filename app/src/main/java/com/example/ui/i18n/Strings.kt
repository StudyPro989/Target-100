package com.example.ui.i18n

object AppStrings {

    fun get(key: String, lang: String = "hi"): String {
        val map = if (lang == "hi") hindiStrings else englishStrings
        return map[key] ?: englishStrings[key] ?: key
    }

    private val englishStrings = mapOf(
        "app_title" to "Target 100",
        "app_subtitle" to "RBSE Class 10 Learning Verification",

        // Drawer Navigation
        "nav_dashboard" to "Dashboard",
        "nav_targets" to "Targets & Tasks",
        "nav_syllabus" to "Syllabus & Chapters",
        "nav_revision" to "Revision System",
        "nav_ai_tests" to "AI Chapter Tests",
        "nav_reports" to "Performance & Reports",
        "nav_achievements" to "Streaks & Badges",
        "nav_settings" to "Settings",

        // Dashboard
        "dash_board_exam" to "RBSE Board Exam",
        "dash_days_remaining" to "days remaining",
        "dash_streak" to "Study Streak",
        "dash_streak_days" to "Days Streak",
        "dash_verified_chapters" to "Verified Chapters",
        "dash_out_of" to "of",
        "dash_chapters" to "Chapters",
        "dash_total_xp" to "Total XP",
        "dash_level" to "Level",
        "dash_todays_targets" to "Today's Study Targets",
        "dash_no_targets_today" to "No targets set for today. Plan your day!",
        "dash_add_target" to "Add Target",
        "dash_quick_test" to "Take AI Test",
        "dash_schedule_rev" to "Schedule Revision",
        "dash_recent_scores" to "Recent Test Scores",
        "dash_no_tests_yet" to "No tests taken yet. Pass a test to verify a chapter!",
        "dash_weak_chapters" to "Weak Chapters Needing Revision",
        "dash_all_chapters_strong" to "Great job! No weak chapters pending.",

        // Statuses
        "status_not_started" to "Not Started",
        "status_in_progress" to "In Progress",
        "status_awaiting_verification" to "Studied (Pending Test)",
        "status_verified_completed" to "Verified Completed",
        "status_needs_revision" to "Needs Revision",

        "target_pending" to "Pending",
        "target_completed" to "Completed",
        "target_rescheduled" to "Rolled Over",
        "target_cancelled" to "Cancelled",

        // Targets Screen
        "targets_title" to "Study Targets",
        "targets_today" to "Today",
        "targets_upcoming" to "Upcoming",
        "targets_completed" to "Completed",
        "target_new_dialog_title" to "Create Study Target",
        "target_title_hint" to "e.g. Solve Quadratic Equations Exercise 4.2",
        "target_subject" to "Subject",
        "target_chapter" to "Chapter (Optional)",
        "target_type" to "Target Type",
        "target_date" to "Target Date",
        "target_priority" to "Priority",
        "target_notes" to "Notes (Optional)",
        "target_mark_done" to "Mark Done",
        "target_rollover_notice" to "Unfinished tasks roll over automatically without duplicate records.",

        // Syllabus Screen
        "syllabus_title" to "RBSE Class 10 Syllabus",
        "syllabus_verified_badge" to "VERIFIED",
        "syllabus_unverified_badge" to "UNVERIFIED",
        "syllabus_passing_threshold" to "Passing Threshold",
        "syllabus_test_btn" to "Verify with Test",
        "syllabus_add_chapter" to "Add Custom Chapter",
        "syllabus_import" to "Import / Edit Syllabus",

        // Revision Screen
        "revision_title" to "Revision Center",
        "revision_subtitle" to "Smart revision prioritized by failed tests and low scores",
        "revision_scheduled" to "Scheduled Sessions",
        "revision_suggested" to "Suggested for Revision",
        "revision_new_btn" to "Schedule Revision",
        "revision_mark_done" to "Complete Revision",

        // AI Tests Screen
        "ai_tests_title" to "AI Chapter Test & Verification",
        "ai_tests_rule_notice" to "Core Rule: A completed task is not proof of learning. A chapter is verified only when you pass its test.",
        "ai_test_select_subject" to "Select Subject",
        "ai_test_select_chapter" to "Select Chapter",
        "ai_test_questions_count" to "Number of Questions",
        "ai_test_difficulty" to "Difficulty",
        "ai_test_start" to "Start Test",
        "ai_test_generating" to "Generating authentic MCQs...",
        "ai_test_taking_q" to "Question",
        "ai_test_prev" to "Previous",
        "ai_test_next" to "Next",
        "ai_test_submit" to "Submit Test",
        "ai_test_confirm_submit" to "Are you sure you want to submit your answers?",
        "ai_test_confirm_yes" to "Submit Now",
        "ai_test_cancel" to "Cancel",

        // Results Screen
        "results_title" to "Test Results",
        "results_passed" to "PASSED - Chapter Verified!",
        "results_failed" to "FAILED - Needs Revision",
        "results_score" to "Your Score",
        "results_threshold" to "Required to Pass",
        "results_review" to "Detailed Question Review",
        "results_mistake_practice" to "Practice Mistakes",
        "results_fresh_test" to "Take Fresh Test",
        "results_back" to "Back to Syllabus",

        // Reports Screen
        "reports_title" to "Weekly Performance & Reports",
        "reports_weekly_summary" to "Weekly Summary",
        "reports_targets_completed" to "Targets Completed",
        "reports_tests_taken" to "Tests Attempted",
        "reports_avg_score" to "Average Score",
        "reports_xp_gained" to "XP Earned",
        "reports_export_pdf" to "Export Weekly Report",

        // Achievements Screen
        "achievements_title" to "Streaks & Achievements",
        "achievements_level_info" to "Target 100 Rank",
        "achievements_all_badges" to "Badges & Trophies",
        "achievements_unlocked" to "UNLOCKED",
        "achievements_locked" to "LOCKED",

        // Settings Screen
        "settings_title" to "Settings",
        "settings_language" to "App Language / भाषा",
        "settings_theme" to "Theme Mode",
        "settings_accent" to "Accent Color Palette",
        "settings_exam_config" to "Exam Countdown Settings",
        "settings_ai_config" to "AI Provider & API Key",
        "settings_ai_provider" to "Provider",
        "settings_ai_key_hint" to "Enter your API key",
        "settings_test_conn" to "Test Connection",
        "settings_daily_limit" to "Daily AI Request Quota",
        "settings_data_backup" to "Data Backup & Restore",
        "settings_export_data" to "Export Local Data (JSON)",
        "settings_import_data" to "Import Data",
        "settings_reset_data" to "Reset All Data",
        "settings_reset_confirm" to "Are you sure? This will delete all student records.",
        "settings_about" to "Target 100 - RBSE Class 10 Study & Verification System"
    )

    private val hindiStrings = mapOf(
        "app_title" to "टारगेट 100",
        "app_subtitle" to "आरबीएसई कक्षा 10 अध्ययन एवं अधिगम सत्यापन",

        // Drawer Navigation
        "nav_dashboard" to "डैशबोर्ड",
        "nav_targets" to "दैनिक लक्ष्य एवं कार्य",
        "nav_syllabus" to "पाठ्यक्रम एवं अध्याय",
        "nav_revision" to "पुनरावृत्ति केंद्र",
        "nav_ai_tests" to "एआई अध्याय परीक्षा",
        "nav_reports" to "प्रगति रिपोर्ट एवं आंकड़े",
        "nav_achievements" to "अध्ययन क्रम एवं पदक",
        "nav_settings" to "सेटिंग्स",

        // Dashboard
        "dash_board_exam" to "आरबीएसई 10वीं बोर्ड परीक्षा",
        "dash_days_remaining" to "दिन शेष",
        "dash_streak" to "अध्ययन क्रम (Streak)",
        "dash_streak_days" to "दिन लगातार",
        "dash_verified_chapters" to "सत्यापित अध्याय",
        "dash_out_of" to "में से",
        "dash_chapters" to "अध्याय",
        "dash_total_xp" to "कुल अनुभव (XP)",
        "dash_level" to "स्तर (Level)",
        "dash_todays_targets" to "आज के अध्ययन लक्ष्य",
        "dash_no_targets_today" to "आज के लिए कोई लक्ष्य निर्धारित नहीं है। नया लक्ष्य बनाएं!",
        "dash_add_target" to "नया लक्ष्य जोड़ें",
        "dash_quick_test" to "अध्याय टेस्ट दें",
        "dash_schedule_rev" to "रिवीजन जोड़ें",
        "dash_recent_scores" to "हाल के परीक्षा परिणाम",
        "dash_no_tests_yet" to "अभी तक कोई टेस्ट नहीं दिया गया। अध्याय सत्यापित करने हेतु टेस्ट पास करें!",
        "dash_weak_chapters" to "कमजोर अध्याय (रिवीजन की आवश्यकता)",
        "dash_all_chapters_strong" to "शाबाश! सभी अध्याय मजबूत स्थिति में हैं।",

        // Statuses
        "status_not_started" to "प्रारंभ नहीं हुआ",
        "status_in_progress" to "प्रगति पर",
        "status_awaiting_verification" to "पढ़ा गया (सत्यापन प्रतीक्षित)",
        "status_verified_completed" to "सत्यापित एवं पूर्ण (Verified)",
        "status_needs_revision" to "रिवीजन आवश्यक",

        "target_pending" to "प्रतीक्षारत",
        "target_completed" to "पूर्ण",
        "target_rescheduled" to "अगले दिन स्थानांतरित",
        "target_cancelled" to "रद्द",

        // Targets Screen
        "targets_title" to "अध्ययन लक्ष्य",
        "targets_today" to "आज",
        "targets_upcoming" to "आगामी",
        "targets_completed" to "पूर्ण हुए",
        "target_new_dialog_title" to "नया अध्ययन लक्ष्य बनाएं",
        "target_title_hint" to "उदा. द्विघात समीकरण प्रश्नावली 4.2 हल करना",
        "target_subject" to "विषय",
        "target_chapter" to "अध्याय (वैकल्पिक)",
        "target_type" to "लक्ष्य का प्रकार",
        "target_date" to "लक्ष्य तिथि",
        "target_priority" to "प्राथमिकता",
        "target_notes" to "टिप्पणी (वैकल्पिक)",
        "target_mark_done" to "पूर्ण चिन्हित करें",
        "target_rollover_notice" to "अधूरे लक्ष्य अगले दिन स्वतः स्थानांतरित हो जाते हैं, कोई डेटा नष्ट नहीं होता।",

        // Syllabus Screen
        "syllabus_title" to "आरबीएसई कक्षा 10 आधिकारिक पाठ्यक्रम",
        "syllabus_verified_badge" to "सत्यापित ✓",
        "syllabus_unverified_badge" to "असत्यापित",
        "syllabus_passing_threshold" to "उत्तीर्ण अंक प्रतिशत",
        "syllabus_test_btn" to "सत्यापन टेस्ट दें",
        "syllabus_add_chapter" to "नया अध्याय जोड़ें",
        "syllabus_import" to "पाठ्यक्रम आयात / संपादन",

        // Revision Screen
        "revision_title" to "पुनरावृत्ति (Revision) प्रणाली",
        "revision_subtitle" to "कमजोर स्कोर एवं अनुत्तीर्ण टेस्ट के आधार पर स्वतः सुझाई गई पुनरावृत्ति",
        "revision_scheduled" to "निर्धारित सत्र",
        "revision_suggested" to "सुझाए गए कमजोर अध्याय",
        "revision_new_btn" to "रिवीजन शेड्यूल करें",
        "revision_mark_done" to "रिवीजन पूर्ण करें",

        // AI Tests Screen
        "ai_tests_title" to "एआई अध्याय परीक्षा एवं सत्यापन",
        "ai_tests_rule_notice" to "मूल नियम: केवल कार्य पूरा करना पढ़ाई का प्रमाण नहीं है। अध्याय तभी सत्यापित होगा जब आप टेस्ट पास करेंगे।",
        "ai_test_select_subject" to "विषय चुनें",
        "ai_test_select_chapter" to "अध्याय चुनें",
        "ai_test_questions_count" to "प्रश्नों की संख्या",
        "ai_test_difficulty" to "कठिनाई स्तर",
        "ai_test_start" to "परीक्षा प्रारंभ करें",
        "ai_test_generating" to "आरबीएसई पाठ्यक्रम अनुसार प्रश्न तैयार हो रहे हैं...",
        "ai_test_taking_q" to "प्रश्न",
        "ai_test_prev" to "पिछला",
        "ai_test_next" to "अगला",
        "ai_test_submit" to "परीक्षा जमा करें",
        "ai_test_confirm_submit" to "क्या आप अपने उत्तर जमा करना चाहते हैं?",
        "ai_test_confirm_yes" to "हाँ, जमा करें",
        "ai_test_cancel" to "रद्द करें",

        // Results Screen
        "results_title" to "परीक्षा परिणाम",
        "results_passed" to "उत्तीर्ण - अध्याय सफलतापूर्वक सत्यापित हुआ!",
        "results_failed" to "अनुत्तीर्ण - पुनरावृत्ति (Revision) आवश्यक है",
        "results_score" to "आपका प्राप्तांक",
        "results_threshold" to "न्यूनतम उत्तीर्ण अंक",
        "results_review" to "प्रश्नोत्तर विश्लेषण व व्याख्या",
        "results_mistake_practice" to "गलत प्रश्नों का अभ्यास",
        "results_fresh_test" to "नया टेस्ट दें",
        "results_back" to "पाठ्यक्रम पर वापस जाएं",

        // Reports Screen
        "reports_title" to "साप्ताहिक प्रगति एवं रिपोर्ट",
        "reports_weekly_summary" to "साप्ताहिक सारांश",
        "reports_targets_completed" to "पूर्ण किए गए लक्ष्य",
        "reports_tests_taken" to "दी गई परीक्षाएं",
        "reports_avg_score" to "औसत प्राप्तांक",
        "reports_xp_gained" to "अर्जित अनुभव (XP)",
        "reports_export_pdf" to "साप्ताहिक रिपोर्ट साझा/निर्यात करें",

        // Achievements Screen
        "achievements_title" to "अध्ययन क्रम एवं उपलब्धियां",
        "achievements_level_info" to "टारगेट 100 स्तर",
        "achievements_all_badges" to "पदक एवं सम्मान",
        "achievements_unlocked" to "प्राप्त हुआ ✓",
        "achievements_locked" to "तालाबंद",

        // Settings Screen
        "settings_title" to "सेटिंग्स",
        "settings_language" to "भाषा (Language)",
        "settings_theme" to "थीम मोड (Dark/Light)",
        "settings_accent" to "रंग शैली (Accent Palette)",
        "settings_exam_config" to "बोर्ड परीक्षा उलटी गिनती (Countdown)",
        "settings_ai_config" to "एआई प्रदाता एवं एपीआई कुंजी (API Key)",
        "settings_ai_provider" to "प्रदाता",
        "settings_ai_key_hint" to "एपीआई कुंजी दर्ज करें",
        "settings_test_conn" to "कनेक्शन जांचें",
        "settings_daily_limit" to "दैनिक एआई अनुरोध कोटा",
        "settings_data_backup" to "डेटा बैकअप एवं पुनर्स्थापना",
        "settings_export_data" to "स्थानीय डेटा निर्यात करें (JSON)",
        "settings_import_data" to "डेटा आयात करें",
        "settings_reset_data" to "सभी डेटा रीसेट करें",
        "settings_reset_confirm" to "क्या आप सुनिश्चित हैं? इससे सभी रिकॉर्ड मिट जाएंगे।",
        "settings_about" to "टारगेट 100 - आरबीएसई कक्षा 10 अध्ययन एवं अधिगम सत्यापन प्रणाली"
    )
}
