package com.bhge.wirelineassistant;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xicko on 10/8/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.bhge.wirelineassistant/databases/";

    private static String DB_NAME = "cc.db";

    private SQLiteDatabase myDataBase;

    private final Context myContext;

    private final String BLANK_SELECTION = "     ";

    /**
     * Constructor
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context) {

        super(context, DB_NAME, null, 1);
        this.myContext = context;
    }

    /**
     * Creates a empty database on the system and rewrites it with your own database.
     * */
    public void createDataBase() throws IOException {

        boolean dbExist = checkDataBase();

        if(dbExist){
            //do nothing - database already exist
        }else{

            //By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
            this.getReadableDatabase();

            try {

                copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copying database");

            }
        }

    }

    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

        }catch(SQLiteException e){

            //database does't exist yet.

        }

        if(checkDB != null){

            checkDB.close();

        }

        return checkDB != null ? true : false;
    }

    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase() throws IOException{

        //Open your local db as the input stream
        InputStream myInput = myContext.getAssets().open(DB_NAME);

        // Path to the just created empty db
        String outFileName = DB_PATH + DB_NAME;

        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);

        //transfer bytes from the inputfile to the outputfile
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer))>0){
            myOutput.write(buffer, 0, length);
        }

        //Close the streams
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {

        //Open the database
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {

        if(myDataBase != null)
            myDataBase.close();

        super.close();

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    // Add your public helper methods to access and get content from the database.
    // You could return cursors by doing "return myDataBase.query(....)" so it'd be easy
    // to you to create adapters for your views.

    public ArrayList<String> getPipeSizeList(){
        ArrayList <String> pipeSizes = new ArrayList<>();
        String PIPE_SIZE_QUERY = "SELECT DISTINCT size FROM Pipe_Data";
        //Cursor cursor = myDataBase.query(true, "Pipe_Data", null, null, null, null, null, null, null );
        Cursor cursor = myDataBase.rawQuery(PIPE_SIZE_QUERY, null);
        //TODO: change to regular SQL query
        int sizeColIndex = cursor.getColumnIndex("size");
        String currentItem;
        pipeSizes.add(BLANK_SELECTION);
        while(cursor.moveToNext()) {
            currentItem = cursor.getString(sizeColIndex);
            Log.d("CCDB", "getPipeOD: " + currentItem);
            pipeSizes.add(currentItem);
        }
        cursor.close();
        return pipeSizes;
    }

    public ArrayList<String> getPipeODsFromSize(String pipeSize)
    {
        ArrayList <String> pipeDetails = new ArrayList<>();
        Cursor cursor = myDataBase.query("Pipe_Data", null, "size = \"" + pipeSize + "\"", null, null, null, null, null );
        int weightColIndex = cursor.getColumnIndex("weight");
        int IDColIndex = cursor.getColumnIndex("ID");
        int driftColIndex = cursor.getColumnIndex("drift");
        String currentItemWeight;
        String currentItemID;
        String currentItemdrift;
        pipeDetails.add (BLANK_SELECTION);
        while(cursor.moveToNext()) {
            currentItemWeight = cursor.getString(weightColIndex);
            currentItemID = cursor.getString(IDColIndex);
            currentItemdrift = cursor.getString(driftColIndex);
            pipeDetails.add("Weight: " + currentItemWeight + " ID: " + currentItemID + " Drift: " + currentItemdrift);
        }
        cursor.close();
        return pipeDetails;
    }

    public ArrayList<String> getHydStatPresOptions(int LoadingTableName)
    {
        ArrayList <String> hydOptions = new ArrayList<>();
        String[] HydPres= {"HydPres"};
        Cursor cursor = myDataBase.query("LT_Pres_Loading", HydPres, "LTName = " + LoadingTableName,
                null, null, null, null, null );
        int hydPresColIndex = cursor.getColumnIndex(HydPres[0]);
        hydOptions.add(BLANK_SELECTION);
        String currentItem;
        while(cursor.moveToNext()) {
            currentItem = cursor.getString(hydPresColIndex);
            hydOptions.add(currentItem);
        }
        cursor.close();
        return hydOptions;

    }

    public ArrayList<Integer> getLoadingTableForPipe(String pipeSize, String pipeWeight)
    {
        ArrayList <Integer> loadingTables = new ArrayList<>();
        String JOIN_QUERY = "SELECT DISTINCT Pipe_LT_Selection.LT FROM Pipe_LT_Selection INNER JOIN Pipe_Data ON Pipe_Data._id = Pipe_LT_Selection.Pid " +
                "Where Pipe_Data.size = ? AND Pipe_Data.weight = ?";
        //String JOIN_QUERY = "SELECT DISTINCT LT FROM Pipe_LT_Selection WHERE Pid = ?";
        //        String[] Args =  {Integer.toString(getPipeId(pipeSize,pipeWeight))};
        String[] Args = {pipeSize, pipeWeight};
        Cursor cursor = myDataBase.rawQuery(JOIN_QUERY, Args);
        if (cursor.getCount() == 0) {
            loadingTables.add(-1);
            return loadingTables; //No Entries found in database
        }
        while(cursor.moveToNext()) {
            loadingTables.add(cursor.getInt(0));
        }
        cursor.close();
        return loadingTables;
    }

    public int getPipeId(String pipeSize, String pipeWeight)
    {
        int pipeId = 0;
        String STRING_QUERY = "SELECT _id FROM Pipe_Data Where size = ? AND weight = ?";
        String[] Args =  {pipeSize, pipeWeight};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            pipeId = cursor.getInt(0);
        }
        cursor.close();
        return pipeId;
    }
}
