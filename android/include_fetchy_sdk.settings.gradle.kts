import groovy.json.JsonSlurper
import java.io.File
import java.net.URL
import java.util.zip.ZipInputStream

// Applied from the host app's android/settings.gradle.kts after the Flutter plugin loader.
// Resolves and includes :fetchy-sdk so fetchy_sdk_flutter can depend on it as a project.

val embeddedSdkCommit = "f021bb7456f211e8877295a67868a9b0472ccaa2"
val embeddedSdkArchiveUrl = "https://github.com/spellads-ir/fetchy_sdk/archive/$embeddedSdkCommit.zip"

fun downloadFile(sourceUrl: String, targetFile: File) {
    targetFile.parentFile.mkdirs()
    URL(sourceUrl).openStream().use { input ->
        targetFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

fun unzipGitHubArchive(archiveFile: File, targetDir: File) {
    if (targetDir.exists()) {
        targetDir.deleteRecursively()
    }
    targetDir.mkdirs()

    val canonicalTargetDir = targetDir.canonicalFile
    ZipInputStream(archiveFile.inputStream().buffered()).use { zipInputStream ->
        var entry = zipInputStream.nextEntry
        while (entry != null) {
            val relativePath = entry.name.substringAfter('/', "")
            if (relativePath.isNotEmpty()) {
                val outputFile = File(targetDir, relativePath)
                val canonicalOutputFile = outputFile.canonicalFile
                check(canonicalOutputFile.toPath().startsWith(canonicalTargetDir.toPath())) {
                    "Unsafe archive entry: ${entry.name}"
                }

                if (entry.isDirectory) {
                    canonicalOutputFile.mkdirs()
                } else {
                    canonicalOutputFile.parentFile.mkdirs()
                    canonicalOutputFile.outputStream().use { output ->
                        zipInputStream.copyTo(output)
                    }
                }
            }
            zipInputStream.closeEntry()
            entry = zipInputStream.nextEntry
        }
    }
}

fun resolveFetchySdkProjectDir(pluginAndroidDir: File): File {
    val localSdkProjectDir = File(pluginAndroidDir, "../../fetchy_sdk/fetchy-sdk")
    val importedSdkProjectDir = File(pluginAndroidDir, "third_party/fetchy-android/fetchy-sdk")
    val downloadedSdkRootDir = File(pluginAndroidDir, "build/embedded-sdk/fetchy_sdk-$embeddedSdkCommit")
    val downloadedSdkProjectDir = File(downloadedSdkRootDir, "fetchy-sdk")
    val downloadedArchiveFile = File(pluginAndroidDir, "build/embedded-sdk/fetchy_sdk-$embeddedSdkCommit.zip")

    if (importedSdkProjectDir.resolve("build.gradle.kts").exists() ||
        importedSdkProjectDir.resolve("build.gradle").exists()
    ) {
        return importedSdkProjectDir
    }

    if (localSdkProjectDir.resolve("build.gradle.kts").exists() ||
        localSdkProjectDir.resolve("build.gradle").exists()
    ) {
        return localSdkProjectDir
    }

    val moduleBuildFile = downloadedSdkProjectDir.resolve("build.gradle.kts")
    if (!moduleBuildFile.exists()) {
        if (!downloadedArchiveFile.exists()) {
            downloadFile(embeddedSdkArchiveUrl, downloadedArchiveFile)
        }
        unzipGitHubArchive(downloadedArchiveFile, downloadedSdkRootDir)
    }

    return downloadedSdkProjectDir
}

fun findFetchySdkFlutterAndroidDir(flutterProjectRoot: File): File? {
    val depsFile = File(flutterProjectRoot, ".flutter-plugins-dependencies")
    if (!depsFile.exists()) {
        return null
    }

    @Suppress("UNCHECKED_CAST")
    val meta = JsonSlurper().parseText(depsFile.readText()) as Map<String, Any>
    val plugins = meta["plugins"] as? Map<String, Any> ?: return null
    val androidPlugins = plugins["android"] as? List<*> ?: return null

    for (pluginEntry in androidPlugins) {
        val plugin = pluginEntry as? Map<*, *> ?: continue
        if (plugin["name"] == "fetchy_sdk_flutter") {
            val path = plugin["path"] as? String ?: return null
            return File(path, "android")
        }
    }

    return null
}

if (findProject(":fetchy-sdk") == null) {
    val flutterProjectRoot = settingsDir.parentFile
        ?: error("Could not resolve Flutter project root from ${settingsDir}")
    val pluginAndroidDir = findFetchySdkFlutterAndroidDir(flutterProjectRoot)
        ?: error(
            "fetchy_sdk_flutter is listed in .flutter-plugins-dependencies but its Android directory could not be resolved."
        )

    include(":fetchy-sdk")
    project(":fetchy-sdk").projectDir = resolveFetchySdkProjectDir(pluginAndroidDir)
}
