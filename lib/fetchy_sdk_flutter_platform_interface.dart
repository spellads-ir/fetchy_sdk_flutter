import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'fetchy_sdk_flutter.dart';
import 'fetchy_sdk_flutter_method_channel.dart';

abstract class FetchySdkFlutterPlatform extends PlatformInterface {
  /// Constructs a FetchySdkFlutterPlatform.
  FetchySdkFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static FetchySdkFlutterPlatform _instance =
      MethodChannelFetchySdkFlutter();

  /// The default instance of [FetchySdkFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelFetchySdkFlutter].
  static FetchySdkFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [FetchySdkFlutterPlatform] when
  /// they register themselves.
  static set instance(FetchySdkFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<void> initialize() {
    throw UnimplementedError('initialize() has not been implemented.');
  }

  Future<String?> getToken() {
    throw UnimplementedError('getToken() has not been implemented.');
  }

  Future<FetchyNotificationPermissionStatus>
  getNotificationPermissionStatus() {
    throw UnimplementedError(
      'getNotificationPermissionStatus() has not been implemented.',
    );
  }

  Future<FetchyNotificationPermissionStatus>
  syncNotificationPermissionStatus() {
    throw UnimplementedError(
      'syncNotificationPermissionStatus() has not been implemented.',
    );
  }
}
