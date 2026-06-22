
# 📱 SmartCampus AI

> An AI-powered Android mobile application designed to transform the academic experience of university students.

![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack_Compose-4285F4?style=for-the-badge&logo=android&logoColor=white)
![Android](https://img.shields.io/badge/Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Room](https://img.shields.io/badge/Room_DB-FF6F00?style=for-the-badge&logo=android&logoColor=white)
![AI](https://img.shields.io/badge/AI_Integration-FF4081?style=for-the-badge&logo=openai&logoColor=white)

---

## 📌 Overview

SmartCampus AI is a comprehensive Android application built with modern Android development technologies. It integrates intelligent features that assist students in managing their academic life — from tracking assignments and deadlines to monitoring attendance and accessing an AI-powered study assistant.

---

## ✨ Features

- 📋 **Task & Assignment Management** — Create, track, and manage academic deadlines
- 📅 **Smart Timetable** — Organize weekly class schedules with reminders
- ✅ **Attendance Tracker** — Monitor attendance per subject with threshold alerts
- 📝 **Smart Notes** — Create and organize notes with category support
- 🤖 **AI Study Assistant** — Integrated OpenAI/Gemini API for academic support
- 🔔 **Background Notifications** — WorkManager & Foreground Services for smart reminders
- 🌙 **Dark / Light Theme** — Full Material 3 design system support

---

## 🛠️ Tech Stack

| Category | Technology |
|----------|-----------|
| Language | Kotlin |
| UI Framework | Jetpack Compose |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Hilt |
| Local Database | Room |
| Background Processing | WorkManager, Foreground Services |
| AI Integration | OpenAI / Gemini API |
| Design System | Material 3 |
| Min SDK | Android API 26+ |

---

## 🏗️ Architecture

```
app/
├── ui/
│   ├── screens/        # All Compose screens
│   ├── components/     # Reusable UI components
│   ├── navigation/     # Nav graph
│   └── theme/          # Material 3 theme
├── data/
│   ├── local/          # Room database & DAOs
│   └── repository/     # Repository layer
├── domain/             # Use cases & models
└── util/               # Extensions & helpers
```

---

## 📸 Modules

- Authentication (Login/Register)
- Dashboard (Home overview)
- Tasks & Assignments
- Attendance Tracker
- Timetable Manager
- Smart Notes
- AI Study Assistant
- Settings & Preferences

---

## 🚀 Getting Started

1. Clone the repository
```bash
git clone https://github.com/HadiaKarim16/SmartCampusAI.git
```
2. Open in **Android Studio**
3. Add your API key for AI integration in `local.properties`
4. Build and run on an Android device or emulator (API 26+)

---

## 👩‍💻 Developed By

**Hadia Karim** — Computer Science Graduate, Sukkur IBA University (2026)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0a66c2?style=flat&logo=linkedin)](https://www.linkedin.com/in/hadia-karim/)
[![Portfolio](https://img.shields.io/badge/Portfolio-Visit-7c5cfc?style=flat&logo=google-chrome)](https://hadiakarim16.github.io/Personal_Portfolio/)
[![Email](https://img.shields.io/badge/Email-Contact-teal?style=flat&logo=gmail)](mailto:hadiakarim37@gmail.com)
