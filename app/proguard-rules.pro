# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in X:\android-sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

#-optimizationpasses 2
#-dontobfuscate
#-dontpreverify
#-dontusemixedcaseclassnames
#-dontskipnonpubliclibraryclasses
#-verbose
#-allowaccessmodification
#-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*,!code/allocation/variable
-keepattributes *Annotation*
#-optimizations !code/simplification/arithmetic,!code/simplification/cast,!field/*,!class/merging/*
#-optimizationpasses 4
#-allowaccessmodification
#-dontpreverify
#-dontusemixedcaseclassnames

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.content.Context
-keep public class * extends android.content

-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keepclassmembers class * extends android.content.Context {
   public void *(android.view.View);
   public void *(android.view.MenuItem);
}

-keepclassmembers class * implements android.os.Parcelable {
    static android.os.Parcelable$Creator CREATOR;
}

-keepclassmembers class **.R$* {
    public static <fields>;
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class wash.rocket.xor.rocketwash.model.**
-keepclassmembers class wash.rocket.xor.rocketwash.model.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

#RoboSpice requests and Results must be kept as they are used by reflection via Jackson
-keep class wash.rocket.xor.rocketwash.requests.**
-keepclassmembers class wash.rocket.xor.rocketwash.requests.** {
  public void set*(***);
  public *** get*();
  public *** is*();
}

-keepclassmembers,allowobfuscation class * {
    @org.codehaus.jackson.annotate.* <fields>;
    @org.codehaus.jackson.annotate.* <init>(...);
}

-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }

-keep class android.support.v4.app.** { *; }
-keep interface android.support.v4.app.** { *; }
-keep class android.support.v7.app.** { *; }
-keep interface android.support.v7.app.** { *; }

-keepattributes Signature
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

-keep class org.apache.http.** { *; }
-keep class org.apache.commons.codec.** { *; }
-keep class org.apache.commons.logging.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }
-keep class com.android.internal.http.multipart.** { *; }
-keep class com.octo.android.robospice.** { *; }
-dontwarn com.octo.android.robospice.**

-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.**

#-dontwarn org.apache.http.**
#-dontwarn android.webkit.**
#-dontwarn android.net.http.AndroidHttpClient
#-dontwarn com.google.android.gms.**
#-dontwarn android.webkit.**
#-dontwarn com.google.android.**
#-dontwarn java.lang.invoke**
#-dontwarn org.apache.lang.**
#-dontwarn org.apache.commons.**
#-dontwarn javax.xml.**
#-dontwarn com.aphidmobile.**

-dontwarn org.apache.**
-dontwarn com.google.**
-dontwarn org.slf4j.**

-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement