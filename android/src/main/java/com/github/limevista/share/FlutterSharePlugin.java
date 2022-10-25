package com.github.limevista.share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/**
 * FlutterSharePlugin
 */
public class FlutterSharePlugin implements MethodCallHandler, FlutterPlugin,
        PluginRegistry.RequestPermissionsResultListener, ActivityAware {

    private static final int CODE_ASK_OK = 100;

    @Nullable
    private String mText;

    @Nullable
    private String mType;

    @Nullable
    private String mData;

    @Nullable
    private Activity mActivity;

    @Override
    public void onMethodCall(MethodCall call, @NonNull Result result) {
        if (call.method.equals("share")) {
            mText = call.argument("text");
            mType = call.argument("type");
            mData = call.argument("data");
            share();
            result.success(null);
        } else {
            result.notImplemented();
        }
    }

    private void share() {
        final Activity activity = mActivity;
        if (activity == null) return;
        final String type = mType;
        if (type == null) throw new IllegalArgumentException("Type must be not mull.");
        if (ShareUtils.isFile(mType) && mData != null) {
            if (shouldRequestPermission(new File(mData)) && !checkPermission(activity)) {
                requestPermission(activity);
                return;
            }
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        final String text = mText;
        switch (type) {
            case ShareUtils.TYPE_TEXT:
                shareIntent.setType("text/plain");
                break;

            case ShareUtils.TYPE_IMAGE:
                File imageFile = new File(mData);
                shareIntent.setType("image/*");
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(activity, imageFile, mType)
                );
                break;

            case ShareUtils.TYPE_VIDEO:
                File videoFile = new File(mData);
                shareIntent.setType("video/*");
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(activity, videoFile, mType)
                );
                break;

            case ShareUtils.TYPE_AUDIO:
                File audioFile = new File(mData);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(activity, audioFile, mType)
                );
                break;

            case ShareUtils.TYPE_FILE:
                File file = new File(mData);
                shareIntent.setType("application/*");
                shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(activity, file, mType)
                );
                break;

            case ShareUtils.TYPE_EMAIL:
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, mData);
                break;

            default:
                return;
        }
        if (text != null) {
            shareIntent.putExtra(Intent.EXTRA_TEXT, text);
        }
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Intent chooser = Intent.createChooser(shareIntent, null);
        activity.startActivity(chooser);
    }

    @Override
    public boolean onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] perms,
            @NonNull int[] results
    ) {
        if (requestCode == CODE_ASK_OK &&
                results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {
            share();
        }
        return false;
    }

    private boolean checkPermission(@NonNull Activity activity) {
        return ContextCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission(@NonNull Activity activity) {
        ActivityCompat.requestPermissions(
                activity,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE_ASK_OK
        );
    }

    private static boolean shouldRequestPermission(@NonNull File file) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ShareUtils.inExternalStorage(file);
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        final MethodChannel channel = new MethodChannel(binding.getBinaryMessenger(), "LimeVista:Share");
        channel.setMethodCallHandler(this);
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        mActivity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

    }

    @Override
    public void onDetachedFromActivity() {
        mActivity = null;
    }
}
