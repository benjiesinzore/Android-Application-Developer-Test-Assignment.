package com.mobimarte.metaquotesassignment.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class MyPreferences {
    int PRIVATE_MODE = 0;

    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    SharedPreferences.Editor editor;

    // Context
    Context _context;
    // Sharedpref file name  //
    private static final String ANDROID_VERSION = "ANDROID_VERSION";
    private static final String PREF_NAME = "PREF_NAME";
    private static final String STRING_FILTER = "STRING_FILTER";

    // Constructor
    public MyPreferences(Context context) {
        this._context = context;


        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }


    public void setAndroidVersion(String url) {
        editor.putString(ANDROID_VERSION, url);
        editor.commit();

    }



    public void setStringFilter(String url) {
        editor.putString(STRING_FILTER, url);
        editor.commit();

    }

    public String getStringFilter() {

        if (pref.getString(STRING_FILTER, null) != null)
            return pref.getString(STRING_FILTER, null);
        else
            return null;
    }

}