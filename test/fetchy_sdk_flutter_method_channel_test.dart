import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter.dart';
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter_method_channel.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelFetchySdkFlutter platform =
      MethodChannelFetchySdkFlutter();
  const MethodChannel channel = MethodChannel('fetchy_sdk_flutter');
  final List<MethodCall> capturedCalls = <MethodCall>[];

  setUp(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
          capturedCalls.add(methodCall);
          switch (methodCall.method) {
            case 'getNotificationPermissionStatus':
            case 'syncNotificationPermissionStatus':
              return 'granted';
          }
          return null;
        });
  });

  tearDown(() {
    capturedCalls.clear();
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('initialize forwards method call', () async {
    await platform.initialize();

    expect(capturedCalls.single.method, 'initialize');
  });

  test('permission status mapping works', () async {
    final status = await platform.getNotificationPermissionStatus();

    expect(capturedCalls.single.method, 'getNotificationPermissionStatus');
    expect(status, FetchyNotificationPermissionStatus.granted);
  });

  test('permission sync forwards method call', () async {
    final status = await platform.syncNotificationPermissionStatus();

    expect(capturedCalls.single.method, 'syncNotificationPermissionStatus');
    expect(status, FetchyNotificationPermissionStatus.granted);
  });
}
