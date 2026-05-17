rootProject.name = "fetchy_sdk_flutter"

val localSdkProjectDir = File(settingsDir, "../../fetchy_sdk/fetchy-sdk")
val importedSdkProjectDir = File(settingsDir, "third_party/fetchy-android/fetchy-sdk")
val embeddedSdkProjectDir = when {
	localSdkProjectDir.exists() -> localSdkProjectDir
	importedSdkProjectDir.exists() -> importedSdkProjectDir
	else -> null
}

if (embeddedSdkProjectDir != null) {
	include(":fetchy-sdk")
	project(":fetchy-sdk").projectDir = embeddedSdkProjectDir
}
