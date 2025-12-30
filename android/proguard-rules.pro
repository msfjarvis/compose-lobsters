-dontobfuscate
-keepattributes SourceFile, LineNumberTable

# Keep Glance ActionCallback classes for reflection
-keep class * implements androidx.glance.action.ActionCallback {
    public <init>();
}
