# Fetchy Flutter SDK

Fetchy Flutter is a pull-only wrapper around the embedded Fetchy Android SDK.

The current surface focuses on:
- initialization
- Android notification permission status
- backend-driven pull notifications

## Usage

```dart
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter.dart';

final sdk = FetchySdkFlutter();

await sdk.initialize();

final permission = await sdk.getNotificationPermissionStatus();
final syncedPermission = await sdk.syncNotificationPermissionStatus();
```

## Android Asset Config

Place `fetchy-config.json` in your Android app assets:

```json
{
  "environment": "production",
  "base_url": "https://your-api.example.com",
  "api_key": "your_fetchy_app_api_key",
  "pull": {
    "enabled": true,
    "worker_enabled": true,
    "poll_interval_minutes": 15
  },
  "notification": {
    "channel_id": "pn_notification_channel",
    "channel_name": "Fetchy Notifications",
    "channel_description": "Notifications delivered by the Fetchy SDK."
  }
}
```

## Pull-Only Contract

- No third-party push dependency is needed.
- No push token update API exists.
- No push-message parsing or forwarding API exists.
- Notifications are handled only through backend polling.
- Direct click ack is sent only for internal app links opened by the SDK. External web links are opened normally without client-side ack.