## 1.3.8

- Fix Android plugin packaging so the Java plugin entrypoint is discoverable and consumable from Flutter app builds.

## 1.3.7

- Update the embedded Fetchy Android SDK to v1.3.8.
- Remove queued delivery and permission report plumbing and keep direct click ack only for internal app links.
- Remove SDK log persistence and clean the sample apps that previously read `pn_worker_logs`.
- Replace the placeholder package license with Apache-2.0.

## 1.3.6

- Add a Java-visible Android plugin entrypoint so Flutter apps can compile `GeneratedPluginRegistrant` reliably on Android.

## 1.3.5

- Update the embedded Fetchy Android SDK to v1.3.7.
- Preserve Flutter client type during runtime bootstrap and use a local bell resource as the default notification small icon.

## 1.3.4

- Update the embedded Fetchy Android SDK to v1.3.6.
- Fall back to `mavenLocal()` when the embedded Android SDK project is not included by the Flutter plugin loader.

## 1.3.3

- Update embedded Fetchy Android SDK to v1.3.3.
- Includes fix to prevent duplicate near-simultaneous `/feed` calls.

## 1.3.2

- Remove example app to reduce package size.
- Production documentation cleanup.

## 1.3.1

- Pass `FetchyClientType.FLUTTER_ANDROID` on initialization for accurate client identification.

## 1.3.0

- Initial stable release with pull-only notification support.
- Wraps Fetchy Android SDK via method channel.
- Exposes `initialize`, `getNotificationPermissionStatus`, and `syncNotificationPermissionStatus`.
