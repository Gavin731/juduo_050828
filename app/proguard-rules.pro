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

# 友盟
-keep class com.umeng.** {*;}

-keep class org.repackage.** {*;}

-keep class com.uyumao.** { *; }

-keepclassmembers class * {
   public <init> (org.json.JSONObject);
}

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

-keep class com.uc.** { *; }

-keep public class com.film.television.R$*{
    public static final int *;
}


# 保留某个包下的所有类
-keep class com.common.wheel.http.entity.** { *; }
-keep class com.common.wheel.entity.** { *; }
-keep class com.blankj.utilcode.**.** { *; }
-keep class com.common.wheel.http.BaseUrl { *; }
-keep class com.common.wheel.admanager.OpenScreenAdCallBack { *; }
-keep class com.common.wheel.admanager.RewardAdCallBack { *; }
# 保留所有公共类及其公共方法
-keep public class com.common.wheel.admanager.AdvertisementManager{
    public *;
}
-keep class com.common.wheel.admanager.InfoAdCallBack { *; }
-keep class com.common.wheel.admanager.InitCallback { *; }
-keep class com.common.wheel.admanager.InformationFlowAdCallback { *; }