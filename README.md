# راهنمای استفاده از Fetchy SDK Flutter

[![](https://jitpack.io/v/spellads-ir/fetchy_sdk.svg)](https://jitpack.io/#spellads-ir/fetchy_sdk)

با این پکیج می‌توانید **Fetchy** را داخل اپ Flutter اندرویدی‌تان فعال کنید.

فعلاً فقط **اندروید** پشتیبانی می‌شود.

اگر دوست دارید اول ببینید همه‌چیز چطور کار می‌کند، پوشهٔ [`example/`](example/) را باز کنید و اپ نمونه را اجرا کنید.

---

## قبل از شروع

این‌ها را داشته باشید:

- Flutter نصب‌شده روی سیستم
- یک پروژه Flutter اندروید
- اکانت در [پنل Fetchy](https://fetchy.notifhub.com) و فایل تنظیمات اپ

---

## مراحل نصب (به ترتیب)

### مرحله ۱ — اضافه کردن پکیج‌ها

فایل `pubspec.yaml` پروژه‌تان را باز کنید و این دو پکیج را اضافه کنید:

```yaml
dependencies:
  fetchy_sdk_flutter:
    git:
      url: https://github.com/spellads-ir/fetchy_sdk_flutter.git
      ref: main
  permission_handler: ^12.0.0
```

بعد در ترمینال بزنید:

```bash
flutter pub get
```

`permission_handler` برای گرفتن اجازهٔ نوتیفیکیشن از کاربر لازم است (در اندروید ۱۳ به بالا اجباری است).

---

### مرحله ۲ — تنظیم Gradle اندروید

این مرحله فقط یک‌بار لازم است. فایل زیر را باز کنید:

```txt
android/settings.gradle.kts
```

**قدم اول:** داخل بلوک `plugins` این خط را اضافه کنید:

```kotlin
id("com.google.devtools.ksp") version "2.3.7" apply false
```

**قدم دوم:** در انتهای فایل، بعد از `include(":app")`، این کد را کپی کنید:

```kotlin
// برای کار کردن fetchy_sdk_flutter لازم است. قبلش `flutter pub get` بزنید.
run {
    val flutterRoot = settingsDir.parentFile ?: return@run
    val depsFile = java.io.File(flutterRoot, ".flutter-plugins-dependencies")
    if (!depsFile.exists()) return@run

    @Suppress("UNCHECKED_CAST")
    val meta = groovy.json.JsonSlurper().parseText(depsFile.readText()) as Map<String, Any>
    val plugins = meta["plugins"] as? Map<String, Any> ?: return@run
    val androidPlugins = plugins["android"] as? List<*> ?: return@run

    for (entry in androidPlugins) {
        val plugin = entry as? Map<*, *> ?: continue
        if (plugin["name"] == "fetchy_sdk_flutter") {
            val path = plugin["path"] as? String ?: continue
            apply(from = java.io.File(path, "android/include_fetchy_sdk.settings.gradle.kts"))
            break
        }
    }
}
```

این کد را لازم نیست بفهمید؛ فقط کپی کنید. کارش این است که بخش اندرویدی Fetchy را به پروژه وصل کند.

> اگر build خطا داد، اول `flutter pub get` بزنید و دوباره build کنید.

---

### مرحله ۳ — اجازه‌ها در AndroidManifest

فایل زیر را باز کنید:

```txt
android/app/src/main/AndroidManifest.xml
```

داخل تگ `<manifest>` (نه داخل `<application>`) این دو خط را بگذارید:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

- `INTERNET` → برای ارتباط با سرور Fetchy
- `POST_NOTIFICATIONS` → برای ارسال نوتیفیکیشن

---

### مرحله ۴ — گذاشتن فایل تنظیمات

1. برو به [پنل Fetchy](https://fetchy.notifhub.com) و اپت را ثبت کن.
2. فایل JSON تنظیمات را دانلود کن.
3. اسم فایل را عوض کن به: `fetchy-config.json`
4. فایل را اینجا بگذار:

```txt
android/app/src/main/assets/fetchy-config.json
```

اگر پوشهٔ `assets` نبود، خودت بسازش.

نمونهٔ فایل: [`example/fetchy-config.sample.json`](example/fetchy-config.sample.json)

> این فایل کلید API دارد. آن را داخل Git commit نکن.

---

### مرحله ۵ — کد Dart

#### ۵.۱ — گرفتن اجازهٔ نوتیفیکیشن

قبل از راه‌اندازی SDK، از کاربر اجازه بگیر:

```dart
import 'package:permission_handler/permission_handler.dart';

Future<void> requestNotificationPermission() async {
  final status = await Permission.notification.status;
  if (status.isDenied) {
    await Permission.notification.request();
  } else if (status.isPermanentlyDenied) {
    // کاربر قبلاً «هرگز» زده — بفرستش تنظیمات گوشی
    await openAppSettings();
  }
}
```

#### ۵.۲ — راه‌اندازی Fetchy

```dart
import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter.dart';

final fetchy = FetchySdkFlutter();

Future<void> startFetchy() async {
  // اول اجازه
  await requestNotificationPermission();

  // بعد SDK
  await fetchy.initialize(
    onToken: (token) {
      // توکن دستگاه — ممکن است چند ثانیه طول بکشد
      print('توکن Fetchy: $token');
    },
  );
}
```

`initialize` زود برمی‌گردد. ثبت دستگاه در پس‌زمینه انجام می‌شود. اگر `onToken` بدهید، تا حدود ۳۰ ثانیه صبر می‌کند تا توکن آماده شود.

#### ۵.۳ — مثال داخل یک صفحه

```dart
@override
void initState() {
  super.initState();
  _initializeSdk();
}

Future<void> _initializeSdk() async {
  await requestNotificationPermission();

  await FetchySdkFlutter().initialize(
    onToken: (token) {
      if (!mounted) return;
      setState(() => _deviceToken = token);
    },
  );
}
```

---

## متدهای دیگر (اختیاری)

| متد | کاربرد |
|-----|--------|
| `getToken()` | خواندن توکن الان (اگر هنوز نیامده `null` برمی‌گردد) |
| `getNotificationPermissionStatus()` | ببین کاربر اجازهٔ نوتیف داده یا نه |
| `syncNotificationPermissionStatus()` | وضعیت اجازه را با سرور Fetchy همگام کن |

وضعیت اجازه یکی از این‌هاست: `granted` (مجاز)، `denied` (رد شده)، `unknown` (نامشخص)

---

## اجرای اپ نمونه

```bash
cd example
flutter pub get
flutter run
```

قبلش `fetchy-config.json` واقعی را در `android/app/src/main/assets/` بگذار.

---

## اگر گیر کردی

| مشکل | راه‌حل |
|------|--------|
| خطای `:fetchy-sdk` در build | `flutter pub get` بزن، بعد دوباره build کن |
| توکن نمی‌آید | چند ثانیه صبر کن؛ اجازهٔ نوتیف و `fetchy-config.json` را چک کن |
| چند flavor داری | مسیر `AndroidManifest` و `assets` را با flavor خودت تطبیق بده |

---

تمام! اگر همه مراحل را انجام دادی، Fetchy داخل اپ Flutter شما فعال است.
