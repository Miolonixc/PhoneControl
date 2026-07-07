# Proguard rules for Phone Control
-keepattributes *Annotation*
-keepattributes SourceFile,LineNumberTable

# Keep Compose
-keep class androidx.compose.** { *; }

# Keep Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }
