# Pliki wymagające zewnętrznych SDK

Te pliki **nie są częścią głównego buildu**, ponieważ wymagają konfiguracji usług zewnętrznych:

- `firebase/FitTrackFirebaseService.kt` — Firebase Cloud Messaging. Wymaga `google-services.json` z konsoli Firebase i pluginu `com.google.gms.google-services`.
- `maps/WorkoutMapFragment.kt` — Google Maps SDK + FusedLocationProvider. Wymaga ważnego klucza Maps API w `MAPS_API_KEY` i włączenia Google Play Services w emulatorze.
- `BarcodeScannerFragment.kt` — CameraX + ML Kit. Wymaga `play-services-mlkit-barcode-scanning`.

## Jak je włączyć

1. Dodaj zależności w `app/build.gradle.kts`:

   ```kotlin
   // Firebase
   implementation(platform("com.google.firebase:firebase-bom:33.1.1"))
   implementation("com.google.firebase:firebase-messaging-ktx")

   // Maps + Location
   implementation("com.google.android.gms:play-services-maps:18.2.0")
   implementation("com.google.android.gms:play-services-location:21.3.0")

   // CameraX + ML Kit
   implementation("androidx.camera:camera-core:1.3.4")
   implementation("androidx.camera:camera-camera2:1.3.4")
   implementation("androidx.camera:camera-lifecycle:1.3.4")
   implementation("androidx.camera:camera-view:1.3.4")
   implementation("com.google.mlkit:barcode-scanning:17.3.0")
   ```

2. Dodaj plik `app/google-services.json` (Firebase Console).
3. W `app/build.gradle.kts` dodaj plugin `id("com.google.gms.google-services")`.
4. Ustaw `MAPS_API_KEY` w `manifestPlaceholders` na prawdziwy klucz.
5. Przenieś pliki z tego katalogu do `app/src/main/kotlin/com/fittrack/...`.
6. W layoucie `fragment_workout_map.xml` użyj `SupportMapFragment` (już przygotowane).

W projekcie domyślnym **przypomnienia o wodzie** (WorkManager) działają lokalnie bez Firebase — wystarczą do demonstracji w emulatorze.
