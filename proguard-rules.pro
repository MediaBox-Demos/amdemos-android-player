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
# 指定代码的压缩级别 Specify the compression level of the code
-optimizationpasses 5
#混淆时不使用大小写混合类名 Don't use mixed case class names during proguard
-dontusemixedcaseclassnames
#不跳过library中的非public的类 Don't skip non-public classes in library
-dontskipnonpubliclibraryclasses
#打印混淆的详细信息 Print detailed information about proguard
-verbose
#忽略警告 Ignore warnings
-ignorewarnings
# Optimization is turned off by default. Dex does not like code run
# through the ProGuard optimize and preverify steps (and performs some
# of these optimizations on its own).
#不进行优化，建议使用此选项，理由见上 Not optimized, this option is recommended, for the reasons above
-dontoptimize
#不进行预校验，预校验是作用在Java平台上的，Android平台上不需要这项功能，去掉之后还可以加快混淆速度 No pre-verify, pre-verify is used on the Java platform, Android platform does not need this function, after removing it can speed up the proguard
-dontpreverify
-keep class com.aliyun.alivcsolution.MutiApplication
-keep class com.aliyun.player.demo.PlayerApplication
-keep class com.alivc.live.pusher.demo.LiveApplication
