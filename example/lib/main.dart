import 'package:fetchy_sdk_flutter/fetchy_sdk_flutter.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const FetchyExampleApp());
}

class FetchyExampleApp extends StatelessWidget {
  const FetchyExampleApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'نمونه Fetchy SDK',
      builder: (context, child) {
        return Directionality(
          textDirection: TextDirection.rtl,
          child: child!,
        );
      },
      theme: ThemeData(
        colorScheme: ColorScheme.fromSeed(seedColor: Colors.indigo),
        useMaterial3: true,
      ),
      home: const FetchyExamplePage(),
    );
  }
}

class FetchyExamplePage extends StatefulWidget {
  const FetchyExamplePage({super.key});

  @override
  State<FetchyExamplePage> createState() => _FetchyExamplePageState();
}

class _FetchyExamplePageState extends State<FetchyExamplePage> {
  final _fetchy = FetchySdkFlutter();

  String _initStatus = 'مقداردهی نشده';
  String _permissionStatus = 'نامشخص';
  String _tokenStatus = 'دریافت نشده';
  String? _lastError;
  bool _isBusy = false;

  @override
  void initState() {
    super.initState();
    _initializeSdk();
  }

  Future<void> _requestNotificationPermission() async {
    final status = await Permission.notification.status;
    if (status.isDenied) {
      await Permission.notification.request();
    } else if (status.isPermanentlyDenied) {
      await openAppSettings();
    }
  }

  Future<void> _initializeSdk() async {
    setState(() {
      _isBusy = true;
      _lastError = null;
      _initStatus = 'در حال مقداردهی...';
    });

    try {
      await _requestNotificationPermission();
      await _fetchy.initialize(
        onToken: (token) {
          if (!mounted) return;
          setState(() => _tokenStatus = token);
        },
      );
      final status = await _fetchy.getNotificationPermissionStatus();
      if (!mounted) return;
      setState(() {
        _initStatus = 'مقداردهی شد';
        _permissionStatus = _formatStatus(status);
        _tokenStatus = 'در انتظار دریافت...';
      });
    } catch (error) {
      if (!mounted) return;
      setState(() {
        _initStatus = 'ناموفق';
        _lastError = error.toString();
      });
    } finally {
      if (mounted) {
        setState(() => _isBusy = false);
      }
    }
  }

  Future<void> _refreshPermissionStatus() async {
    setState(() {
      _isBusy = true;
      _lastError = null;
    });

    try {
      final status = await _fetchy.getNotificationPermissionStatus();
      if (!mounted) return;
      setState(() => _permissionStatus = _formatStatus(status));
    } catch (error) {
      if (!mounted) return;
      setState(() => _lastError = error.toString());
    } finally {
      if (mounted) {
        setState(() => _isBusy = false);
      }
    }
  }

  Future<void> _syncPermissionStatus() async {
    setState(() {
      _isBusy = true;
      _lastError = null;
    });

    try {
      final status = await _fetchy.syncNotificationPermissionStatus();
      if (!mounted) return;
      setState(() => _permissionStatus = _formatStatus(status));
    } catch (error) {
      if (!mounted) return;
      setState(() => _lastError = error.toString());
    } finally {
      if (mounted) {
        setState(() => _isBusy = false);
      }
    }
  }

  String _formatStatus(FetchyNotificationPermissionStatus status) {
    return switch (status) {
      FetchyNotificationPermissionStatus.granted => 'مجاز',
      FetchyNotificationPermissionStatus.denied => 'رد شده',
      FetchyNotificationPermissionStatus.unknown => 'نامشخص',
    };
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('نمونه Fetchy SDK'),
      ),
      body: ListView(
        padding: const EdgeInsets.all(24),
        children: [
          const Text(
            'این نمونه نحوه مقداردهی اولیه Fetchy SDK و بررسی وضعیت '
            'دسترسی نوتیفیکیشن در اندروید را نشان می‌دهد.',
            style: TextStyle(fontSize: 16),
            textAlign: TextAlign.justify,
          ),
          const SizedBox(height: 24),
          _StatusCard(
            title: 'وضعیت SDK',
            value: _initStatus,
          ),
          const SizedBox(height: 12),
          _StatusCard(
            title: 'دسترسی نوتیفیکیشن',
            value: _permissionStatus,
          ),
          const SizedBox(height: 12),
          _TokenCard(token: _tokenStatus),
          if (_lastError != null) ...[
            const SizedBox(height: 12),
            Card(
              color: Theme.of(context).colorScheme.errorContainer,
              child: Padding(
                padding: const EdgeInsets.all(16),
                child: Text(
                  _lastError!,
                  style: TextStyle(
                    color: Theme.of(context).colorScheme.onErrorContainer,
                  ),
                ),
              ),
            ),
          ],
          const SizedBox(height: 24),
          FilledButton(
            onPressed: _isBusy ? null : _refreshPermissionStatus,
            child: const Text('بروزرسانی وضعیت دسترسی'),
          ),
          const SizedBox(height: 12),
          FilledButton.tonal(
            onPressed: _isBusy
                ? null
                : () async {
                    await _requestNotificationPermission();
                    await _refreshPermissionStatus();
                  },
            child: const Text('درخواست دسترسی نوتیفیکیشن'),
          ),
          const SizedBox(height: 12),
          OutlinedButton(
            onPressed: _isBusy ? null : _syncPermissionStatus,
            child: const Text('همگام‌سازی وضعیت دسترسی'),
          ),
          const SizedBox(height: 12),
          TextButton(
            onPressed: _isBusy ? null : _initializeSdk,
            child: const Text('مقداردهی مجدد SDK'),
          ),
          if (_isBusy) ...[
            const SizedBox(height: 24),
            const Center(child: CircularProgressIndicator()),
          ],
        ],
      ),
    );
  }
}

class _TokenCard extends StatelessWidget {
  const _TokenCard({required this.token});

  final String token;

  bool get _isRealToken =>
      token != 'دریافت نشده' && token != 'در انتظار دریافت...';

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Expanded(
                  child: Text(
                    'توکن دستگاه',
                    style: Theme.of(context).textTheme.titleMedium,
                  ),
                ),
                if (_isRealToken)
                  IconButton(
                    icon: const Icon(Icons.copy, size: 18),
                    tooltip: 'کپی توکن',
                    onPressed: () {
                      Clipboard.setData(ClipboardData(text: token));
                      ScaffoldMessenger.of(context).showSnackBar(
                        const SnackBar(content: Text('توکن کپی شد')),
                      );
                    },
                  ),
              ],
            ),
            const SizedBox(height: 8),
            Text(
              token,
              style: Theme.of(context).textTheme.bodySmall?.copyWith(
                    fontFamily: 'monospace',
                    color: _isRealToken
                        ? Theme.of(context).colorScheme.primary
                        : Theme.of(context).colorScheme.onSurfaceVariant,
                  ),
            ),
          ],
        ),
      ),
    );
  }
}

class _StatusCard extends StatelessWidget {
  const _StatusCard({
    required this.title,
    required this.value,
  });

  final String title;
  final String value;

  @override
  Widget build(BuildContext context) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            Expanded(
              child: Text(
                title,
                style: Theme.of(context).textTheme.titleMedium,
              ),
            ),
            Text(
              value,
              style: Theme.of(context).textTheme.bodyLarge?.copyWith(
                    fontWeight: FontWeight.w600,
                  ),
            ),
          ],
        ),
      ),
    );
  }
}
