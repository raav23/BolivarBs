# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\CASA\AppData\Local\Android\sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:
-keep class appr.softectachira.com.bolivarbs.SalasJuego { *; }
-keep class appr.softectachira.com.bolivarbs.IngresarFondos_transferencia { *; }
-keep class appr.softectachira.com.bolivarbs.ObjetoInsercionToFirebase { <init>(java.lang.String,double,int,double); }
-keep class appr.softectachira.com.bolivarbs.ObjetoInsercionToFirebase { *; }
-keep class appr.softectachira.com.bolivarbs.ObjetoInsercionToFirebase { <init>(double); }
-keep class javax.** { *; }
-keep class org.** { *; }
-dontwarn javax.management.**
-dontwarn java.lang.management.**
-dontwarn org.apache.log4j.**
-dontwarn org.apache.commons.logging.**
-dontwarn org.slf4j.**
-dontwarn org.json.*





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
#-renamesourcefileattribute SourceFileç
-ignorewarnings

