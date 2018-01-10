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
        pipeSizes = ccDBHelper.getListofThis("Pipe_Size", "Pipe_Data", new String[] {}, new String[] {}, true);
        pipeSizes.add(0, BLANK_SELECTION);
        return pipeSizes;
    }

    public ArrayList<String> getPipeODsFromSize(String pipeSize)
    {
        ArrayList <String> pipeDetails = new ArrayList<>();
        pipeDetails = ccDBHelper.getListofThis("Weight, ID, Drift", "Pipe_Data", new String[] {"Pipe_Size"},
                new String[] {pipeSize}, false);
        pipeDetails.add(0, BLANK_SELECTION);
        return pipeDetails;
    }

    public ArrayList<String> getHydStatPresOptions(String LoadingTableName)
    {
        ArrayList <String> hydOptions = new ArrayList<>();
        hydOptions = ccDBHelper.getListofThis("Hydrostatic_Pressure", "Loading_Tables",
                new String[] {"Loading_Table_ID"}, new String[] {String.valueOf(LoadingTableName)}, false);
        hydOptions.add(0, BLANK_SELECTION);
        return hydOptions;

    }

    public ArrayList<String> getLoadingTableForPipe(String pipeSize, String pipeWeight)
    {
        ArrayList <String> loadingTables;
        String pipeID = getPipeId(pipeSize, pipeWeight);
        loadingTables = new ArrayList<String>(Arrays.asList(ccDBHelper.getThis("LT", "Pipe_LT_Selection", new String[] {"Pid"},
                new String[] {pipeID})));
        return loadingTables;
    }

    public String getPipeId(String pipeSize, String pipeWeight)
    {
        int pipeId = 0;
        return  ccDBHelper.returnCheck(ccDBHelper.getThis("_id", "Pipe_Data", new String[] {"Pipe_Size", "Weight"},
                        new String[]  {pipeSize, pipeWeight}));
    }

    public void setInternalPipeId(String pipeSize, String pipeWeight)
    {
        mPipeID =  getPipeId( pipeSize,  pipeWeight);
    }

    public void setInternalLoadingTable(boolean CoiledTubing){
        ArrayList <String> loadingTables;
        loadingTables = ccDBHelper.getListofThis(
                                "LT", "Pipe_LT_Selection", new String[] {"Pid"},
                                new String[] {String.valueOf(mPipeID)}, true);
        if (loadingTables.size() == 0) {
            loadingTables.add("NULL");
            return; //No Entries found in database
        }
        if (CoiledTubing)
            mLoadingTable = String.valueOf(loadingTables.get(0));
        else
            mLoadingTable = String.valueOf(loadingTables.get(1));
    }


    public String getSlipSubDetails(){
        String result[] ;
        String[] Args =  {String.valueOf(mPipeID)};
        result = ccDBHelper.getThis("SSid", "Pipe_SS_Selection", new String[]{"Pid"}, Args);
        Args[0] =  result[0];
        result =  ccDBHelper.getThis("OD, PartNo", "Slip_sub", new String[]{"_id"}, Args);
        return "OD: "+ccDBHelper.returnCheck(result)+
                " PartBo: F"+ccDBHelper.returnCheck(result, 1);
    }

    public String getCuttingHead(){
        String result[];
        String[] Args =  {String.valueOf(mPipeID)};
        result = ccDBHelper.getThis("CHid", "Pipe_CH_Selection", new String[]{"Pid"}, Args);
        Args[0] =  result[0];
        result = ccDBHelper.getThis("OD, PartNo", "cutting_head", new String[]{"_id"}, Args);
        return "OD: "+ccDBHelper.returnCheck(result)+
                " PartNo: F"+ccDBHelper.returnCheck(result, 1);
    }


    public String getCyclinderDetails(){
        String result[];// = "", cylinder="";
        String[] Args =  {String.valueOf(mLoadingTable)};
        result = ccDBHelper.getThis("Cylid", "LT_CYL_Match", new String[]{"Loading_Table"}, Args);
        Args[0] =  result[0];
        result = ccDBHelper.getAll("cutting_head", new String[]{"_id"}, Args);
        String combined = "";
        for (int i = 0; i<result.length/2; i++)
            combined = combined + ccDBHelper.returnCheck(result, (i*2)+1) +
                    ": "+ccDBHelper.returnCheck(result, i*2) + " ";
        return combined;
    }

    public void setInternalHydPres(String HydraulicPressure){
        mHydPress = HydraulicPressure;
    }

    public String getTopProp()
    {
        String result[];
        String[] Args =  {mHydPress, mLoadingTable};
        result = ccDBHelper.getThis("Top_Propellant_Size, Top_Propellant_PartNo", "Loading_Tables",
                new String[]{"Hydrostatic_Pressure", "Loading_Table_ID"}, Args);
        return "Size: "+ccDBHelper.returnCheck(result)
                +" PartNo: F"+ccDBHelper.returnCheck(result, 1);
    }

    public String getBotProp()
    {
        String result[];
        result = ccDBHelper.getThis("Bot_Propellant_Size, Bot_Propellant_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"},
                new String[] {mHydPress, mLoadingTable});
        return "Size: "+ccDBHelper.returnCheck(result) +
                " PartNo: F"+ccDBHelper.returnCheck(result, 1);
    }


    public String getCatSizeNo()
    {
        String result [];
        result = ccDBHelper.getThis("Catalyst_Size, Catalyst_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return "Size: "+ccDBHelper.returnCheck(result)+
                " PartNo: F"+ccDBHelper.returnCheck(result, 1);
    }

    public String getChokeDiameter()
    {
        String result [];
        result = ccDBHelper.getThis("Choke", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return ccDBHelper.returnCheck(result);
    }



    public String getBurstDisc()
    {
        String result [];
        result = ccDBHelper.getThis("Burst_Disc", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return ccDBHelper.returnCheck(result);
    }

    public String getIgnitionLengthAndSub()
    {
        String result [];
        result = ccDBHelper.getThis("Ignition_Sub_Length, Ignition_Sub_PartNo", "Loading_Tables",
                new String[] {"Hydrostatic_Pressure", "Loading_Table_ID"}, new String[] {mHydPress, mLoadingTable});
        return "Size: "+result[0]+" PartNo: F"+result[1];
    }

    public String getVarFromParsedRow(String var, String row){
        return ccDBHelper.getVarFromParsedRow(var, row);
    }
}
