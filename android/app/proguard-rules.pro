# FitTrack — minimalne reguły ProGuard (release build).
# Domyślne reguły AGP wystarczają dla zależności użytych w projekcie.
-keepattributes *Annotation*
-keep class com.fittrack.data.model.** { *; }
