import 'package:flutter_test/flutter_test.dart';
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter.dart';
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter_method_channel.dart';
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter_platform_interface.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockFetchySdkFlutterPlatform
    with MockPlatformInterfaceMixin
    implements FetchySdkFlutterPlatform {
  @override
  Future<void> initialize() async {}

  @override
  Future<FetchyNotificationPermissionStatus>
  getNotificationPermissionStatus() async {
    return FetchyNotificationPermissionStatus.granted;
  }

  @override
  Future<FetchyNotificationPermissionStatus>
  syncNotificationPermissionStatus() async {
    return FetchyNotificationPermissionStatus.granted;
  }
}

void main() {
  final FetchySdkFlutterPlatform initialPlatform =
      FetchySdkFlutterPlatform.instance;

  test('$MethodChannelFetchySdkFlutter is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelFetchySdkFlutter>());
  });

  test('getNotificationPermissionStatus', () async {
    FetchySdkFlutter fetchySdkFlutterPlugin = FetchySdkFlutter();
    MockFetchySdkFlutterPlatform fakePlatform =
        MockFetchySdkFlutterPlatform();
    FetchySdkFlutterPlatform.instance = fakePlatform;

    expect(
      await fetchySdkFlutterPlugin.getNotificationPermissionStatus(),
      FetchyNotificationPermissionStatus.granted,
    );
  });
}
