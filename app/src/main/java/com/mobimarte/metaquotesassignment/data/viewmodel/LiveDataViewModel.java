package com.mobimarte.metaquotesassignment.data.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LiveDataViewModel extends ViewModel {

    // Create a LiveData with a String
    private MutableLiveData<String> downloadStatus;

    public MutableLiveData<String> getDownloadStatus() {
        if (downloadStatus == null) {
            downloadStatus = new MutableLiveData<>();
        }
        return downloadStatus;
    }
}
