package com.mobimarte.metaquotesassignment.data.sqllite;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.mobimarte.metaquotesassignment.data.datamodel.DataModel;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;


public class Repository {

    private final DAO mDao;

    public Repository(Application application) {
        RoomDatabase db = RoomDatabase.getDatabase(application);
        mDao = db.wordDao();
    }

    public LiveData<List<DataModel>> getAllWords() {


        MutableLiveData<List<DataModel>> res = new MutableLiveData<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<LiveData<List<DataModel>>> results = executorService.submit(mDao::getAlphabetizedWords);
        try {
            res = (MutableLiveData<List<DataModel>>) results.get();
        } catch (Exception ee){
            //
        }
        return res;
    }


    public List<DataModel> getListOfLines() {


        List<DataModel> res = new ArrayList<>();
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<List<DataModel>> results = executorService.submit(mDao::getListOfLines);
        try {
            res = results.get();
        } catch (Exception ee){
            //
        }
        return res;
    }


    public void insert(DataModel word) {
        RoomDatabase.databaseWriteExecutor.execute(() -> mDao.insert(word));

    }

    public void deleteAll() {
        RoomDatabase.databaseWriteExecutor.execute(mDao::deleteAll);

    }

    public int getTotalCount() {

        int res = 0;
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<Integer> results = executorService.submit(mDao::getTotalCount);
        try {
            res = results.get();
        } catch (Exception ee){
            //
        }
        return res;
    }


}
