package com.mobimarte.metaquotesassignment.data.datamodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "text_file")
public class DataModel {

    @PrimaryKey(autoGenerate = true)
    private int pri_key;


    @ColumnInfo(name = "textLine")
    private String textLine;

    public String getTextLine() {
        return textLine;
    }

    public void setTextLine(String textLine) {
        this.textLine = textLine;
    }

    public int getPri_key() {
        return pri_key;
    }

    public void setPri_key(int pri_key) {
        this.pri_key = pri_key;
    }
}
