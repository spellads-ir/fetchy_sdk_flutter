import org.gradle.api.GradleException

group = "com.fetchy.fetchy_sdk_flutter"
version = "1.0-SNAPSHOT"

val fetchySdkProject = findProject(":fetchy-sdk")?.takeIf {
    it.projectDir.resolve("build.gradle.kts").exists() || it.projectDir.resolve("build.gradle").exists()
} ?: throw GradleException(
    """
    fetchy_sdk_flutter requires the :fetchy-sdk Gradle subproject.
    Add this to your app's android/settings.gradle.kts after the Flutter plugin loader:

        apply(from = File("<flutter-project-root>/.pub-cache/.../android/apply_fetchy_sdk_plugin_includes.settings.gradle.kts"))

    Or copy the discovery block from:
    https://github.com/spellads-ir/fetchy_sdk_flutter/blob/main/android/apply_fetchy_sdk_plugin_includes.settings.gradle.kts
    """.trimIndent()
)

allprojects {
    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.library")
}

val agpMajor = com.android.Version.ANDROID_GRADLE_PLUGIN_VERSION.substringBefore('.').toInt()
if (agpMajor < 9) {
    apply(plugin = "org.jetbrains.kotlin.android")
}

android {
    namespace = "com.fetchy.fetchy_sdk_flutter"

    compileSdk = 36

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

project.extensions.configure(org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension::class.java) {
    compilerOptions {
        jvmTarget = org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17
    }
}

dependencies {
    implementation(fetchySdkProject)

    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.mockito:mockito-core:5.0.0")
}
