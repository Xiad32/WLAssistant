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

    private String mPipeID = "" , mLoadingTable = "" , mHydPress = "";

    /**
     * Constructor=
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

    //Returns the last entry from the cursor
    public String[] getThis(String items, String table, String[] variables, String[] conditions)
    {
        String[] itemsCut = items.split(",");
        int itemsCount = itemsCut.length;
        String result[] = new String[itemsCount];
        String STRING_QUERY = "SELECT " + items +
                " FROM " + table + " Where ";
        for (String var : variables){
            STRING_QUERY = STRING_QUERY + var + " = ? AND";
        }
        STRING_QUERY.replaceAll("AND$", "");
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        while (cursor.moveToNext()){
            for (int i = 0; i<itemsCount; i++){
                result[i] = cursor.getString(i);
            }
        }
        cursor.close();
        return result;
    }

    //returns an array with the even as column name, the odd is the value
    public String[] getAll(String table, String[] variables, String[] conditions)
    {
        String STRING_QUERY = "SELECT * " +
                " FROM "+ table + " Where ";
        for (String var : variables){
            STRING_QUERY = STRING_QUERY + var + " = ? AND";
        }
        STRING_QUERY.replaceAll("AND$", "");
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , conditions);
        String[] titles = cursor.getColumnNames();
        String[] results = new String[(titles.length)*2];
        while (cursor.moveToNext()){
            for (int i = 0; i<titles.length; i++){
                results[(i*2)-1] = titles[i];
                results[i*2] = cursor.getString(i);
            }
        }
        cursor.close();
        return results;
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

    public void setInternalPipeId(String pipeSize, String pipeWeight)
    {
        mPipeID =  String.valueOf(getPipeId( pipeSize,  pipeWeight));
    }

    public void setInternalLoadingTable(boolean CoiledTuing){
        CoiledTuing = true; //TODO: to be removed later
        ArrayList <Integer> loadingTables = new ArrayList<>();
        String JOIN_QUERY = "SELECT DISTINCT LT FROM Pipe_LT_Selection Where Pid = ?";
        String[] Args =  {String.valueOf(mPipeID)};
        Cursor cursor = myDataBase.rawQuery(JOIN_QUERY, Args);
        if (cursor.getCount() == 0) {
            loadingTables.add(-1);
            return; //No Entries found in database
        }
        while(cursor.moveToNext()) {
            loadingTables.add(cursor.getInt(0));
        }
        cursor.close();
        if (CoiledTuing)
            mLoadingTable = String.valueOf(loadingTables.get(0));
        else
            mLoadingTable = String.valueOf(loadingTables.get(1));
        //TODO: add support to CT pipes
    }


    public String getSlipSubDetails(){
        String result="";
        String STRING_QUERY = "SELECT SSid FROM Pipe_SS_Selection Where Pid = ?";
        String[] Args =  {String.valueOf(mPipeID)};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = cursor.getString(0);
        }
        STRING_QUERY = "SELECT OD, PartNo FROM Slip_sub Where _id = ?";
        Args[0] =  result;
        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "OD: "+cursor.getString(0)+" PartNo: F"+cursor.getString(1);
        }
        cursor.close();
        return result;
    }

    public String getCuttingHead(){
        String result="";
        String STRING_QUERY = "SELECT CHid FROM Pipe_CH_Selection Where Pid = ?";
        String[] Args =  {String.valueOf(mPipeID)};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = cursor.getString(0);
        }
        STRING_QUERY = "SELECT OD, PartNo FROM cutting_head Where _id = ?";
        Args[0] =  result;
        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "OD: "+cursor.getString(0)+" PartNo: F"+cursor.getString(1);
        }
        cursor.close();
        return result;
    }


    public String getCyclinderDetails(){
        String result = "", cylinder="";
        String STRING_QUERY = "SELECT Cylid FROM LT_CYL_Match Where LTName = ?";
        String[] Args =  {String.valueOf(mLoadingTable)};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            cylinder = cursor.getString(0);
        }
        STRING_QUERY = "SELECT * FROM Cylinders Where _id = ?";
        Args[0] =  cylinder;
        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "OD: " + cursor.getString(1) +
                    " Lenght: " + cursor.getString(2) +
                    " Part No: F" + cursor.getString(3);
        }
        cursor.close();
        return result;
    }

    public void setInternalHydPres(String HydraulicPressure){
        mHydPress = HydraulicPressure;
    }

    public String getTopProp()
    {
        String result = "";
        String STRING_QUERY = "SELECT TopPropSize, TopPropPartNo " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
        }
        cursor.close();
        return result;
    }

    public String getBotProp()
    {
        String result = "";
        String STRING_QUERY = "SELECT BotPropSize, BotPropPartNo " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
        }
        cursor.close();
        return result;
    }


    public String getCatSizeNo()
    {
        String result = "";
        String STRING_QUERY = "SELECT CatSize, CatPartNo " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
        }
        cursor.close();
        return result;
    }

    public String getChokeDiameter()
    {
        String result = "";
        String STRING_QUERY = "SELECT Choke " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }



    public String getBurstDisc()
    {
        String result = "";
        String STRING_QUERY = "SELECT BurstDisc " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = cursor.getString(0);
        }
        cursor.close();
        return result;
    }

    public String getIgnitionLengthAndSub()
    {
        String result = "";
        String STRING_QUERY = "SELECT IgnitionSize, IgnitionSubPartNo " +
                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
        while (cursor.moveToNext()){
            result = "Length: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
        }
        cursor.close();
        return result;
    }


}
