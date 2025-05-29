
-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.** { *; }

# Keep Facebook SDK classes (if you are using Facebook Ads or Login)
# You might need more specific rules depending on your Facebook SDK usage.
# Often, the Facebook SDK provides its own ProGuard rules.
-keep class com.facebook.** { *; }
-keep interface com.facebook.** { *; }
-keep @com.facebook.infer.annotation.Nullsafe class *
-keepclassmembers class * {
    @com.facebook.infer.annotation.Nullsafe *;
}

# Keep Facebook Ads SDK annotations
-keep class com.facebook.infer.annotation.** { *; }

# Keep JSpecify annotations (used by Google Mediation Test Suite)
-keep class org.jspecify.nullness.** { *; }

# Keep classes related to Facebook Ads
-keep class com.facebook.ads.** { *; }
-dontwarn com.facebook.ads.**

# Keep classes related to Google Mobile Ads Mediation Test Suite
# You might need to be more specific if these are too broad,
# but start with this and refine if necessary.
-keep class com.google.android.ads.mediationtestsuite.** { *; }
-dontwarn com.google.android.ads.mediationtestsuite.**


