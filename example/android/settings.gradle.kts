pluginManagement {
    val flutterSdkPath =
        run {
            val properties = java.util.Properties()
            file("local.properties").inputStream().use { properties.load(it) }
            val flutterSdkPath = properties.getProperty("flutter.sdk")
            require(flutterSdkPath != null) { "flutter.sdk not set in local.properties" }
            flutterSdkPath
        }

    includeBuild("$flutterSdkPath/packages/flutter_tools/gradle")

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

plugins {
    id("dev.flutter.flutter-plugin-loader") version "1.0.0"
    id("com.android.application") version "9.0.1" apply false
    id("com.android.library") version "9.0.1" apply false
    id("org.jetbrains.kotlin.android") version "2.3.20" apply false
    id("com.google.devtools.ksp") version "2.3.7" apply false
}

include(":app")

// Include :fetchy-sdk for fetchy_sdk_flutter (path from .flutter-plugins-dependencies).
run {
    val flutterRoot = settingsDir.parentFile ?: return@run
    val depsFile = java.io.File(flutterRoot, ".flutter-plugins-dependencies")
    if (!depsFile.exists()) return@run

    @Suppress("UNCHECKED_CAST")
    val meta = groovy.json.JsonSlurper().parseText(depsFile.readText()) as Map<String, Any>
    val plugins = meta["plugins"] as? Map<String, Any> ?: return@run
    val androidPlugins = plugins["android"] as? List<*> ?: return@run

    for (entry in androidPlugins) {
        val plugin = entry as? Map<*, *> ?: continue
        if (plugin["name"] == "fetchy_sdk_flutter") {
            val path = plugin["path"] as? String ?: continue
            apply(from = java.io.File(path, "android/include_fetchy_sdk.settings.gradle.kts"))
            break
        }
    }
}
