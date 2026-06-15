import java.net.URL
import java.util.zip.ZipInputStream

rootProject.name = "fetchy_sdk_flutter"

val embeddedSdkCommit = "5da42ee4eef11cec680626ee8bcd7d26fce2a798"
val embeddedSdkArchiveUrl = "https://github.com/spellads-ir/fetchy_sdk/archive/$embeddedSdkCommit.zip"
val localSdkProjectDir = File(settingsDir, "../../fetchy_sdk/fetchy-sdk")
val importedSdkProjectDir = File(settingsDir, "third_party/fetchy-android/fetchy-sdk")
val downloadedSdkRootDir = File(settingsDir, "build/embedded-sdk/fetchy_sdk-$embeddedSdkCommit")
val downloadedSdkProjectDir = File(downloadedSdkRootDir, "fetchy-sdk")
val downloadedArchiveFile = File(settingsDir, "build/embedded-sdk/fetchy_sdk-$embeddedSdkCommit.zip")

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

fun resolveEmbeddedSdkProjectDir(): File {
	if (importedSdkProjectDir.exists()) {
		return importedSdkProjectDir
	}

	if (localSdkProjectDir.exists()) {
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

include(":fetchy-sdk")
project(":fetchy-sdk").projectDir = resolveEmbeddedSdkProjectDir()
