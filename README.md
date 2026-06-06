
# راهنمای نصب Fetchy SDK Flutter

## 1. افزودن دسترسی نوتیفیکیشن در Android

فایل زیر را باز کنید:

```txt
android/app/src/main/AndroidManifest.xml
````

سپس این permission را داخل تگ اصلی `manifest` اضافه کنید:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS"/>
```

---

## 2. دانلود فایل تنظیمات SDK

از پنل سایت، فایل JSON مربوط به اپلیکیشن خود را دانلود کنید.

سپس نام فایل را به شکل زیر تغییر دهید:

```txt
fetchy-config.json
```

و آن را در مسیر زیر قرار دهید:

```txt
android/app/src/main/assets
```

> اگر پوشه `assets` وجود ندارد، آن را بسازید.

---

## 3. افزودن پکیج به `pubspec.yaml`

در فایل `pubspec.yaml`، بخش `dependencies` را به شکل زیر تکمیل کنید:

```yaml
dependencies:
  fetchy_sdk_flutter:
    git:
      url: https://github.com/spellads-ir/fetchy_sdk_flutter.git
      ref: main
```

سپس دستور زیر را اجرا کنید:

```bash
flutter pub get
```

---

## 4. مقداردهی اولیه SDK

در اولین صفحه یا کلاس اصلی اپلیکیشن، متد `initialize` را داخل `initState` فراخوانی کنید:

```dart
@override
void initState() {
  super.initState();
  FetchySdkFlutter().initialize();
}
```

---

## نصب کامل شد

بعد از انجام مراحل بالا، Fetchy SDK روی پروژه Flutter شما فعال خواهد شد.

```
