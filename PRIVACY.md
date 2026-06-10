# Polityka prywatności — FitTrack

Ostatnia aktualizacja: 10 czerwca 2026

## 1. Kto jest administratorem danych

Administratorem danych jest Jakub Kucharski (kontakt: jakub.kucharski36@gmail.com), twórca aplikacji FitTrack. Aplikacja jest projektem open-source.

## 2. Jakie dane są przetwarzane

FitTrack przetwarza wyłącznie dane podane dobrowolnie przez użytkownika i niezbędne do działania aplikacji:

- **Dane konta:** e-mail i hasło (przechowywane jako hash BCrypt na backendzie wskazanym w konfiguracji aplikacji).
- **Dane profilu:** pseudonim, płeć, data urodzenia, waga, wzrost, poziom aktywności, cel diety. Wykorzystywane do obliczenia dziennego zapotrzebowania kalorycznego.
- **Wpisy w dzienniku:** posiłki, treningi, ilość wypitej wody, opcjonalne zdjęcia potraw zrobione aparatem.
- **Tokeny JWT** (access + refresh) używane do uwierzytelnienia sesji.

FitTrack **nie pobiera** lokalizacji (GPS), kontaktów, listy zainstalowanych aplikacji ani identyfikatorów reklamowych.

## 3. Cel przetwarzania

Dane są przetwarzane wyłącznie w celu świadczenia funkcji aplikacji: prowadzenie dziennika, kalkulacja kcal, wyświetlanie historii posiłków i treningów, lokalne powiadomienia.

## 4. Gdzie są przechowywane dane

Dane są zapisywane:

- W bazie SQLite na backendzie skonfigurowanym przez użytkownika (domyślnie własny serwer).
- Lokalnie na urządzeniu — tokeny w DataStore, zdjęcia w katalogu aplikacji.

FitTrack **nie korzysta** z usług Firebase, Google Analytics, sieci reklamowych ani innych dostawców trzecich do śledzenia użytkowników.

## 5. Powiadomienia

Wszystkie powiadomienia (woda, posiłki, ruch) są generowane lokalnie przez WorkManager na urządzeniu. Nie są wysyłane przez serwer pushowy.

## 6. Uprawnienia aplikacji

- `INTERNET` — komunikacja z backendem.
- `CAMERA` — opcjonalnie, do robienia zdjęć potraw.
- `POST_NOTIFICATIONS` (Android 13+) — lokalne przypomnienia.

## 7. Prawa użytkownika

Zgodnie z RODO użytkownik ma prawo do:

- dostępu do swoich danych,
- ich sprostowania,
- usunięcia konta (wystarczy wysłać prośbę na adres e-mail powyżej),
- przenoszenia danych (eksport JSON z backendu).

## 8. Bezpieczeństwo

- Hasła są hashowane algorytmem BCrypt.
- Komunikacja z backendem powinna odbywać się przez HTTPS (TLS) — w wersji produkcyjnej własnego serwera.
- Tokeny JWT są podpisywane HS256 z sekretem 256-bit.

## 9. Zmiany w polityce

W razie zmian najnowsza wersja będzie publikowana w repozytorium GitHub aplikacji: https://github.com/Kuchy777/fittrack

## 10. Kontakt

W sprawach związanych z danymi osobowymi: **jakub.kucharski36@gmail.com**
