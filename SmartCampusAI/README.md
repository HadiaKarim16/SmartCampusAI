# 🎓 SmartCampus AI — Intelligent Student Productivity Assistant

<p align="center">
  <img src="app/src/main/res/drawable/ic_splash.xml" width="120"/>
</p>

> A professional, industry-grade Android application for university students. Manage academic life, tasks, notes, attendance, timetable, and AI-powered study assistance — all in one place.

---

## 📱 Screenshots

| Login | Home Dashboard | Tasks | AI Assistant |
|-------|---------------|-------|-------------|
| Auth flow with validation | Greeting, stats, quick actions | Create, filter, reminder | Chat with AI study assistant |

---

## 🏗 Architecture

```
SmartCampusAI/
├── app/src/main/java/com/smartcampus/ai/
│   ├── SmartCampusApp.kt           ← Application class, DI init, channels
│   ├── di/
│   │   └── AppModule.kt            ← Hilt DI module (DB, DAOs, Network)
│   ├── data/
│   │   ├── local/
│   │   │   ├── SmartCampusDatabase.kt
│   │   │   ├── dao/Daos.kt         ← Room DAOs for all entities
│   │   │   └── entity/             ← Entities + Mappers
│   │   ├── remote/
│   │   │   └── AiService.kt        ← OkHTTP + JSON AI integration
│   │   ├── repository/
│   │   │   └── Repositories.kt     ← All repositories (Repository Pattern)
│   │   └── preferences/
│   │       └── PreferencesManager.kt ← DataStore preferences
│   ├── domain/
│   │   └── model/
│   │       └── Models.kt           ← Domain models + UiState sealed class
│   ├── ui/
│   │   ├── MainActivity.kt         ← Entry point + bottom nav scaffold
│   │   ├── navigation/Navigation.kt ← Routes + NavGraph + BottomNavItems
│   │   ├── theme/Theme.kt          ← Material3 dark/light color schemes
│   │   ├── components/Components.kt ← Reusable Composables
│   │   └── screens/
│   │       ├── auth/               ← Login, Signup screens + ViewModel
│   │       ├── home/               ← Dashboard screen + ViewModel
│   │       ├── tasks/              ← Tasks list, Add/Edit + ViewModel
│   │       ├── notes/              ← Notes list, Add/Edit + ViewModel
│   │       ├── attendance/         ← Attendance tracker + ViewModel
│   │       ├── timetable/          ← Weekly timetable + ViewModel
│   │       ├── ai_assistant/       ← AI chat screen + ViewModel
│   │       ├── pomodoro/           ← Focus timer screen + ViewModel
│   │       └── settings/           ← Settings screen + ViewModel
│   ├── service/
│   │   └── PomodoroService.kt      ← Foreground service for timer
│   ├── worker/
│   │   └── Workers.kt              ← WorkManager workers (reminders, alerts)
│   └── util/
│       ├── NotificationHelper.kt   ← WorkManager scheduling helper
│       └── Extensions.kt           ← Kotlin extension functions
```

---

## 🚀 Tech Stack

| Layer | Technology |
|-------|-----------|
| Language | Kotlin |
| UI | Jetpack Compose + Material3 |
| Architecture | MVVM + Clean Architecture |
| DI | Hilt |
| Database | Room (SQLite) |
| Preferences | DataStore |
| Background | WorkManager + Foreground Service |
| Network | OkHTTP |
| Navigation | Navigation Compose |
| Async | Coroutines + StateFlow |

---

## ✨ Features

### 🔐 Authentication
- Login & Sign Up with validation
- Remember Me / session persistence via DataStore
- Secure password handling

### 🏠 Home Dashboard
- Personalized greeting by time of day
- Stats: pending tasks, completed tasks, focus time
- Quick action panel (6 shortcuts)
- Motivational quotes from API
- Today's class schedule
- Upcoming deadlines
- Attendance shortage alerts

### ✅ Task & Assignment Manager
- Create tasks with title, subject, description, deadline, priority
- Filter by: All / Pending / Completed / Urgent
- Search by title or subject
- Mark complete/incomplete with one tap
- WorkManager-powered deadline reminders
- Priority color coding: Low → Medium → High → Urgent

### 📝 Smart Notes
- Rich text notes with title, content, category
- Pin important notes
- Custom color-coded notes
- Search across all notes
- Category filtering

### 📊 Attendance Tracker
- Add subjects with required attendance threshold
- Mark Present / Absent per class
- Animated progress bars
- Auto-calculates shortage & classes needed to recover
- Visual semester overview

### 📅 Timetable
- Weekly timetable organized by day
- Add class with subject, teacher, room, time, color
- Day-selector with class count indicators

### 🤖 AI Study Assistant
- Chat interface with AI (OpenAI/Gemini ready)
- Subject-specific context selection
- Quick prompt chips
- Offline mock responses (no API key needed for demo)
- Typing indicator, auto-scroll, chat history persistence

### 🍅 Pomodoro Focus Timer
- Circular progress timer with gradient arc
- Foreground service — runs in background
- Pause / Resume / Stop controls
- Break timer with skip option
- Session counter with visual bubbles
- Total focus time tracking

### ⚙️ Settings
- Dark / Light mode toggle
- Notification controls
- Adjustable Pomodoro & Break duration
- Attendance threshold configuration
- Sign out with confirmation

---

## 🛠 Setup Instructions

### 1. Clone the project
```bash
git clone https://github.com/yourusername/SmartCampusAI.git
cd SmartCampusAI
```

### 2. Open in Android Studio
- Use **Android Studio Ladybug** or newer
- Sync Gradle (it will auto-download dependencies)

### 3. (Optional) Add AI API Key
Open `AiService.kt` and replace:
```kotlin
private const val API_KEY = "YOUR_API_KEY_HERE"
```
With your actual OpenAI or Gemini API key. Without it, the app uses intelligent mock responses.

### 4. Build & Run
- Connect a device or start an emulator (API 26+)
- Click **Run ▶** in Android Studio

### 5. Generate APK
- **Build → Generate Signed Bundle / APK**
- Select APK → choose release keystore → build

---

## 📦 Dependencies

All dependencies are managed via `gradle/libs.versions.toml`:

- `androidx.compose.bom:2024.10.00`
- `hilt-android:2.51.1`
- `room:2.6.1`
- `workmanager:2.9.1`
- `okhttp:4.12.0`
- `datastore-preferences:1.1.1`
- `navigation-compose:2.8.3`
- `coil-compose:2.7.0`
- `core-splashscreen:1.0.1`

---

## 🔒 Permissions Used

```xml
INTERNET              — AI API & motivational quotes
POST_NOTIFICATIONS    — Task reminders & class alerts
FOREGROUND_SERVICE    — Pomodoro background timer
RECEIVE_BOOT_COMPLETED — Restore reminders after reboot
READ_MEDIA_IMAGES     — Note image attachments (Android 13+)
SCHEDULE_EXACT_ALARM  — Precise deadline reminders
```

---

## 🎨 Design System

| Token | Value |
|-------|-------|
| Primary | `#6C63FF` (Electric Violet) |
| Secondary | `#00D4AA` (Cyan) |
| Tertiary | `#FFB74D` (Amber) |
| Background | `#0F0F1A` |
| Surface | `#1A1A2E` |
| Error | `#FF5370` |

---

## 📄 License

MIT License — free for academic, portfolio, and personal use.

---

## 👨‍💻 Author

Built with ❤️ for 8th Semester Final Year Project  
SmartCampus AI — Empowering students with intelligent tools
