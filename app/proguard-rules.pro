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

# Mixpanel
-dontwarn com.mixpanel.**
-keep class com.mixpanel.android.abtesting.** { *; }
-keep class com.mixpanel.android.mpmetrics.** { *; }
-keep class com.mixpanel.android.surveys.** { *; }
-keep class com.mixpanel.android.util.** { *; }
-keep class com.mixpanel.android.java_websocket.** { *; }
-keep class **.R
-keep class **.R$* { <fields>; }

# Butterknife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

# Retrofit, OkHttp, Gson
-keep class com.squareup.okhttp3.** { *; }
-dontwarn com.squareup.okhttp3.**
-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keepattributes Signature
-keepattributes Exceptions
-keepattributes Annotation
-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}
# Also you must note that if you are using GSON for conversion from JSON to POJO representation, you must ignore those POJO classes from being obfuscated.
# Here include the POJO's that have you have created for mapping JSON response to POJO for example.
-keep class com.videonasocialmedia.videona.auth.repository.model.** { *; }
-keep class com.videonasocialmedia.videona.auth.domain.model.** { *; }
-keep class com.videonasocialmedia.videona.auth.repository.localsource.** { *; }
-keep class com.videonasocialmedia.videona.promo.repository.model.** { *; }

# Proguard Configuration for Realm (http://realm.io)
# For detailed discussion see: https://groups.google.com/forum/#!topic/realm-java/umqKCc50JGU
# Additionally you need to keep your Realm Model classes as well
# For example:
# -keep class com.yourcompany.realm.** { *; }

-keep class io.realm.annotations.RealmModule
-keep @io.realm.annotations.RealmModule class *
-keep class io.realm.internal.Keep
-keep @io.realm.internal.Keep class *
-dontwarn javax.**
-dontwarn io.realm.**

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

# LeakCanary. @See https://github.com/square/leakcanary/blob/master/leakcanary-android/consumer-proguard-rules.pro
-dontwarn com.squareup.haha.guava.**
-dontwarn com.squareup.haha.perflib.**
-dontwarn com.squareup.haha.trove.**
-dontwarn com.squareup.leakcanary.**
-keep class com.squareup.haha.** { *; }
-keep class com.squareup.leakcanary.** { *; }

# Marshmallow removed Notification.setLatestEventInfo()
-dontwarn android.app.Notification