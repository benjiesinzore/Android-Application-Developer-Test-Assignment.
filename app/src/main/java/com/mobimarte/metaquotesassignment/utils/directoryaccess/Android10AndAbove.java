package com.mobimarte.metaquotesassignment.utils.directoryaccess;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.io.File;

public class Android10AndAbove {

    Context context;

    public static Android10AndAbove getInstance() {
        return new Android10AndAbove();
    }

    public Android10AndAbove with(Context context) {
        this.context = context;
        return this;
    }

    //ANDROID 10 AND ABOVE





    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Uri createLogs(@NonNull Context context, String directory, String filename, String mimetype) {
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.MediaColumns.MIME_TYPE, mimetype);

        contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS +
                File.separator + directory); // Specify the Downloads directory for Android 10+

        return contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
    }


    @RequiresApi(api = Build.VERSION_CODES.Q)
    public Uri getLogFileUri(Context context, String filename) {
        ContentResolver contentResolver = context.getContentResolver();
        Uri contentUri = MediaStore.Downloads.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.MediaColumns.DISPLAY_NAME + "=?";
        String[] selectionArgs = new String[]{filename};

        try (Cursor cursor = contentResolver.query(contentUri, null, selection, selectionArgs, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                int idColumn = cursor.getColumnIndex(MediaStore.MediaColumns._ID);
                long fileId = cursor.getLong(idColumn);
                return ContentUris.withAppendedId(contentUri, fileId);
            }
        }

        return null; // Return null if the log file doesn't exist
    }



}
