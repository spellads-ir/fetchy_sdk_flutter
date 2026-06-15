import 'fetchy_sdk_flutter_platform_interface.dart';

enum FetchyNotificationPermissionStatus { granted, denied, unknown }

class FetchySdkFlutter {
  /// Initializes the SDK.
  ///
  /// [onToken] is called once the device token becomes available from the
  /// backend. Because the SDK registers asynchronously via WorkManager, the
  /// token may not be ready immediately after [initialize] returns. The
  /// callback polls until the token is available (up to ~30 s) and then fires
  /// exactly once.
  Future<void> initialize({void Function(String token)? onToken}) async {
    await FetchySdkFlutterPlatform.instance.initialize();
    if (onToken != null) {
      _deliverToken(onToken);
    }
  }

  void _deliverToken(void Function(String token) onToken) async {
    for (var attempt = 0; attempt < 30; attempt++) {
      await Future<void>.delayed(const Duration(seconds: 1));
      final token = await getToken();
      if (token != null && token.isNotEmpty) {
        onToken(token);
        return;
      }
    }
  }

  Future<String?> getToken() {
    return FetchySdkFlutterPlatform.instance.getToken();
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
