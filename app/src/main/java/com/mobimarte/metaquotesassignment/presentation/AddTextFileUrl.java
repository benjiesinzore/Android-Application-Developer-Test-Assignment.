package com.mobimarte.metaquotesassignment.presentation;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import com.mobimarte.metaquotesassignment.R;
import com.mobimarte.metaquotesassignment.data.logic.DataLogic;
import com.mobimarte.metaquotesassignment.data.viewmodel.DataViewModel;
import com.mobimarte.metaquotesassignment.data.viewmodel.LiveDataViewModel;
import com.mobimarte.metaquotesassignment.databinding.ActivityMainBinding;
import com.mobimarte.metaquotesassignment.utils.MyPreferences;

import java.util.Objects;

public class AddTextFileUrl extends AppCompatActivity {

    private LiveDataViewModel liveDataViewModel;
    private ProgressDialog progressDialog;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Top Bar Layout Properties
        binding.topBarTitle.setText(this.getString(R.string.add_url_act_ttle));
        binding.topBarBack.setOnClickListener(v -> finishAffinity());

        // Get the ViewModel.
        liveDataViewModel = new ViewModelProvider(this).get(LiveDataViewModel.class);

        MyPreferences pref = new MyPreferences(this);

        DataViewModel viewModel = new ViewModelProvider(this).get(DataViewModel.class);


        progressDialog = new ProgressDialog(this);


        binding.btnDownloadAndProcessData.setOnClickListener(v ->{

            String stringFilter = binding.edtLineSelectFilter.getText().toString().trim();

            if (binding.edtTxtFileUrl.getText().toString().isEmpty()){
                binding.edtTxtFileUrl.setError(this.getString(R.string.field_required));
            }

            else if (!binding.edtTxtFileUrl.getText().toString().trim().startsWith(this.getString(R.string.https)) ||

                    !binding.edtTxtFileUrl.getText().toString().trim().startsWith(this.getString(R.string.http))) {

                binding.edtTxtFileUrl.setError(this.getString(R.string.enter_valid_url));

            }

            else {

                viewModel.deleteAll();
                pref.setStringFilter(stringFilter);

                progressDialog.setMessage(this.getString(R.string.please_wait_a_moment));
                progressDialog.setCancelable(false);
                progressDialog.show();

                new DataLogic()
                        .getDataToDisplayOnUI(this, binding.edtTxtFileUrl.getText().toString().trim());
            }

        });

        observeFileDownloadStatus();

    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void observeFileDownloadStatus(){

        // Create the observer which updates the UI.
        final Observer<String> downloadStatus = newValue
                -> {

            if (Objects.equals(newValue, this.getString(R.string.res_success))){

                progressDialog.dismiss();
                Toast.makeText(this, (this.getString(R.string.res_success)), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(this, ShowDataActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));

                new DataLogic().getFileFromFolderAndAddContentsToList(this);

            } else if (!Objects.equals(newValue, this.getString(R.string.res_success))) {

                progressDialog.dismiss();
                Toast.makeText(this, (this.getString(R.string.error_mess))
                        + "\n" + newValue, Toast.LENGTH_SHORT).show();
            }


        };

        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        liveDataViewModel.getDownloadStatus().observe(this, downloadStatus);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        finishAffinity();
    }
}