package com.fetchy.fetchy_sdk_flutter;

import android.content.Context;
import androidx.annotation.NonNull;
import com.fetchy.sdk.Fetchy;
import com.fetchy.sdk.FetchyClientType;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import java.util.Locale;

public final class FetchySdkFlutterPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler {
    private MethodChannel channel;
    private Context applicationContext;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        applicationContext = binding.getApplicationContext();
        channel = new MethodChannel(binding.getBinaryMessenger(), "fetchy_sdk_flutter");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
        switch (call.method) {
            case "initialize":
                try {
                    Fetchy.initialize(applicationContext, FetchyClientType.FLUTTER_ANDROID);
                    result.success(null);
                } catch (Exception error) {
                    result.error("initialize_failed", error.getMessage(), null);
                }
                break;
            case "getToken":
                result.success(Fetchy.getToken(applicationContext));
                break;
            case "getNotificationPermissionStatus":
                result.success(
                    Fetchy.getNotificationPermissionStatus(applicationContext)
                        .name()
                        .toLowerCase(Locale.ROOT)
                );
                break;
            case "syncNotificationPermissionStatus":
                try {
                    result.success(
                        Fetchy.syncNotificationPermissionStatus(applicationContext)
                            .name()
                            .toLowerCase(Locale.ROOT)
                    );
                } catch (Exception error) {
                    result.error("permission_sync_failed", error.getMessage(), null);
                }
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        if (channel != null) {
            channel.setMethodCallHandler(null);
            channel = null;
        }
    }
}