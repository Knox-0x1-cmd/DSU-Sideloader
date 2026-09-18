# Hilt
-keep class dagger.hilt.** { *; }
-keep class * extends dagger.hilt.android.HiltAndroidApp { *; }
-keep class * extends dagger.hilt.android.HiltViewModel { *; }

# AboutLibraries
-keep class com.mikepenz.aboutlibraries.** { *; }

# Kotlinx Serialization
-keep class kotlinx.serialization.** { *; }

# Shizuku
-keep class rikka.shizuku.** { *; }

# LibSu
-keep class com.github.topjohnwu.libsu.** { *; }

# HiddenApiBypass
-keep class org.lsposed.hiddenapibypass.** { *; }

# Kotlin coroutines
-keep class kotlinx.coroutines.** { *; }

# Kotlinx serialization runtime
-keep class kotlinx.serialization.json.** { *; }

# LZ4 (net.jpountz.lz4) - prevent R8 stripping
-keep class net.jpountz.lz4.** { *; }
-keep class net.jpountz.xxhash.** { *; }
-keep class net.jpountz.util.** { *; }

# Zstd (optional, if added later)
-keep class org.zstd.** { *; }

# BZip2 (commons-compress)
-keep class org.apache.commons.compress.compressors.bzip2.** { *; }