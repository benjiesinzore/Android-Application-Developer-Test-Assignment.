package com.mobimarte.metaquotesassignment.data.viewmodel;


import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.mobimarte.metaquotesassignment.data.datamodel.DataModel;
import com.mobimarte.metaquotesassignment.data.sqllite.Repository;

import java.util.List;

public class DataViewModel  extends AndroidViewModel {

    private final Repository mRepository;

    public DataViewModel(Application application) {
        super(application);
        mRepository = new Repository(application);
    }

    public LiveData<List<DataModel>> getAllWords() {
        return mRepository.getAllWords();
    }

    public List<DataModel> getListOfLines() {
        return mRepository.getListOfLines();
    }

    public void insert(DataModel word) {
        mRepository.insert(word);
    }

    public void deleteAll(){
        mRepository.deleteAll();
    }


}
