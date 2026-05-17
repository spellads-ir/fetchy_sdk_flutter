import org.gradle.api.GradleException
import org.gradle.api.tasks.GradleBuild
import java.net.URL
import java.util.Properties
import java.util.zip.ZipInputStream

group = "com.fetchy.fetchy_sdk_flutter"
version = "1.0-SNAPSHOT"

val embeddedSdkVersion = "1.3.8"
val embeddedSdkCommit = "5da42ee4eef11cec680626ee8bcd7d26fce2a798"
val embeddedSdkArchiveUrl = "https://github.com/spellads-ir/fetchy_sdk/archive/$embeddedSdkCommit.zip"
val localSdkRootDir = projectDir.resolve("../../fetchy_sdk")
val vendoredSdkRootDir = projectDir.resolve("third_party/fetchy-android")
val downloadedSdkRootDir = layout.buildDirectory.dir("embedded-sdk/fetchy_sdk-$embeddedSdkCommit")

fun File.hasEmbeddedSdkModule(): Boolean {
    return resolve("fetchy-sdk/build.gradle.kts").exists() || resolve("fetchy-sdk/build.gradle").exists()
}

fun resolveEmbeddedSdkRootDir(): File {
    return when {
        localSdkRootDir.hasEmbeddedSdkModule() -> localSdkRootDir
        vendoredSdkRootDir.hasEmbeddedSdkModule() -> vendoredSdkRootDir
        else -> downloadedSdkRootDir.get().asFile
    }
}

fun downloadFile(sourceUrl: String, targetFile: File) {
    targetFile.parentFile.mkdirs()
    URL(sourceUrl).openStream().use { input ->
        targetFile.outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

fun readPropertyFromFile(propertyFile: File, propertyName: String): String? {
    if (!propertyFile.exists()) {
        return null
    }

    val properties = Properties()
    propertyFile.inputStream().use { input ->
        properties.load(input)
    }

    return properties.getProperty(propertyName)?.takeIf { it.isNotBlank() }
}

fun resolveAndroidSdkDir(): String? {
    val localPropertiesCandidates = listOf(
        rootProject.file("local.properties"),
        project.file("local.properties"),
        rootProject.projectDir.parentFile?.resolve("local.properties"),
    ).filterNotNull().distinct()

    for (propertyFile in localPropertiesCandidates) {
        val sdkDir = readPropertyFromFile(propertyFile, "sdk.dir")
        if (sdkDir != null) {
            return sdkDir
        }
    }

    return System.getenv("ANDROID_HOME")?.takeIf { it.isNotBlank() }
        ?: System.getenv("ANDROID_SDK_ROOT")?.takeIf { it.isNotBlank() }
}

fun ensureEmbeddedSdkLocalProperties(embeddedSdkRootDir: File) {
    val sdkDir = resolveAndroidSdkDir()
        ?: throw GradleException(
            "Android SDK location not found. Set sdk.dir in local.properties or define ANDROID_HOME/ANDROID_SDK_ROOT before building fetchy_sdk_flutter."
        )

    val localPropertiesFile = embeddedSdkRootDir.resolve("local.properties")
    val properties = Properties()

    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { input ->
            properties.load(input)
        }
    }

    if (properties.getProperty("sdk.dir") == sdkDir) {
        return
    }

    localPropertiesFile.parentFile.mkdirs()
    properties.setProperty("sdk.dir", sdkDir)
    localPropertiesFile.outputStream().use { output ->
        properties.store(output, null)
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

val prepareEmbeddedFetchySdkSource by tasks.registering {
    val downloadedArchive = layout.buildDirectory.file("embedded-sdk/fetchy_sdk-$embeddedSdkCommit.zip")

    onlyIf {
        !localSdkRootDir.hasEmbeddedSdkModule() && !vendoredSdkRootDir.hasEmbeddedSdkModule()
    }

    outputs.file(downloadedArchive)
    outputs.dir(downloadedSdkRootDir)

    doLast {
        val archiveFile = downloadedArchive.get().asFile
        val extractedRootDir = downloadedSdkRootDir.get().asFile
        val moduleBuildFile = extractedRootDir.resolve("fetchy-sdk/build.gradle.kts")

        if (moduleBuildFile.exists()) {
            return@doLast
        }

        if (!archiveFile.exists()) {
            downloadFile(embeddedSdkArchiveUrl, archiveFile)
        }

        unzipGitHubArchive(archiveFile, extractedRootDir)
    }
}

val embeddedSdkAarFile = provider {
    resolveEmbeddedSdkRootDir().resolve("fetchy-sdk/build/outputs/aar/fetchy-sdk-release.aar")
}

val buildEmbeddedFetchySdk by tasks.registering(GradleBuild::class) {
    dependsOn(prepareEmbeddedFetchySdkSource)
    doFirst {
        ensureEmbeddedSdkLocalProperties(resolveEmbeddedSdkRootDir())
    }
    dir = resolveEmbeddedSdkRootDir()
    tasks = listOf(":fetchy-sdk:assembleRelease")
}

buildscript {
    val kotlinVersion = "1.9.24"
    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:8.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
    }
}

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    namespace = "com.fetchy.fetchy_sdk_flutter"

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }

    sourceSets {
        getByName("main") {
            java.srcDirs("src/main/kotlin", "src/main/java")
        }
        getByName("test") {
            java.srcDirs("src/test/kotlin")
        }
    }

    defaultConfig {
        minSdk = 21
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all {
                it.useJUnitPlatform()

                it.outputs.upToDateWhen { false }

                it.testLogging {
                    events("passed", "skipped", "failed", "standardOut", "standardError")
                    showStandardStreams = true
                }
            }
        }
    }
}

dependencies {
    val embeddedSdkProject = findProject(":fetchy-sdk")?.takeIf {
        it.projectDir.resolve("build.gradle.kts").exists() || it.projectDir.resolve("build.gradle").exists()
    }
    if (embeddedSdkProject != null) {
        implementation(embeddedSdkProject)
    } else {
        implementation("com.fetchy:fetchy-sdk:$embeddedSdkVersion")
    }

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.mockito:mockito-core:5.0.0")
}
