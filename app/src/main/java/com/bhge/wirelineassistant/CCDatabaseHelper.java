package com.bhge.wirelineassistant;

import android.content.Context;
import android.database.SQLException;
import android.provider.ContactsContract;

import java.io.IOException;

/**
 * Created by xicko on 10/29/17.
 */

public class CCDatabaseHelper {
    private DataBaseHelper ccDBHelper;


    public CCDatabaseHelper(Context context)
    {
        //Load Database:
        ccDBHelper = new DataBaseHelper(context);
        try {
            ccDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            ccDBHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
    }

}
