# Ktor
-keep class io.ktor.** { *; }
-keepclassmembers class io.ktor.** { *; }
-dontwarn io.ktor.**

# Koin
-keep class org.koin.** { *; }
-dontwarn org.koin.**

# kotlinx.serialization
-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt
-keep,includedescriptorclasses class pt.socialfood.**$$serializer { *; }
-keepclassmembers class pt.socialfood.** {
    *** Companion;
}
-keepclasseswithmembers class pt.socialfood.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Kamel
-keep class kamel.** { *; }
-dontwarn kamel.**

# Multiplatform Settings
-keep class com.russhwolf.settings.** { *; }
-dontwarn com.russhwolf.settings.**

# Kotlin coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-dontwarn kotlinx.coroutines.**

# Keep serializable data classes
-keepclassmembers @kotlinx.serialization.Serializable class * {
    *** Companion;
    *** INSTANCE;
    kotlinx.serialization.KSerializer serializer(...);
}

# Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Credential Manager / Google Sign-In
# The Play-Services-backed credential provider is discovered via reflection at
# runtime. Without these keep rules, R8 strips/renames it in minified release
# builds, so CredentialManager.getCredential() finds no provider and throws
# NoCredentialException: "No credentials available".
-keep class androidx.credentials.** { *; }
-dontwarn androidx.credentials.**
-keep class com.google.android.gms.auth.api.identity.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.google.android.libraries.identity.googleid.** { *; }
-dontwarn com.google.android.libraries.identity.googleid.**
