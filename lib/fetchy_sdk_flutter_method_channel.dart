import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'fetchy_sdk_flutter.dart';
import 'fetchy_sdk_flutter_platform_interface.dart';

/// An implementation of [FetchySdkFlutterPlatform] that uses method channels.
class MethodChannelFetchySdkFlutter extends FetchySdkFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('fetchy_sdk_flutter');

  @override
  Future<void> initialize() async {
    await methodChannel.invokeMethod<void>('initialize');
  }

  @override
  Future<String?> getToken() async {
    return methodChannel.invokeMethod<String>('getToken');
  }

  @override
  Future<FetchyNotificationPermissionStatus>
  getNotificationPermissionStatus() async {
    final rawStatus = await methodChannel.invokeMethod<String>(
      'getNotificationPermissionStatus',
    );
    return _mapPermissionStatus(rawStatus);
  }

  @override
  Future<FetchyNotificationPermissionStatus>
  syncNotificationPermissionStatus() async {
    final rawStatus = await methodChannel.invokeMethod<String>(
      'syncNotificationPermissionStatus',
    );
    return _mapPermissionStatus(rawStatus);
  }
}

FetchyNotificationPermissionStatus _mapPermissionStatus(String? rawStatus) {
  switch (rawStatus) {
    case 'granted':
      return FetchyNotificationPermissionStatus.granted;
    case 'denied':
      return FetchyNotificationPermissionStatus.denied;
    case 'unknown':
    default:
      return FetchyNotificationPermissionStatus.unknown;
  }
}
