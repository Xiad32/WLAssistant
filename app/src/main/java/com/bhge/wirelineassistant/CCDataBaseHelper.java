package com.bhge.wirelineassistant;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by xicko on 10/29/17.
 */

public class CCDataBaseHelper {
    private static final String mDB_PATH = "/data/data/com.bhge.wirelineassistant/databases/";
    private static final String mDB_NAME = "cc.db";
    private DataBaseHelper ccDBHelper;
    private final String BLANK_SELECTION = "     ";
    private String mPipeID = "" , mLoadingTable = "" , mHydPress = "";
    private static String DB_NAME = "cc.db";

    public CCDataBaseHelper(Context context)
    {
        //Load Database:
        ccDBHelper = new DataBaseHelper(context, mDB_NAME, mDB_PATH);
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

    public ArrayList<String> getPipeSizeList(){
        ArrayList <String> pipeSizes = new ArrayList<>();
        //String PIPE_SIZE_QUERY = "SELECT DISTINCT size FROM Pipe_Data";
        //Cursor cursor = myDataBase.query(true, "Pipe_Data", null, null, null, null, null, null, null );
        //Cursor cursor = myDataBase.rawQuery(PIPE_SIZE_QUERY, null);
        //int sizeColIndex = cursor.getColumnIndex("size");
        //String currentItem;

//        while(cursor.moveToNext()) {
//            currentItem = cursor.getString(sizeColIndex);
//            Log.d("CCDB", "getPipeOD: " + currentItem);
//            pipeSizes.add(currentItem);
//        }
        pipeSizes = ccDBHelper.getListofThis("Pipe_Size", "Pipe_Data", new String[] {}, new String[] {}, true);
//        cursor.close();
        pipeSizes.add(0, BLANK_SELECTION);
        return pipeSizes;
    }

    public ArrayList<String> getPipeODsFromSize(String pipeSize)
    {
        ArrayList <String> pipeDetails = new ArrayList<>();
//        Cursor cursor = myDataBase.query("Pipe_Data", null, "size = \"" + pipeSize + "\"", null, null, null, null, null );
//        int weightColIndex = cursor.getColumnIndex("weight");
//        int IDColIndex = cursor.getColumnIndex("ID");
//        int driftColIndex = cursor.getColumnIndex("drift");
//        String currentItemWeight;
//        String currentItemID;
//        String currentItemdrift;
//
//        while(cursor.moveToNext()) {
//            currentItemWeight = cursor.getString(weightColIndex);
//            currentItemID = cursor.getString(IDColIndex);
//            currentItemdrift = cursor.getString(driftColIndex);
//            pipeDetails.add("Weight: " + currentItemWeight + " ID: " + currentItemID + " Drift: " + currentItemdrift);
//        }
//        cursor.close();
        pipeDetails = ccDBHelper.getListofThis("Weight, ID, Drift", "Pipe_Data", new String[] {"Pipe_Size"},
                new String[] {pipeSize}, false);
        pipeDetails.add(0, BLANK_SELECTION);
        return pipeDetails;
    }

    public ArrayList<String> getHydStatPresOptions(String LoadingTableName)
    {
        ArrayList <String> hydOptions = new ArrayList<>();
//        String[] HydPres= {"HydPres"};
//        Cursor cursor = myDataBase.query("LT_Pres_Loading", HydPres, "LTName = " + LoadingTableName,
//                null, null, null, null, null );
//        int hydPresColIndex = cursor.getColumnIndex(HydPres[0]);
//        hydOptions.add(BLANK_SELECTION);
//        String currentItem;
//        while(cursor.moveToNext()) {
//            currentItem = cursor.getString(hydPresColIndex);
//            hydOptions.add(currentItem);
//        }
//        cursor.close();
        hydOptions = ccDBHelper.getListofThis("Hydrostatic_Pressure", "Loading_Tables",
                new String[] {"Loading_Table_ID"}, new String[] {String.valueOf(LoadingTableName)}, false);
        hydOptions.add(0, BLANK_SELECTION);
        return hydOptions;

    }

    public ArrayList<String> getLoadingTableForPipe(String pipeSize, String pipeWeight)
    {
        ArrayList <String> loadingTables;
//        String JOIN_QUERY = "SELECT DISTINCT Pipe_LT_Selection.LT FROM Pipe_LT_Selection INNER JOIN Pipe_Data ON Pipe_Data._id = Pipe_LT_Selection.Pid " +
//                "Where Pipe_Data.size = ? AND Pipe_Data.weight = ?";
//        String[] Args = {pipeSize, pipeWeight};
//        Cursor cursor = myDataBase.rawQuery(JOIN_QUERY, Args);
//        if (cursor.getCount() == 0) {
//            loadingTables.add(-1);
//            return loadingTables; //No Entries found in database
//        }
//        while(cursor.moveToNext()) {
//            loadingTables.add(cursor.getInt(0));
//        }
//        cursor.close();
        loadingTables = new ArrayList<String>(Arrays.asList(ccDBHelper.getThis("LT", "Pipe_LT_Selection")));
        return loadingTables;
    }

    public String getPipeId(String pipeSize, String pipeWeight)
    {
        int pipeId = 0;
        return  ccDBHelper.getThis("_id", "Pipe_Data", new String[] {"Pipe_Size", "Weight"},
                        new String[]  {pipeSize, pipeWeight})[0];
    }

    public void setInternalPipeId(String pipeSize, String pipeWeight)
    {
        mPipeID =  getPipeId( pipeSize,  pipeWeight);
    }

    public void setInternalLoadingTable(boolean CoiledTuing){
        CoiledTuing = true; //TODO: to be removed later
        ArrayList <String> loadingTables;
//        String JOIN_QUERY = "SELECT DISTINCT LT FROM Pipe_LT_Selection Where Pid = ?";
//        String[] Args =  {String.valueOf(mPipeID)};
//        Cursor cursor = myDataBase.rawQuery(JOIN_QUERY, Args);
        loadingTables = ccDBHelper.getListofThis(
                                "LT", "Pipe_LT_Selection", new String[] {"Pid"},
                                new String[] {String.valueOf(mPipeID)}, true);
        if (loadingTables.size() == 0) {
            loadingTables.add("NULL");
            return; //No Entries found in database
        }
        if (CoiledTuing)
            mLoadingTable = String.valueOf(loadingTables.get(0));
        else
            mLoadingTable = String.valueOf(loadingTables.get(1));
        //TODO add support to CT pipes
    }


    public String getSlipSubDetails(){
        String result[] ;
//        String STRING_QUERY = "SELECT SSid FROM Pipe_SS_Selection Where Pid = ?";
        String[] Args =  {String.valueOf(mPipeID)};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = cursor.getString(0);
//        }
        result = ccDBHelper.getThis("SSid", "Pipe_SS_Selection", new String[]{"Pid"}, Args);
//        STRING_QUERY = "SELECT OD, PartNo FROM Slip_sub Where _id = ?";
        Args[0] =  result[0];
//        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "OD: "+cursor.getString(0)+" PartNo: F"+cursor.getString(1);
//        }
//        cursor.close();
        result =  ccDBHelper.getThis("OD, PartNo", "Slip_sub", new String[]{"_id"}, Args);
        return "OD: "+result[0]+" PartBo: F"+result[1];
    }

    public String getCuttingHead(){
        String result[];
//        String STRING_QUERY = "SELECT CHid FROM Pipe_CH_Selection Where Pid = ?";
        String[] Args =  {String.valueOf(mPipeID)};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = cursor.getString(0);
//        }
        result = ccDBHelper.getThis("CHid", "Pipe_CH_Selection", new String[]{"Pid"}, Args);
//        STRING_QUERY = "SELECT OD, PartNo FROM cutting_head Where _id = ?";
        Args[0] =  result[0];
//        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "OD: "+cursor.getString(0)+" PartNo: F"+cursor.getString(1);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("OD, PartNo", "cutting_head", new String[]{"_id"}, Args);
        return "OD: "+result[0]+" PartNo: F"+result[1];
    }


    public String getCyclinderDetails(){
        String result[];// = "", cylinder="";
//        String STRING_QUERY = "SELECT Cylid FROM LT_CYL_Match Where LTName = ?";
        String[] Args =  {String.valueOf(mLoadingTable)};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            cylinder = cursor.getString(0);
//        }
        result = ccDBHelper.getThis("Cylid", "LT_CYL_Match", new String[]{"Loading_Table"}, Args);
//        STRING_QUERY = "SELECT * FROM Cylinders Where _id = ?";
        Args[0] =  result[0];
//        cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "OD: " + cursor.getString(1) +
//                    " Lenght: " + cursor.getString(2) +
//                    " Part No: F" + cursor.getString(3);
//        }
//        cursor.close();
        result = ccDBHelper.getAll("cutting_head", new String[]{"_id"}, Args);
        String combined = "";
        for (int i = 0; i<result.length/2; i++)
            combined = combined + result[(i*2)+1] +": "+result[i*2]+ " ";
        return combined;
    }

    public void setInternalHydPres(String HydraulicPressure){
        mHydPress = HydraulicPressure;
    }

    public String getTopProp()
    {
        String result[];
//        String STRING_QUERY = "SELECT TopPropSize, TopPropPartNo " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Top_Propellant_Size, Top_Propellant_PartNo", "Loading_Tables",
                new String[]{"Hydrostatic_Pressure", "Loading_Table_ID"}, Args);
        return "Size: "+result[0]+" PartNo: F"+result[1];
    }

