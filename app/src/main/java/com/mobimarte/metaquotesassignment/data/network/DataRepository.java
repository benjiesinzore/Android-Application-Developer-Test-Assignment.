package com.mobimarte.metaquotesassignment.data.network;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.mobimarte.metaquotesassignment.R;
import com.mobimarte.metaquotesassignment.data.logic.DataLogic;
import com.mobimarte.metaquotesassignment.utils.directoryaccess.Android10AndAbove;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.ResponseBody;


@RequiresApi(api = Build.VERSION_CODES.N)
public class DataRepository {

//    private final ApiService apiService;

    public DataRepository() {
    }


    public void downloadFileFromUrl(Context context, String url, Function<String, Void> callback) {


        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            Request request = new Request.Builder()
                    .url(url)
                    .method("GET", null)
                    .build();

            try {
                okhttp3.Response response = client.newCall(request).execute();
                if (response.isSuccessful()) {
                    // Save the downloaded file to the device's storage
                    saveFileToDeviceStorage(context, response.body());

                    callback.apply(context.getString(R.string.res_success));
                }

                response.close();
            } catch (IOException ee) {

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
                callback.apply(ee.getMessage());
            }
        });
    }


    private Uri textFileUri;


    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri getOrCreateTextFile(@NonNull Context context, String path) {

        String filename = context.getString(R.string.file_name);
        String mimetype = context.getString(R.string.file_type);

        // Check if the log file exists
        Uri existingTextFileUri = Android10AndAbove.getInstance()
                .with(context)
                .getLogFileUri(context, filename);

        if (existingTextFileUri != null) {
            // Log file exists, return its Uri
            return existingTextFileUri;
        } else {
            // Log file does not exist, create a new one and return its Uri
            return Android10AndAbove.getInstance()
                    .with(context)
                    .createLogs(context, path, filename, mimetype);
        }
    }


    private void saveFileToDeviceStorage(Context context, ResponseBody body) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            if (textFileUri == null) {
                // The log file doesn't exist, create a new one and store its Uri
                String path = context.getString(R.string.app_name);
                textFileUri = getOrCreateTextFile(context, path);
            }

            try {

                InputStream inputStream = body.byteStream();

                StringBuilder sb = new StringBuilder();
                byte[] buffer = new byte[4096];
                int bytesRead;
                List<String> list = new ArrayList<>();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String line = new String(buffer, 0, bytesRead);
                    sb.append(line);
                    list.add(line);

                }


                OutputStream outputStream = context.getContentResolver().openOutputStream(textFileUri, "wa");
                for (String cont : list){

                    // Write the content to the file
                    outputStream.write(cont.getBytes());

                }

                outputStream.close();
            } catch (IOException ee) {

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }
        }

        else {

            File rootFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));
            boolean directoriesCreated = true;
            if (!rootFolder.exists()) {
                directoriesCreated = rootFolder.mkdirs();
            }

            if (!directoriesCreated) {
                try {
                    throw new IOException("Failed to create directories: " + rootFolder.getAbsolutePath());
                } catch (IOException ee) {

                    new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
                }
            }

            File file = new File(rootFolder, context.getString(R.string.file_name));

            try {
                InputStream inputStream = body.byteStream();

                byte[] buffer = new byte[4096];
                int bytesRead;
                List<String> list = new ArrayList<>();
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    String line = new String(buffer, 0, bytesRead);
                    list.add(line);

                }

                FileOutputStream fos = new FileOutputStream(file);
                for (String cont : list){

                    // Write the content to the file
                    fos.write(cont.getBytes());

                }

                fos.close();
                inputStream.close();


            } catch (Exception ee) {

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }

        }

    }


}
