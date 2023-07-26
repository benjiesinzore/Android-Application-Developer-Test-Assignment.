package com.mobimarte.metaquotesassignment.data.logic;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.mobimarte.metaquotesassignment.R;
import com.mobimarte.metaquotesassignment.data.datamodel.DataModel;
import com.mobimarte.metaquotesassignment.utils.directoryaccess.Android10AndAbove;
import com.mobimarte.metaquotesassignment.data.network.DataRepository;
import com.mobimarte.metaquotesassignment.data.viewmodel.DataViewModel;
import com.mobimarte.metaquotesassignment.data.viewmodel.LiveDataViewModel;
import com.mobimarte.metaquotesassignment.utils.MyPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RequiresApi(api = Build.VERSION_CODES.N)
public class DataLogic {

    DataRepository dataRepository;
    private Uri logFileUri;

    public DataLogic() {
        dataRepository = new DataRepository();
    }


    public void getDataToDisplayOnUI(Context context, String endpoint
    ){
        // Get the ViewModel.
        LiveDataViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(LiveDataViewModel.class);

        dataRepository.downloadFileFromUrl(context, endpoint, res -> {

            viewModel.getDownloadStatus().postValue(res);


            return null;
        });

    }

    public void getFileFromFolderAndAddContentsToList(Context context){

        List<DataModel> res;

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<DataModel>> results = executorService.submit(() -> {

            List<DataModel> list = new ArrayList<>();

            FileReader fileReader;


            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

                String filename = context.getString(R.string.file_name);
                Uri existingTextFileUri = Android10AndAbove.getInstance()
                        .with(context)
                        .getLogFileUri(context, filename);


                // Check if the log file already exists
                if (existingTextFileUri != null) {
                    try (InputStream inputStream = context.getContentResolver().openInputStream(existingTextFileUri);
                         BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {

                            DataModel model = new DataModel();
                            model.setTextLine(line);
                            sb.append(line).append("\n"); // Add a new line after each line read from the file

                            list.add(model);
                        }


                    } catch (IOException ee) {

                        new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
                    }
                } else {
                    // The log file does not exist, so you can handle it accordingly (e.g., show a message)
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(context, R.string.the_log_file_does_not_exist, Toast.LENGTH_LONG).show());
                }


            }

            else {

                File rootFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));

                File file = new File(rootFolder, context.getString(R.string.file_name));

