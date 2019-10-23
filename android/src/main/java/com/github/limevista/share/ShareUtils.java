package com.github.limevista.share;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import java.io.File;

@SuppressWarnings("WeakerAccess")
public class ShareUtils {

    static final String TYPE_FILE = "file";
    static final String TYPE_IMAGE = "image";
    static final String TYPE_VIDEO = "video";
    static final String TYPE_AUDIO = "audio";
    static final String TYPE_EMAIL = "email";
    static final String TYPE_TEXT = "text";

    public static Uri getUriForFile(@NonNull Context context, @NonNull File file, @Nullable String type) {
        String authorities = context.getPackageName() + ".LimeVista.FileProvider";
        Uri uri;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            uri = Uri.fromFile(file);
        } else {
            uri = FileProvider.getUriForFile(context, authorities, file);
            if (!inExternalStorage(file) || !isMedia(type)) return uri;
            final ContentResolver resolver = context.getContentResolver();
            final String fileType = resolver.getType(uri);
            if (fileType == null || fileType.length() == 0) return uri;
            if (!TextUtils.isEmpty(uri.toString())) {
                if (fileType.contains("video/")) {
                    uri = getVideoContentUriFromFile(context, file);
                } else if (fileType.contains("image/")) {
                    uri = getImageContentUriFromFile(context, file);
                } else if (fileType.contains("audio/")) {
                    uri = getAudioContentUriFromFile(context, file);
                }
            }
        }
        return uri;
    }

    public static boolean inExternalStorage(File file) {
        return file.getAbsolutePath()
                .startsWith(Environment.getExternalStorageDirectory().getAbsolutePath());
    }

    static boolean isFile(String type) {
        if (type == null) return false;
        switch (type) {
            case TYPE_AUDIO:
            case TYPE_FILE:
            case TYPE_IMAGE:
            case TYPE_VIDEO:
                return true;
            default:
                return false;
        }
    }

    @Nullable
    private static Uri getImageContentUriFromFile(Context context, File imageFile) {
        return getContentUri(
                context,
                imageFile,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
    }

    @Nullable
    private static Uri getVideoContentUriFromFile(Context context, File videoFile) {
        return getContentUri(
                context,
                videoFile,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        );
    }

    @Nullable
    private static Uri getAudioContentUriFromFile(Context context, File audioFile) {
        return getContentUri(
                context,
                audioFile,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        );
    }

    @Nullable
    @SuppressWarnings("SameParameterValue")
    private static Uri getContentUri(Context context, File mediaFile, String tagData,
                                     String tagId, Uri externalUri) {
        String filePath = mediaFile.getAbsolutePath();
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{tagId},
                tagData + "=? ",
                new String[]{filePath},
                null
        )) {
            if (cursor != null && cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
                return Uri.withAppendedPath(externalUri, String.valueOf(id));
            } else {
                if (mediaFile.exists()) {
                    ContentValues values = new ContentValues();
                    values.put(tagData, filePath);
                    return context.getContentResolver().insert(externalUri, values);
                } else {
                    return null;
                }
            }
        }
    }

    private static boolean isMedia(String type) {
        return type != null && (type.equals(TYPE_IMAGE) || type.equals(TYPE_VIDEO) || type.equals(TYPE_AUDIO));
    }
}
