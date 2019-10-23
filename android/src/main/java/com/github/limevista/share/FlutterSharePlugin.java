package com.github.limevista.share;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterSharePlugin
 */
public class FlutterSharePlugin implements MethodCallHandler, PluginRegistry.RequestPermissionsResultListener {

    private static final int CODE_ASK_OK = 100;

    private final Registrar mRegistrar;
    private String mText;
    private String mType;
    private String mData;

    private FlutterSharePlugin(Registrar registrar) {
        mRegistrar = registrar;
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "LimeVista:Share");
        channel.setMethodCallHandler(new FlutterSharePlugin(registrar));
    }

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
        final String type = mType;
        if (type == null) throw new IllegalArgumentException("Type must be not mull.");
        if (ShareUtils.isFile(mType) && mData != null) {
            if (shouldRequestPermission(new File(mData)) && !checkPermission()) {
                requestPermission();
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
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(mRegistrar.context(), imageFile, mType));
                break;

            case ShareUtils.TYPE_VIDEO:
                File videoFile = new File(mData);
                shareIntent.setType("video/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(mRegistrar.context(), videoFile, mType));
                break;

            case ShareUtils.TYPE_AUDIO:
                File audioFile = new File(mData);
                shareIntent.setType("audio/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(mRegistrar.context(), audioFile, mType));
                break;

            case ShareUtils.TYPE_FILE:
                File file = new File(mData);
                shareIntent.setType("application/*");
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        ShareUtils.getUriForFile(mRegistrar.context(), file, mType));
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
        final Activity activity = mRegistrar.activity();
        if (activity != null) {
            activity.startActivity(chooser);
        } else {
            chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mRegistrar.context().startActivity(chooser);
        }
    }

    @Override
    public boolean onRequestPermissionsResult(int requestCode, String[] perms, int[] results) {
        if (requestCode == CODE_ASK_OK &&
                results.length > 0 &&
                results[0] == PackageManager.PERMISSION_GRANTED) {
            share();
        }
        return false;
    }

    private boolean checkPermission() {
        return ContextCompat.checkSelfPermission(mRegistrar.context(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(
                mRegistrar.activity(),
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                CODE_ASK_OK
        );
    }

    private static boolean shouldRequestPermission(@NonNull File file) {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ShareUtils.inExternalStorage(file);
    }
}
