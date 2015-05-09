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

-keep public class com.videonasocialmedia.videona.utils.VideoUtils

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewInjector { *; }

-keepclasseswithmembernames class * {
    @butterknife.InjectView <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.OnClick <methods>;
    @butterknife.OnEditorAction <methods>;
    @butterknife.OnItemClick <methods>;
    @butterknife.OnItemLongClick <methods>;
    @butterknife.OnLongClick <methods>;
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



-keep class org.aspectj.**
-keep class com.coremedia.**
-keep class com.googlecode.** { *; }
-keep class com.mp4parser.** { *; }
-keepclassmembers class com.googlecode.**


# Aspect 클래스 보존
-keep @org.aspectj.lang.annotation.Aspect class * { *; }
-keepclasseswithmembers class * {
  public static *** aspectOf();
}


