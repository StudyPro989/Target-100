# Target 100 — RBSE Class 10 Study Planner & AI Verification

> **Core Rule:** *A completed task is not proof of learning. A chapter becomes verified only after the student passes its configured test.*

**Target 100** is an offline-first Android application designed for RBSE Class 10 students in Rajasthan, India. It combines goal-driven study targets, official syllabus tracking, AI-powered multiple-choice chapter tests, automated weak-chapter revision recommendations, study streaks, and performance analytics.

---

## 🌟 Key Architecture & Highlights

### 1. Verification State Machine
Chapter progress is strictly decoupled from study task completion:
* `NOT_STARTED`: Chapter has not been initiated.
* `IN_PROGRESS`: Target created or study commenced.
* `STUDIED_AWAITING_VERIFICATION`: Study targets completed, awaiting test verification.
* `VERIFIED_COMPLETED`: Successfully passed chapter verification test with score ≥ configured threshold (default: 75%).
* `NEEDS_REVISION`: Test failed or revision test failed; flagged for revision without deleting historical completion records.

### 2. Authentic RBSE Class 10 Syllabus
Pre-populated with official RBSE subjects and chapters:
* **Mathematics (गणित):** Real Numbers, Polynomials, Linear Equations, Quadratic Equations, Arithmetic Progressions, Triangles, Coordinate Geometry, Trigonometry, Circles, Statistics, Probability.
* **Science (विज्ञान):** Chemical Reactions, Acids & Bases, Metals, Carbon, Life Processes, Control & Coordination, Reproduction, Heredity, Light, Electricity, Magnetism, Environment.
* **Social Science (सामाजिक विज्ञान - SST):** History, Geography, Democratic Politics, Economics.
* **Hindi (हिंदी):** Kshitij, Kritika, Grammar (व्यावहारिक व्याकरण).
* **English:** First Flight, Footprints Without Feet, Grammar & Composition.
* **Sanskrit (संस्कृत):** Shemushi, Grammar.
* **Information Technology (IT - Optional):** Digital Documentation, Spreadsheets, DBMS, Web Security.

### 3. Modular AI Provider Architecture
* **Google Gemini Adapter:** Direct REST API integration with `gemini-3.5-flash` using structured JSON schema.
* **OpenAI-Compatible Adapter:** Configurable chat-completion endpoint for custom server setups.
* **Offline Question Bank:** Authentic RBSE questions included locally for 100% offline study without requiring API keys or network access.
* **Daily Quota Enforcement:** Configurable daily request limits (default: 15/day) tracked locally and reset according to the device's local calendar.
* **Deterministic Local Scoring:** Answers are evaluated deterministically against locally generated answer keys, not from opaque AI estimations.

### 4. Target Lifecycle & Idempotent Rollover
* **Target Types:** Chapter Study, Revision, Practice Questions, Mock Test, Custom Task.
* **Automatic Rollover:** Unfinished targets from previous days automatically roll over to today idempotently without duplicate records, preserving original due date and logging reschedule history.

### 5. Gamification & Anti-Abuse Rules
* **Base XP (+50 XP):** Awarded upon completing a legitimate study target.
* **Verification Bonus (+150 XP):** Awarded upon passing a chapter verification test.
* **Revision XP (+75 XP):** Awarded for completing revision sessions.
* **Study Streak:** A day qualifies only when the student completes at least 1 valid target satisfying test criteria (never awarded merely for opening the app).
* **Badges:** First Milestone, High 5 Verified, Target Crusher, Centurion Spirit (90%+), Revision Master, 7-Day Warrior, RBSE 100 Champion.

### 6. Full English & Hindi Localization
Switchable in real time from the top bar or settings without restarting the application.

---

## 🛠 Tech Stack
* **Language:** Kotlin
* **UI Framework:** Jetpack Compose with Material Design 3
* **Local Persistence:** Room Database (SQLite) with KSP
* **Networking & JSON:** OkHttp, Retrofit, org.json
* **Testing:** JUnit local unit tests