    public String getBotProp()
    {
        String result[];
//        String STRING_QUERY = "SELECT BotPropSize, BotPropPartNo " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
//        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Bot_Propellant_Size, Bot_Propellant_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"},
                new String[] {mHydPress, mLoadingTable});
        return "Size: "+result[0]+" PartNo: F"+result[1];
    }


    public String getCatSizeNo()
    {
        String result [];
//        String STRING_QUERY = "SELECT CatSize, CatPartNo " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
//        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "Size: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Catalyst_Size, Catalyst_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return "Size: "+result[0]+" PartNo: F"+result[1];
    }

    public String getChokeDiameter()
    {
        String result [];
//        String STRING_QUERY = "SELECT Choke " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
//        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = cursor.getString(0);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Choke", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return result[0];
    }



    public String getBurstDisc()
    {
        String result [];
//        String STRING_QUERY = "SELECT BurstDisc " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
//        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = cursor.getString(0);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Burst_Disc", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return result[0];
    }

    public String getIgnitionLengthAndSub()
    {
        String result [];
//        String STRING_QUERY = "SELECT IgnitionSize, IgnitionSubPartNo " +
//                "FROM LT_Pres_Loading Where HydPres = ? AND LTName = ?";
//        String[] Args =  {mHydPress, mLoadingTable};
//        Cursor cursor = myDataBase.rawQuery(STRING_QUERY , Args);
//        while (cursor.moveToNext()){
//            result = "Length: " + cursor.getString(0) + " Part No: F" + cursor.getString(1);
//        }
//        cursor.close();
        result = ccDBHelper.getThis("Ignition_Sub_Length, Ignition_Sub_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return "Size: "+result[0]+" PartNo: F"+result[1];
    }

    public String getVarFromParsedRow(String var, String row){
        return ccDBHelper.getVarFromParsedRow(var, row);
    }
}
