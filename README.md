# راهنمای استفاده از Fetchy SDK Flutter

این پکیج یک Flutter plugin است. برای استفاده از آن باید SDK را در پروژه Flutter میزبان اضافه و تنظیم کنید.

## 1. افزودن دسترسی نوتیفیکیشن در Android

در پروژه Flutter اصلی، فایل AndroidManifest مربوط به اپ را باز کنید. در بیشتر پروژه‌ها این فایل در مسیر زیر است:

```txt
android/app/src/main/AndroidManifest.xml
```

سپس permission زیر را داخل تگ اصلی `manifest` قرار دهید:

```xml
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
```

## 2. قرار دادن فایل تنظیمات SDK

از پنل سایت، فایل JSON مربوط به اپلیکیشن خود را دانلود کنید و نام آن را به شکل زیر تغییر دهید:

```txt
fetchy-config.json
```

سپس فایل را در مسیر زیر در پروژه Flutter میزبان قرار دهید:

```txt
android/app/src/main/assets
```

اگر پوشه `assets` وجود ندارد، آن را بسازید.

## 3. افزودن پکیج به `pubspec.yaml`

در فایل `pubspec.yaml`، بخش `dependencies` را به شکل زیر تکمیل کنید:

```yaml
dependencies:
  fetchy_sdk_flutter:
    git:
      url: https://github.com/spellads-ir/fetchy_sdk_flutter.git
      ref: main
```

سپس این دستور را اجرا کنید:

```bash
flutter pub get
```

## 4. مقداردهی اولیه SDK

در اولین صفحه یا کلاس اصلی اپلیکیشن، متد `initialize` را در `initState` فراخوانی کنید:

```dart
@override
void initState() {
  super.initState();
  FetchySdkFlutter().initialize();
}
```

## نکته

اگر در پروژه خود از چند flavor یا ماژول Android استفاده می‌کنید، مسیر فایل manifest و پوشه assets را با ساختار همان پروژه تطبیق دهید.

## نصب کامل شد

بعد از انجام مراحل بالا، Fetchy SDK در پروژه Flutter شما فعال خواهد شد.
