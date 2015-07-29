# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/jca/Android/sdk/tools/proguard/proguard-android.txt
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


-allowaccessmodification
-keepattributes *Annotation*,Signature,Exceptions,InnerClasses,EnclosingMethod

-dontoptimize

-keepclasseswithmembernames class * { native <methods>; }
-keepclassmembers enum * {public static **[] values();public static ** valueOf(java.lang.String);}


-dontshrink

-keep public class * implements com.coremedia.**
-keep public class * implements com.googlecode.**
-keep public class * implements com.mp4parser.**
-keep class com.coremedia.**
-keep class com.googlecode.** { *; }
-keep class com.mp4parser.** { *; }

-keep public class com.videonasocialmedia.videona.utils.VideoUtils

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Retrofit, OkHttp, Gson
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**
-dontwarn rx.**
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

#Okio
-dontwarn okio.**

-dontwarn javax.xml.bind.DatatypeConverter
-dontwarn org.apache.commons.codec.binary.Base64

#Glide
-keep class org.aspectj.**
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}


-keepclassmembers class com.googlecode.**


# Aspect 클래스 보존
-keep @org.aspectj.lang.annotation.Aspect class * { *; }
-keepclasseswithmembers class * {
  public static *** aspectOf();
}

# EventBus
-keepclassmembers class ** {
    public void onEvent*(**);
}


