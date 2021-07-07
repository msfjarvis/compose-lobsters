-keepattributes SourceFile,LineNumberTable
-keepattributes RuntimeVisibleAnnotations,
                RuntimeVisibleParameterAnnotations,
                RuntimeVisibleTypeAnnotations,
                AnnotationDefault
-renamesourcefileattribute SourceFile
# Retain the generic signature of retrofit2.Call until added to Retrofit.
# Issue: https://github.com/square/retrofit/issues/3580.
# Pull request: https://github.com/square/retrofit/pull/3579.
-keep,allowobfuscation,allowshrinking class retrofit2.Call

# Retain annotation default values for all annotations.
# Required until R8 version >= 3.1.12-dev (expected in AGP 7.1.0-alpha4).
-keep,allowobfuscation,allowshrinking @interface *

-keep,allowobfuscation,allowshrinking class dev.msfjarvis.claw.android.** { *; }
