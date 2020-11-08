-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

-keep,includedescriptorclasses class dev.msfjarvis.lobsters.model.**$$serializer { *; }
-keepclassmembers class dev.msfjarvis.lobsters.model.** {
    *** Companion;
}
-keepclasseswithmembers class dev.msfjarvis.lobsters.model.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Workaround for https://github.com/ktorio/ktor/issues/1354
-keepclassmembers class io.ktor.** { volatile <fields>; }

# Workaround for https://github.com/Kotlin/kotlinx.coroutines/issues/1564
-keepclassmembers class kotlinx.** {
    volatile <fields>;
}
