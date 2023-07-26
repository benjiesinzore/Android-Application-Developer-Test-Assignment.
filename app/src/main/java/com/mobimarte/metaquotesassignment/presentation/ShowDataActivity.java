package com.mobimarte.metaquotesassignment.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.mobimarte.metaquotesassignment.R;
import com.mobimarte.metaquotesassignment.data.adapter.ListAdapter;
import com.mobimarte.metaquotesassignment.data.viewmodel.DataViewModel;
import com.mobimarte.metaquotesassignment.databinding.ActivityShowDataBinding;

public class ShowDataActivity extends AppCompatActivity {


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityShowDataBinding binding = ActivityShowDataBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DataViewModel viewModel = new ViewModelProvider(this).get(DataViewModel.class);
        final ListAdapter adapter = new ListAdapter( (new ListAdapter.ProductDiff()),
                viewModel.getListOfLines(), this);

        //Top Bar Layout Properties
        binding.topBarTitle.setText(this.getString(R.string.view_data_act_ttle));
        binding.topBarBack.setOnClickListener(v -> {
            startActivity(new Intent(this, AddTextFileUrl.class)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });


        binding.recycleView.setAdapter(adapter);

        binding.recycleView.setLayoutManager(new LinearLayoutManager(this));



        // Update the cached copy of the words in the adapter.
        // Observe the LiveData and update the adapter when it changes
        viewModel.getAllWords().observe(this, dataModels -> {
            adapter.submitList(dataModels);
            adapter.notifyDataSetChanged();
        });


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(this, AddTextFileUrl.class)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }
}