                try {
                    fileReader = new FileReader(file);
                    BufferedReader reader = new BufferedReader(fileReader);

                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {

                        DataModel model = new DataModel();
                        model.setTextLine(line);
                        sb.append(line).append("\n"); // Add a new line after each line read from the file

                        list.add(model);
                    }


                } catch (IOException ee){

                    new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
                }
            }

            return list;
        });

        try {

            MyPreferences pref = new MyPreferences(context);
            res = results.get();

            filetDataToDisplayOnList(context, pref.getStringFilter(),res);

        } catch (Exception ee){

            new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
        }
    }

    public void filetDataToDisplayOnList(Context context, String filterString, @NonNull List<DataModel> list) {


        if (!Objects.equals(filterString, "")){

            List<DataModel> res = new ArrayList<>();
            Set<String> uniqueLines = new HashSet<>();

            for (DataModel mod : list) {
                String line = mod.getTextLine();
                String[] split = line.split("\\s|,|\\.|\\?");

                for (String word : split) {

                    if (word.equals(filterString)) {

                        uniqueLines.add(line);
                        break;
                    } else if (word.startsWith(filterString)) {

                        uniqueLines.add(line);
                        break;
                    }
                }

                if (line.equals(filterString)) {
                    uniqueLines.add(line);
                }
            }

            for (String line : uniqueLines) {
                DataModel mod = new DataModel();
                mod.setTextLine(line);
                res.add(mod);

            }

            addDataToSQLLiteDatabase(context, res);
        }

        else {

            addDataToSQLLiteDatabase(context, list);

        }



    }

    private void writeDataToResultsFile(@NonNull Context context, List<DataModel> res) {



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (logFileUri == null) {
                // The log file doesn't exist, create a new one and store its Uri
                String path = context.getString(R.string.app_name);
                String filename = context.getString(R.string.file_name_res);
                logFileUri = getOrCreateLogFile(context, path, filename);
            }


            // Use StringBuilder to build the content
            StringBuilder stringBuilder = new StringBuilder();
            for (DataModel model : res) {
                String line = model.getTextLine();
                stringBuilder.append(line).append("\n");
            }

            try (OutputStream fos = context.getContentResolver().openOutputStream(logFileUri, "wa")) {
                // Write the content to the file
                fos.write(stringBuilder.toString().getBytes());
                fos.write("\n\n".getBytes()); // Add a new line after each string

            } catch (IOException ee) {

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }

        }

        else {

            File rootFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));

            boolean directoriesCreated = true;
            if (!rootFolder.exists()){
                directoriesCreated = rootFolder.mkdirs();
            }

            if (!directoriesCreated) {
                // Handle the case when directories couldn't be created (e.g., log an error or throw an exception).
                try {
                    throw new IOException("Failed to create directories: " + rootFolder.getAbsolutePath());
                } catch (IOException ee) {

                    new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
                }
            }

            File file = new File(rootFolder, context.getString(R.string.file_name_res));

            // Use StringBuilder to build the content
            StringBuilder stringBuilder = new StringBuilder();
            for (DataModel model : res) {
                String line = model.getTextLine();
                stringBuilder.append(line).append("\n");
            }

            try (FileOutputStream fos = new FileOutputStream(file)) {
                // Write the content to the file
                fos.write(stringBuilder.toString().getBytes());
            } catch (IOException ee){

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }
        }

    }

    public void addDataToSQLLiteDatabase(@NonNull Context context,@NonNull List<DataModel> list){
        DataViewModel viewModel = new ViewModelProvider((ViewModelStoreOwner) context).get(DataViewModel.class);
        for (int i = 0; i < list.size(); i++){

            Log.i("Log-My-Data", list.get(i).getTextLine());
            viewModel.insert(list.get(i));

        }


        writeDataToResultsFile(context, list);

    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private Uri getOrCreateLogFile(@NonNull Context context, String path, String filename) {

        String mimetype = context.getString(R.string.file_type_log);

        // Check if the log file exists
        Uri existingLogFileUri = Android10AndAbove.getInstance()
                .with(context)
                .getLogFileUri(context, filename);

        if (existingLogFileUri != null) {
            // Log file exists, return its Uri
            return existingLogFileUri;
        } else {
            // Log file does not exist, create a new one and return its Uri
            return Android10AndAbove.getInstance()
                    .with(context)
                    .createLogs(context, path, filename, mimetype);
        }
    }



    public void writeErrorLogs(@NonNull Context context, @NonNull String content) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (logFileUri == null) {
                // The log file doesn't exist, create a new one and store its Uri
                String path = context.getString(R.string.app_name);
                String filename = context.getString(R.string.file_name_logs);
                logFileUri = getOrCreateLogFile(context, path, filename);
            }

            try (OutputStream fos = context.getContentResolver().openOutputStream(logFileUri, "wa")) {
                // Append the new content to the file
                fos.write(content.getBytes());
                fos.write("\n\n".getBytes()); // Add a new line after each string

            } catch (IOException ee) {

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }
        }

        else {


            File rootFolder = new File(Environment.getExternalStorageDirectory(), context.getString(R.string.app_name));

            File file = new File(rootFolder, context.getString(R.string.file_name_logs));

            try (FileOutputStream fos = new FileOutputStream(file, true)) {
                // Append the new content to the file
                fos.write(content.getBytes());
                fos.write("\n\n".getBytes()); // Add a new line after each string
            } catch (IOException ee){

                new DataLogic().writeErrorLogs(context, Objects.requireNonNull(ee.getMessage()));
            }
        }


    }


}
