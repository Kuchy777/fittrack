# FitTrack — monorepo

Monorepo aplikacji **FitTrack** zawierające backend (Spring Boot / Kotlin) i frontend (Android / Kotlin).

Zawartość pochodzi w 100% z dostarczonego dokumentu `paste.txt` — bez dodawania brakujących plików (encje, repozytoria, DTO, SecurityConfig, layouty XML, build.gradle itd. trzeba uzupełnić ręcznie).

## Struktura

```
fittrack/
├── backend/                          ← Spring Boot + Kotlin
│   ├── src/main/kotlin/com/fittrack/
│   │   ├── service/                  ← Auth, Profile, Diary, Recipe, Workout, Notification
│   │   └── controller/               ← REST endpoints
│   └── src/test/kotlin/com/fittrack/
│       └── FitTrackIntegrationTest.kt
│
├── android/                          ← Android Studio
│   └── app/src/main/
│       ├── kotlin/com/fittrack/
│       │   ├── ui/auth/              ← Login, Register, AuthViewModel
│       │   ├── ui/navigation/        ← MainFragment + BottomNav
│       │   ├── ui/diary/             ← Diary, BarcodeScanner, adapter, VM
│       │   ├── ui/recipes/           ← Recipes, adapter, VM
│       │   ├── ui/workout/           ← Workout, WorkoutMap (GPS), VM
│       │   ├── ui/profile/           ← Profile, VM
│       │   ├── service/              ← FitTrackFirebaseService (FCM)
│       │   └── worker/               ← WaterReminderWorker
│       ├── res/navigation/nav_graph.xml
│       └── AndroidManifest.xml
│
└── .github/workflows/ci.yml          ← GitHub Actions CI
```

## Schemat bazy danych

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────────────┐
│    users     │──1:1──│  user_profiles   │       │  notification_settings│
│──────────────│       │──────────────────│       │──────────────────────│
│ id PK        │       │ user_id FK       │       │ user_id FK           │
│ email UNIQUE │       │ weight_kg        │       │ water_reminders      │
│ password     │       │ height_cm        │       │ water_reminder_times │
│ role         │       │ daily_kcal_goal  │       └──────────────────────┘
└──────┬───────┘       │ activity_level   │
       │               │ goal             │
       │1:N            └──────────────────┘
       │
  ┌────▼──────────┐   ┌──────────────────┐   ┌──────────────────────────┐
  │ diary_entries │   │  food_products   │   │   workout_activities     │
  │───────────────│   │──────────────────│   │──────────────────────────│
  │ user_id FK    │──▶│ id PK            │   │ user_id FK               │
  │ entry_date    │   │ name             │   │ activity_date            │
  │ meal_type     │   │ barcode UNIQUE   │   │ activity_type            │
  │ product_id FK │   │ kcal_per_100g    │   │ duration_min             │
  │ recipe_id FK  │   │ protein_g        │   │ kcal_burned              │
  │ quantity_g    │   │ fat_g            │   │ distance_km              │
  │ kcal          │   │ carbs_g          │   └──────────┬───────────────┘
  │ photo_path    │   └──────────────────┘              │1:N
  │ synced        │                                     │
  └───────────────┘                             ┌───────▼──────────┐
                                                │  workout_tracks  │
  ┌─────────────┐   ┌───────────────────────┐  │──────────────────│
  │   recipes   │   │  recipe_ingredients   │  │ latitude         │
  │─────────────│──▶│───────────────────────│  │ longitude        │
  │ author_id FK│   │ recipe_id FK          │  │ altitude_m       │
  │ title       │   │ product_id FK         │  │ speed_ms         │
  │ tags (array)│   │ quantity_g            │  └──────────────────┘
  │ kcal/serving│   └───────────────────────┘
  └─────────────┘
  ┌─────────────────────┐   ┌──────────────────┐   ┌──────────────────┐
  │ weight_measurements │   │   water_logs     │   │  device_tokens   │
  │─────────────────────│   │──────────────────│   │──────────────────│
  │ user_id FK          │   │ user_id FK       │   │ user_id FK       │
  │ measured_at         │   │ log_date         │   │ fcm_token UNIQUE │
  │ weight_kg           │   │ amount_ml        │   │ device_name      │
  └─────────────────────┘   └──────────────────┘   └──────────────────┘
