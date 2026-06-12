# FitTrack

Monorepo aplikacji **FitTrack** — kontroler kalorii i aktywności fizycznej.
Zawiera backend (Spring Boot + Kotlin + SQLite + JWT) oraz aplikację Android (Kotlin + MVVM + Hilt + Retrofit).

## Funkcje

- Rejestracja i logowanie (JWT access + refresh)
- Profil użytkownika z automatyczną kalkulacją dziennego celu kcal (Mifflin–St Jeor + TDEE)
- Dziennik posiłków: dodawanie wpisów, swipe-to-delete, dzienne podsumowanie kalorii i makro
- **Dodawanie posiłku ze zdjęciem** — aparat robi zdjęcie potrawy, użytkownik podaje nazwę i gramaturę, wpis trafia do dziennika
- Przepisy niskokaloryczne (przeglądanie i tworzenie własnych)
- Trening: ręczne dodawanie aktywności, dzienny licznik spalonych kcal
- **Lokalne powiadomienia push** o piciu wody (co 2h), posiłkach (co 4h) i treningu (raz dziennie) — przez WorkManager, bez Firebase
- OpenAPI / Swagger UI

## Struktura monorepo

```
fittrack/
├── android/         aplikacja Android (Kotlin, Gradle Kotlin DSL)
├── backend/         backend Spring Boot (Kotlin, SQLite, JWT)
└── README.md        ten plik
```

## Stack

### Backend
- Kotlin 1.9, Spring Boot 3.3.2
- Spring Data JPA + Hibernate 6 + dialect SQLite
- Spring Security + BCrypt + JWT (jjwt 0.12)
- springdoc-openapi 2.6 (Swagger UI)
- JUnit 5 + MockK + H2 + JaCoCo (raport pokrycia)

### Android
- Kotlin 1.9, AGP 8.5, minSdk 26, targetSdk 34
- MVVM + Repository, Hilt DI, Navigation Component
- Retrofit 2 + Moshi + OkHttp logging
- Coroutines + StateFlow, ViewBinding
- DataStore Preferences (tokeny JWT)
- WorkManager (cykliczne powiadomienia lokalne)
- ActivityResultContracts.TakePicture + FileProvider (aparat)

## Wymagania

- JDK 17 (zalecane Eclipse Adoptium)
- Android Studio Hedgehog (2023.1.1) lub nowszy
- Gradle 8.7 (wrapper w repo)

## Uruchomienie — backend

```bash
cd backend
./gradlew bootRun        # Linux/macOS
gradlew.bat bootRun      # Windows cmd
.\gradlew bootRun        # Windows PowerShell
```

Backend startuje na `http://localhost:8080`. Plik bazy SQLite (`fittrack.db`) tworzony jest automatycznie w katalogu roboczym.

**Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
**OpenAPI JSON**: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

Szybki test:

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"email":"test@fittrack.pl","password":"tajne123"}'
```

### JDK 17 na Windowsie

Plik `backend/gradle.properties` zawiera wskazanie `org.gradle.java.home` na lokalną instalację Eclipse Adoptium. Jeśli masz JDK 17 w innym miejscu — zmień tę ścieżkę lub ustaw `JAVA_HOME` przez `setx JAVA_HOME "..."` i otwórz nowy terminal.

## Uruchomienie — Android

1. Otwórz katalog `android/` w Android Studio
2. Uruchom emulator (API 26+)
3. Uruchom aplikację (Shift + F10)

Aplikacja łączy się z backendem przez `http://10.0.2.2:8080/` — to alias hosta dla emulatora Androida.

## Testy

```bash
cd backend
./gradlew test                            # uruchamia wszystkie testy
./gradlew jacocoTestReport                # raport pokrycia w build/reports/jacoco
./gradlew jacocoTestCoverageVerification  # weryfikacja >=60% pokrycia
```

Pokrycie testami obejmuje wszystkie serwisy logiki biznesowej oraz JwtUtils. Testy integracyjne uruchamiają pełny kontekst Springa na in-memory H2.

## Endpointy API

| Metoda  | Ścieżka                         | Auth      | Opis                              |
|---------|---------------------------------|-----------|-----------------------------------|
| POST    | `/api/auth/register`            | publiczne | Rejestracja                       |
| POST    | `/api/auth/login`               | publiczne | Logowanie                         |
| POST    | `/api/auth/refresh`             | publiczne | Odświeżenie access token          |
| GET     | `/api/profile`                  | JWT       | Pobierz profil                    |
| PUT     | `/api/profile`                  | JWT       | Aktualizuj profil + cel kcal      |
| GET     | `/api/diary?date=YYYY-MM-DD`    | JWT       | Wpisy dnia                        |
| POST    | `/api/diary`                    | JWT       | Dodaj wpis (też zdjęcie potrawy)  |
| DELETE  | `/api/diary/{id}`               | JWT       | Usuń wpis                         |
| GET     | `/api/diary/summary?date=...`   | JWT       | Dzienne podsumowanie kcal i makro |
| GET     | `/api/food/search?q=`           | JWT       | Wyszukaj produkt                  |
| GET     | `/api/recipes?q=&tag=`          | publiczne | Lista przepisów                   |
| POST    | `/api/recipes`                  | JWT       | Stwórz przepis                    |
| GET     | `/api/workouts?date=...`        | JWT       | Treningi danego dnia              |
| POST    | `/api/workouts`                 | JWT       | Dodaj trening                     |
| PUT     | `/api/notifications/settings`   | JWT       | Preferencje powiadomień           |

Pełny opis i schematy w Swagger UI.

## Powiadomienia lokalne

Aplikacja **nie używa Firebase**. Cykliczne przypomnienia są planowane przez `WorkManager` i wyświetlane jako lokalne `NotificationCompat`:

- woda — co 2 godziny
- posiłki — co 4 godziny
- trening — raz dziennie

Na Android 13+ aplikacja pyta o uprawnienie `POST_NOTIFICATIONS` przy pierwszym uruchomieniu.

## Bezpieczeństwo

- Hasła przechowywane jako BCrypt (siła 10)
- JWT podpisany HS256 z 256-bitowym sekretem (Base64 w `application.yml`)
- Access token 1h, refresh token 30 dni
- CORS otwarty na potrzeby developmentu — przed wdrożeniem produkcyjnym ograniczyć
- Sekret JWT w `application.yml` należy zmienić przed wdrożeniem na produkcję

## Licencja i autor

Projekt akademicki, autor: .
