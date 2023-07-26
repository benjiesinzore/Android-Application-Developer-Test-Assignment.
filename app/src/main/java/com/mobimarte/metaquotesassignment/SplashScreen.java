package com.mobimarte.metaquotesassignment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.mobimarte.metaquotesassignment.data.viewmodel.DataViewModel;
import com.mobimarte.metaquotesassignment.databinding.ActivitySplashScreenBinding;
import com.mobimarte.metaquotesassignment.presentation.AddTextFileUrl;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {


    private final int PERMISSION_STORAGE_CODE = 10;

    private DataViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySplashScreenBinding binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        viewModel = new ViewModelProvider(this).get(DataViewModel.class);

        if(!ifPublicStorageAvailableForRW()) {
            Toast.makeText(this, this.getString(R.string.provide_storage),
                    Toast.LENGTH_SHORT).show();
        }

        //PERMISSION
        checkForReadWritePermission();

        //CLEAR SQLLite DB
        clearSharedPrefAndClearSQLLiteDB();

        //DELAY SPLASHSCREEN FOR 5 SECONDS
        callDelaySplashScreen();
    }

    private void clearSharedPrefAndClearSQLLiteDB() {

        viewModel.deleteAll();
    }

    public Boolean ifPublicStorageAvailableForRW(){

        String extStorageState = Environment.getExternalStorageState();
        return extStorageState.equals(Environment.MEDIA_MOUNTED);
    }



    private void checkForReadWritePermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager
                    .PERMISSION_DENIED){
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_STORAGE_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_STORAGE_CODE){

            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED){

                Toast.makeText(this, this.getString(R.string.permission_denied),
                        Toast.LENGTH_SHORT).show();
            } else {

                Toast.makeText(this, this.getString(R.string.permission_granted),
                        Toast.LENGTH_SHORT).show();
            }


        }
    }


    // Delay of 5 seconds (5000 milliseconds)
    public void callDelaySplashScreen(){
        int delayMillis = 5000;
        new Handler().postDelayed(() ->

                        startActivity(new Intent(SplashScreen.this, AddTextFileUrl.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP))

                , delayMillis
        );
    }

}