rootProject.name = "fetchy_sdk_flutter"

include(":fetchy-sdk")
val localSdkProjectDir = File(settingsDir, "../../fetchy_sdk/fetchy-sdk")
val importedSdkProjectDir = File(settingsDir, "third_party/fetchy-android/fetchy-sdk")
project(":fetchy-sdk").projectDir = if (localSdkProjectDir.exists()) localSdkProjectDir else importedSdkProjectDir
