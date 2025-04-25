# Firebase Crashlytics rules
-keepattributes *Annotation*
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Prevent obfuscation of Crashlytics classes and methods
-keep public class * extends java.lang.Exception
-keep class * implements com.google.firebase.crashlytics.internal.CrashlyticsAppQualitySessionsListener { *; }
-keep class * implements com.google.firebase.crashlytics.internal.network.HttpRequest { *; }

# Prevent obfuscation of the Crashlytics NDK symbols
-keepclassmembers class * {
    @com.google.firebase.crashlytics.internal.annotation.CrashlyticsOrigin *;
}

# Keep StackTraceElement names and line numbers
-keepattributes SourceFile,LineNumberTable