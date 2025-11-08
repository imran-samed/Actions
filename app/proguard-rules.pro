# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
## Rules for Retrofit2
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# OkHttp 3
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn org.bouncycastle.jsse.BCSSLParameters
-dontwarn org.bouncycastle.jsse.BCSSLSocket
-dontwarn org.bouncycastle.jsse.provider.BouncyCastleJsseProvider
-dontwarn org.conscrypt.Conscrypt$Version
-dontwarn org.conscrypt.Conscrypt
-dontwarn org.conscrypt.ConscryptHostnameVerifier
-dontwarn org.openjsse.javax.net.ssl.SSLParameters
-dontwarn org.openjsse.javax.net.ssl.SSLSocket
-dontwarn org.openjsse.net.ssl.OpenJSSE

# --- Gson/Retrofit Model Classes ---
-keep class com.stpauls.dailyliturgy.base.BaseResponse { *; }
-keep class com.stpauls.dailyliturgy.localDb.tables.GodsWordBean { *; }
-keep class com.stpauls.dailyliturgy.localDb.tables.ReadingBean { *; }
-keep class com.stpauls.dailyliturgy.orderOfMass.OrderOfMassBean { *; }
-keep class com.stpauls.dailyliturgy.commonPrayers.bean.PrayerBean { *; }
-keep class com.stpauls.dailyliturgy.popularHymns.bean.PopularHymnsBean { *; }

# Keep all model classes in these packages (if you add more models, this will help):
-keep class com.stpauls.dailyliturgy.base.** { *; }
-keep class com.stpauls.dailyliturgy.localDb.tables.** { *; }
-keep class com.stpauls.dailyliturgy.orderOfMass.** { *; }
-keep class com.stpauls.dailyliturgy.commonPrayers.bean.** { *; }
-keep class com.stpauls.dailyliturgy.popularHymns.bean.** { *; }

# Keep Gson annotations and generic type signatures
-keepattributes Signature
-keepattributes *Annotation*

# --- Gson Core Library ---
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keep class sun.misc.Unsafe { *; }

# Retain generic signatures of TypeToken and its subclasses with R8 version 3.0 and higher.
-keep,allowobfuscation,allowshrinking class com.google.gson.reflect.TypeToken
-keep,allowobfuscation,allowshrinking class * extends com.google.gson.reflect.TypeToken

-keep class com.google.gson.reflect.TypeToken
-keep class * extends com.google.gson.reflect.TypeToken
-keep public class * implements java.lang.reflect.Type
