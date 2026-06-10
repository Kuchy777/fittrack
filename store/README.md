# Materiały do Google Play

## Pliki tekstowe

- `short_description_pl.txt` / `short_description_en.txt` — krótki opis (max 80 znaków)
- `full_description_pl.txt` / `full_description_en.txt` — pełny opis (max 4000 znaków)
- `../PRIVACY.md` — polityka prywatności (do opublikowania jako strona WWW; URL trzeba podać w Konsoli Play)

## Materiały graficzne — PLACEHOLDER

Zgodnie z wymaganiami Google Play musisz dostarczyć:

| Asset | Wymiary | Format | Plik |
|---|---|---|---|
| Ikona aplikacji | 512×512 px | PNG (z kanałem alfa) | `icon/icon_512.png` (do dorobienia) |
| Feature graphic | 1024×500 px | PNG/JPG | `icon/feature_graphic.png` (do dorobienia) |
| Screenshoty telefon | min. 2, 1080×1920 do 3840×2160 | PNG/JPG | `screenshots/phone_*.png` |
| Screenshoty tablet 7" | opcjonalne | PNG/JPG | `screenshots/tablet7_*.png` |
| Screenshoty tablet 10" | opcjonalne | PNG/JPG | `screenshots/tablet10_*.png` |

### Jak zrobić screenshoty

Po zbudowaniu APK debug i uruchomieniu w emulatorze:

```powershell
adb shell screencap -p /sdcard/screen.png
adb pull /sdcard/screen.png screenshots/phone_01.png
```

Polecane ekrany do pokazania:
1. Logowanie / rejestracja
2. Dashboard z dziennym celem kcal
3. Dziennik posiłków + zdjęcie potrawy
4. Lista treningów
5. Biblioteka przepisów
6. Ustawienia powiadomień

### Ikona

Najszybciej wygenerować w Android Studio: **File → New → Image Asset → Launcher Icons (Adaptive and Legacy)**. Wynikowe `mipmap-*/ic_launcher.png` można powiększyć / wyrenderować w 512×512 do Konsoli Play.

## Krok po kroku — publikacja

1. Wygeneruj keystore (raz, trzymaj w bezpiecznym miejscu poza repo):

   ```powershell
   keytool -genkey -v -keystore fittrack-release.keystore -alias fittrack -keyalg RSA -keysize 2048 -validity 10000
   ```

2. Utwórz `android/keystore.properties` (na podstawie `keystore.properties.example`).

3. Zbuduj AAB:

   ```powershell
   cd android
   .\gradlew bundleRelease
   ```

   Wynikowy plik: `app/build/outputs/bundle/release/app-release.aab`.

4. Wejdź do Google Play Console → Utwórz aplikację → wczytaj AAB, uzupełnij metadane z tego folderu.

5. Podaj URL polityki prywatności (PRIVACY.md opublikowane jako GitHub Pages lub w innym miejscu z publicznym dostępem).
