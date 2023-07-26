package com.mobimarte.metaquotesassignment.data.sqllite;

import androidx.lifecycle.LiveData;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.mobimarte.metaquotesassignment.data.datamodel.DataModel;

import java.util.List;

@androidx.room.Dao
public interface DAO {

    @Query("SELECT * FROM text_file")// ORDER BY pri_key ASC
    LiveData<List<DataModel>> getAlphabetizedWords();

    @Query("SELECT * FROM text_file")// ORDER BY pri_key ASC
    List<DataModel> getListOfLines();


    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(DataModel word);

    @Query("DELETE FROM text_file")
    void deleteAll();


    @Query("SELECT COUNT(*) FROM text_file")
    int getTotalCount();

}