```

## Mapa endpointów (OpenAPI)

| Metoda    | Endpoint                       | Opis                          |
|-----------|--------------------------------|-------------------------------|
| POST      | `/api/auth/register`           | Rejestracja                   |
| POST      | `/api/auth/login`              | Logowanie → JWT               |
| POST      | `/api/auth/refresh`            | Odświeżenie tokenu            |
| GET/PUT   | `/api/profile`                 | Profil + kalkulacja kcal      |
| GET       | `/api/diary?date=`             | Wpisy dnia                    |
| POST      | `/api/diary`                   | Dodaj posiłek                 |
| DELETE    | `/api/diary/{id}`              | Usuń (swipe-to-delete)        |
| GET       | `/api/diary/summary?date=`     | Podsumowanie kcal             |
| GET       | `/api/recipes?q=&tag=`         | Wyszukaj/filtruj przepisy     |
| POST      | `/api/recipes`                 | Utwórz przepis                |
| GET/POST  | `/api/workouts`                | Treningi                      |
| POST      | `/api/workouts/{id}/track`     | Punkty GPS trasy              |
| GET/POST  | `/api/weight`                  | Historia wagi (wykres)        |
| POST      | `/api/water`                   | Zapis nawodnienia             |
| POST      | `/api/notifications/token`     | Rejestracja tokenu FCM        |
| PUT       | `/api/notifications/settings`  | Ustawienia powiadomień push   |

Swagger UI: `/swagger-ui.html` po uruchomieniu aplikacji.

## Kluczowe decyzje techniczne

### Backend
- **Flyway** — wersjonowanie schematu bazy, migracje przy każdym starcie
- **Offline-first** — pole `synced` w `diary_entries` pozwala aplikacji Android zapisywać lokalnie i synchronizować później
- **FCM** — push notifications przez `firebase-admin` SDK
- **Barcode** — pole `barcode` w `food_products` (kod EAN), endpoint `GET /api/food/barcode/{code}`
- **GPS tracking** — tabela `workout_tracks` z indeksem na `(workout_id, recorded_at)`
- **JWT** — accessToken (24h) + refreshToken (7d)

### Android
| Aspekt          | Rozwiązanie                                    |
|-----------------|------------------------------------------------|
| Architektura    | MVVM + Repository                              |
| DI              | Hilt                                           |
| Sieć            | Retrofit + OkHttp + AuthInterceptor            |
| Tokeny          | DataStore Preferences                          |
| Kamera          | CameraX + ML Kit (barcode)                     |
| Push            | FCM + WorkManager (przypomnienia 10/14/18)     |
| Mapy GPS        | Google Maps SDK + FusedLocationProvider        |
| Offline-first   | pole `synced` + bufor lokalny                  |
| Nawigacja       | Navigation Component + SafeArgs                |

## ⚠️ Status — brakujące pliki

Repo zawiera **wyłącznie** pliki z `paste.txt`. Aby projekt się zbudował, musisz dodatkowo wygenerować:

**Backend:**
- `FitTrackApplication.kt`, `build.gradle.kts`, `application.yml`
- `entity/Entities.kt` (User, UserProfile, DiaryEntry, FoodProduct, Recipe, RecipeIngredient, WorkoutActivity, WorkoutTrack, DeviceToken, NotificationSettings, …)
- `repository/Repositories.kt`
- `dto/Dtos.kt`
- `security/{JwtUtils, JwtAuthFilter, UserDetailsServiceImpl}.kt`
- `config/SecurityConfig.kt`
- `db/migration/V1__init_schema.sql` (Flyway)

**Android:**
- `FitTrackApp.kt` (`@HiltAndroidApp`), `MainActivity.kt`, `build.gradle.kts`
- `di/AppModule.kt`
- `data/api/FitTrackApi.kt`, `AuthInterceptor.kt`
- `data/model/Models.kt`
- `data/repository/{Auth,Diary,Recipe,Workout,Profile}Repository.kt`
- `ui/auth/SplashFragment.kt`, `ui/recipes/RecipeDetailFragment.kt`
- `util/{TokenManager,Resource}.kt`
- Wszystkie layouty XML: `fragment_login.xml`, `fragment_register.xml`, `fragment_main.xml`, `fragment_diary.xml`, `fragment_barcode_scanner.xml`, `fragment_recipes.xml`, `fragment_workout.xml`, `fragment_workout_map.xml`, `fragment_profile.xml`, `item_diary_entry.xml`, `item_recipe.xml`
- `res/values/{strings,colors,themes}.xml`, ikony w `res/drawable/`

Przygotowano przy użyciu Claude Sonnet 4.6 (oryginalny dokument).
