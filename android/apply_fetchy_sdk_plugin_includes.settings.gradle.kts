import groovy.json.JsonSlurper
import java.io.File

val flutterProjectRoot = settingsDir.parentFile
if (flutterProjectRoot != null) {
    val depsFile = File(flutterProjectRoot, ".flutter-plugins-dependencies")
    if (depsFile.exists()) {
        @Suppress("UNCHECKED_CAST")
        val meta = JsonSlurper().parseText(depsFile.readText()) as Map<String, Any>
        val plugins = meta["plugins"] as? Map<String, Any>
        val androidPlugins = plugins?.get("android") as? List<*>
        if (androidPlugins != null) {
            for (pluginEntry in androidPlugins) {
                val plugin = pluginEntry as? Map<*, *> ?: continue
                if (plugin["name"] != "fetchy_sdk_flutter") {
                    continue
                }

                val path = plugin["path"] as? String ?: continue
                apply(from = File(path, "android/include_fetchy_sdk.settings.gradle.kts"))
                break
            }
        }
    }
}
