# FamilyApp 👨‍👩‍👧‍👦

A complete family management platform built with **Kotlin** — featuring a Spring Boot backend with admin dashboard, and an Android app with Jetpack Compose.

## Features

| Feature | Description |
|---------|-------------|
| 📅 **Shared Calendar** | Colour-coded family schedule with monthly/weekly views |
| 📋 **Lists & To-Do** | Shared lists with progress tracking (grocery, chores, etc.) |
| 💰 **Expense Tracker** | Family finances with bill tracking and savings goals |
| 🍽️ **Meal Planner** | Weekly meal plans with breakfast/lunch/dinner slots |
| 💬 **Family Chat** | Group messaging with text, photo & video support |
| 📌 **Notice Board** | Pinned family announcements and reminders |
| 📍 **Location Tracking** | See family members' last known location |
| 🔐 **Admin Dashboard** | Web-based admin panel for user and family management |

---

## Project Structure

```
FamilyApp/
├── backend/                          # Spring Boot Backend
│   ├── build.gradle.kts
│   └── src/main/kotlin/com/familyapp/
│       ├── FamilyAppApplication.kt   # Entry point
│       ├── model/                    # JPA Entities
│       ├── repository/               # Data access layer
│       ├── service/                  # Business logic
│       ├── controller/api/           # REST API endpoints
│       ├── controller/admin/         # Admin dashboard controller
│       ├── config/                   # Security, JWT, seeder
│       └── dto/                      # Request/Response DTOs
│
├── android/                          # Android App
│   └── app/src/main/java/com/familyapp/
│       ├── MainActivity.kt
│       ├── data/api/                 # Retrofit API service
│       ├── data/model/               # Data transfer objects
│       └── ui/
│           ├── navigation/           # Nav graph + bottom bar
│           ├── theme/                # Material 3 theme
│           └── screens/              # All feature screens
│
└── README.md
```

---

## Getting Started

### Backend

**Requirements:** Java 17+, Gradle 8.5+

```bash
cd FamilyApp/backend
./gradlew bootRun
```

- **API:** http://localhost:8080/api/
- **Admin Dashboard:** http://localhost:8080/admin/
- **H2 Console:** http://localhost:8080/h2-console

**Default Admin Login:**
- Email: `admin@familyapp.com`
- Password: `admin123`

### Android App

Open `FamilyApp/android/` in **Android Studio** (Hedgehog+) and run on emulator or device.

The app connects to the backend at `http://10.0.2.2:8080/api/` (Android emulator default for localhost).

---

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login & get JWT |
| GET | `/api/calendar` | Get family events |
| POST | `/api/calendar` | Create event |
| GET | `/api/lists` | Get family lists |
| POST | `/api/lists` | Create list |
| GET | `/api/expenses` | Get expenses |
| POST | `/api/expenses` | Add expense |
| GET | `/api/meals` | Get meal plans |
| POST | `/api/meals` | Create meal plan |
| GET | `/api/messages` | Get chat messages |
| POST | `/api/messages` | Send message |
| GET | `/api/noticeboard` | Get notices |
| POST | `/api/location` | Update location |

All API endpoints (except auth) require `Authorization: Bearer <token>` header.

---

## Tech Stack

### Backend
- Kotlin 1.9 + Spring Boot 3.2
- Spring Security (JWT + Session)
- Spring Data JPA + H2 Database
- Thymeleaf + Bootstrap 5 (Admin UI)

### Android
- Kotlin + Jetpack Compose
- Material Design 3
- Hilt (Dependency Injection)
- Retrofit (HTTP Client)
- Navigation Compose
- DataStore (Local Preferences)
