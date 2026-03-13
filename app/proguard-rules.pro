# MediaPipe
-keep class com.google.mediapipe.** { *; }
-dontwarn com.google.mediapipe.**

# Room
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class *

# Hilt / Dagger
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Auto-value (used by MediaPipe/Protobuf internally)
-dontwarn javax.annotation.processing.**
-dontwarn javax.lang.model.**
-dontwarn com.google.auto.value.**
-dontwarn autovalue.shaded.**

# Protobuf
-dontwarn com.google.protobuf.**
-keep class com.google.protobuf.** { *; }

# Kotlin Coroutines
-dontwarn kotlinx.coroutines.**

# Keep Compose
-dontwarn androidx.compose.**

# Accompanist
-dontwarn com.google.accompanist.**
