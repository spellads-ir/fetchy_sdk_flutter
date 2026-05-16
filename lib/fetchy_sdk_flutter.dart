import 'fetchy_sdk_flutter_platform_interface.dart';

enum FetchyNotificationPermissionStatus { granted, denied, unknown }

class FetchySdkFlutter {
  Future<void> initialize() {
    return FetchySdkFlutterPlatform.instance.initialize();
  }

  Future<FetchyNotificationPermissionStatus>
  getNotificationPermissionStatus() {
    return FetchySdkFlutterPlatform.instance
        .getNotificationPermissionStatus();
  }

  Future<FetchyNotificationPermissionStatus>
  syncNotificationPermissionStatus() {
    return FetchySdkFlutterPlatform.instance
        .syncNotificationPermissionStatus();
  }
}
