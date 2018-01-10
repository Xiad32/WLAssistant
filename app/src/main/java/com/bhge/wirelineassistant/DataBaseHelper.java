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
import java.util.function.Consumer;

/**
 * Created by xicko on 10/8/17.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static String DB_PATH;
    private static String DB_NAME;
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    private final int BYTE_BUFFER_SIZE = 1024;

    /**
     * Constructor=
     * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     * @param context
     */
    public DataBaseHelper(Context context, String mDB_NAME, String mDB_PATH) {
        super(context, mDB_NAME, null, 1);
        DB_NAME = mDB_NAME;
        DB_PATH = mDB_PATH;
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
            try {copyDataBase();
            } catch (IOException e) {throw new Error("Error copying database");}
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
        }catch(SQLiteException e){//database does't exist yet.
        }

        if(checkDB != null){checkDB.close();}

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
        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
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


    public String[] getThis(String items, String table, String[] variables, String[] conditions)
    {
        String[] itemsCut = items.split(",");
        int itemsCount = itemsCut.length;
        String STRING_QUERY = "SELECT " + items +
                " FROM " + table + " Where ";
        for (String var : variables){
            STRING_QUERY = STRING_QUERY + var + " = ? AND ";
        }
        STRING_QUERY = STRING_QUERY.replaceAll("\\w\\w\\w\\s$", " ");
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        String result[] = new String[itemsCount* cursor.getCount()];
        Log.i("DBHelper", "getThis: size results buffer" + Integer.toString(itemsCount * cursor.getCount()) );
        Log.i("DBHelper", "getThis: cursor Pos" + Integer.toString(cursor.getPosition()) );
        while (cursor.moveToNext()){
            Log.i("DBHelper", "getThis: cursor Pos" + Integer.toString(cursor.getPosition()) );
            int upperBound = (cursor.getPosition()+1) * itemsCount;
            for (int i = cursor.getPosition()*itemsCount; i< upperBound; i++){
                int j = 0;Log.i("DBHelper", "getThis: i, position:" + Integer.toString(i) + " " + Integer.toString(cursor.getPosition()) );
                Log.i("DBHelper", "getThis: upper limit:" + Integer.toString(upperBound) );
                result[i] = cursor.getString(j);
                j++;
            }
        }
        cursor.close();
        return parseResults(result);
    }

    public String[] getThis(String items, String table)
    {
        String[] itemsCut = items.split(",");
        int itemsCount = itemsCut.length;
        String result[] = new String[itemsCount];
        String STRING_QUERY = "SELECT " + items +
                " FROM " + table;
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , new String[] {});
        while (cursor.moveToNext()){
            for (int i = 0; i<itemsCount; i++){
                result[i] = cursor.getString(i);
            }
        }
        cursor.close();
        return parseResults(result);
    }

    //returns an array with the even as column name, the odd is the value
    public String[] getAll(String table, String[] variables, String[] conditions)
    {
        String STRING_QUERY = "SELECT * " +
                " FROM "+ table;
        if (variables.length > 0 && conditions.length > 0 && (variables.length == conditions.length) ) {
            STRING_QUERY = STRING_QUERY + " Where ";
            for (String var : variables) {
                STRING_QUERY = STRING_QUERY + var + " = ? AND ";
            }
            STRING_QUERY = STRING_QUERY.replaceAll("\\w\\w\\w\\s$", " ");
        }
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        String[] titles = cursor.getColumnNames();
        String[] results = new String[(titles.length-1)*2];
        while (cursor.moveToNext()){
            boolean skipped = false;
            for (int i = 0; i<titles.length; i++){
                if (!titles[i].equals("_id") & !skipped){
                    results[(i*2)+1] = titles[i];
                    results[i*2] = cursor.getString(i);
                }
                else if (titles[i].equals("_id") & !skipped) {
                    skipped = true;
                }
                else
                {
                    results[((i-1)*2)+1] = titles[i];
                    results[(i-1)*2] = cursor.getString(i);
                }
            }
        }
        cursor.close();
        return parseResults(results);
    }

    public String[] getAll(String table)
    {
        String STRING_QUERY = "SELECT * " +
                " FROM "+ table;
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , new String[] {});
        String[] titles = cursor.getColumnNames();
        String[] results = new String[(titles.length)*2];
        while (cursor.moveToNext()){
            for (int i = 0; i<titles.length; i++){
                results[(i*2)+1] = titles[i];
                results[i*2] = cursor.getString(i);
            }
        }
        cursor.close();
        return parseResults(results);
    }

    //returns an array with the even as column name, the odd is the value
    public ArrayList<String> getListofAll(String table, String[] variables, String[] conditions)
    {
        String STRING_QUERY = "SELECT * " +
                " FROM "+ table + " Where ";
        for (String var : variables){
            STRING_QUERY = STRING_QUERY + var + " = ? AND ";
        }
        STRING_QUERY = STRING_QUERY.replaceAll("\\w\\w\\w\\s$", " ");
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        String[] titles = cursor.getColumnNames();
        String rows = "";
        String row = "";
        for (String title : titles){
            rows = rows + "_" + title;
        }
        ArrayList<String> results = new ArrayList<>();
        int i;
        while (cursor.moveToNext()){
            for(i= 0; i< titles.length; i++)
            {row = row + "_" + cursor.getString(i);}
            results.add(row);
            row = new String("");
        }
        cursor.close();
        return parseResults(results);
    }

    public ArrayList<String> getListofThis(String items, String table, String[] variables, String[] conditions, boolean distinct)
    {
        String[] itemsCut = items.split(",");
        int itemsCount = itemsCut.length;
        String result[] = new String[itemsCount];
        String STRING_QUERY = "SELECT ";
        if (distinct)
            STRING_QUERY = STRING_QUERY + "DISTINCT ";
        STRING_QUERY = STRING_QUERY + items;
        Cursor cursor;
        STRING_QUERY = STRING_QUERY + " FROM " + table;
        if (variables.length > 0 && conditions.length > 0 && (variables.length == conditions.length) ){
            STRING_QUERY = STRING_QUERY + " Where ";
            for (String var : variables){
                STRING_QUERY = STRING_QUERY + var + " = ? AND ";}
            STRING_QUERY = STRING_QUERY.replaceAll("\\w\\w\\w\\s$", " ");
            cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        }
        else
            cursor = myDataBase.rawQuery(STRING_QUERY , new String[] {});
        String row = "";
        ArrayList<String> results = new ArrayList<>();
        int i;
        if (itemsCount == 1){
        while (cursor.moveToNext()){
            row = cursor.getString(0);
            for(i= 1; i< itemsCount; i++)
            {row = row + "_" + cursor.getString(i);}
            results.add(row);
            row = new String("");
        }}
        else
            while (cursor.moveToNext()){
                row = cursor.getColumnName(0);
                row = row + "_" + cursor.getString(0);
                for(i= 1; i< itemsCount; i++)
                {   row = row + "_" + cursor.getColumnName(i);
                    row = row + "_" + cursor.getString(i);
                }
                results.add(row);
                row = new String("");
            }
        cursor.close();
        return parseResults(results);

    }


    private String[] parseResults (String[] results){
        for (String result : results){
            result.replace("_", " ");
        }
        return results;
    }

    private ArrayList<String> parseResults(ArrayList<String> results){
        for(int i=0; i < results.size(); i++) {
            results.set(i, results.get(i).replace("_"," "));
        }
        return results;
    }

    public String getVarFromParsedRow(String var, String row){
        int start = row.indexOf(" ", row.indexOf(var));
        return row.substring(start, row.indexOf(" ", start+1) );
    }

    public String returnCheck(String[] queryReturn){
        if(queryReturn.length >0)
            return queryReturn[0];
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String calling = stackTraceElements[stackTraceElements.length].getMethodName();
        Log.d("DB Return Check, ", calling + "@ returnCheck: Expected single return, returned multiple.");
        return "NULL";
    }

    public String returnCheck(String[] queryReturn, int position){
        if(queryReturn.length > position)
            return queryReturn[position];
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String calling = stackTraceElements[stackTraceElements.length].getMethodName();
        Log.d("DB Return Check, ", calling + "@ returnCheck: Expected single return, returned multiple.");
        return "NULL";
    }

}
