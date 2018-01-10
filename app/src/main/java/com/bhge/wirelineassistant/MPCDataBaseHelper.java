package com.bhge.wirelineassistant;

import android.content.Context;
import android.database.SQLException;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xicko on 12/9/17.
 */

class MPCDataBaseHelper {
    private static final String mDB_PATH = "/data/data/com.bhge.wirelineassistant/databases/";
    private static final String mDB_NAME = "mpc.db";
    private DataBaseHelper mpcDBHelper;
    private final static String BLANK_SELECTION = "     ";
    private String mPipeSize;
    private String mPipeType;
    private String mPipeMaterial;
    private String mPipeWeight;
    private String mPipeID;

    public MPCDataBaseHelper(Context context)
    {
        //Load Database:
        mpcDBHelper = new DataBaseHelper(context, mDB_NAME, mDB_PATH);
        try {
            mpcDBHelper.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            mpcDBHelper.openDataBase();
        }catch(SQLException sqle){
            throw sqle;
        }
    }

    public ArrayList<String> getPipeSizeList(String PipeType) {
        ArrayList<String> pipeSizes = new ArrayList<>();
        pipeSizes = mpcDBHelper.getListofThis("Pipe_OD_in", "Pipe", new String[] {"Pipe_Type"}, new String[] {PipeType}, true);
        pipeSizes.add(0, BLANK_SELECTION);
        return pipeSizes;
    }

    public ArrayList<String> getPipeTypeList() {
        ArrayList<String> pipeSizes = new ArrayList<>();
        pipeSizes = mpcDBHelper.getListofThis("Pipe_Type", "Pipe", new String[] {}, new String[] {}, true);
        pipeSizes.add(0, BLANK_SELECTION);
        return pipeSizes;
    }

    public ArrayList<String> getMaterialsList(String Pipe_Type) {
        ArrayList<String> results = new ArrayList<>();
        results = mpcDBHelper.getListofThis("Material", "Parms", new String[] {"Pipe_Type"}, new String[] {Pipe_Type}, true);
        results.add(0, BLANK_SELECTION);
        return results;
    }

    public void setInternalValues(String PipeType, String PipeSize, String PipeWeight, String PipeMaterial) {
        mPipeType = PipeType;
        mPipeSize = PipeSize;
        mPipeMaterial = PipeMaterial;
        mPipeWeight = PipeWeight;
        mPipeID = getPipeID(PipeType, PipeSize, PipeWeight);
    }

    public String getPipeID(String PipeType, String PipeSize, String PipeWeight){
        String[] pipeID;
        pipeID = mpcDBHelper.getThis("_id", "Pipe", new String[] {"Pipe_Type", "Pipe_OD_in", "Weight_ppf"},
                new String[] {PipeType, PipeSize, PipeWeight});
        return mpcDBHelper.returnCheck(pipeID);
    }


    public ArrayList<String> getPipeWeightsList(String Pipe_Type, String Pipe_OD) {
        ArrayList<String> pipeWeights = new ArrayList<>();
        pipeWeights = mpcDBHelper.getListofThis("Weight_ppf", "Pipe", new String[] {"Pipe_Type", "Pipe_OD_in"}, new String[] {Pipe_Type, Pipe_OD}, true);
        pipeWeights.add(0, BLANK_SELECTION);
        return pipeWeights;
    }

    public String getPipeDrift() {
        String[] pipeID;
        pipeID = mpcDBHelper.getThis("Drift_ID_in", "Pipe",
                new String[] {"Pipe_Type", "Pipe_OD_in"}, new String[] {mPipeType, mPipeSize});
        return mpcDBHelper.returnCheck(pipeID);
    }

    public String getMaxHardness() {
        String[] maxHardness;
        maxHardness = mpcDBHelper.getThis("Max_Hardness_HRC", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(maxHardness);
    }

    public String getMinYieldStrength() {
        String[] minYieldStrength;
        minYieldStrength = mpcDBHelper.getThis("Min_Yield_Strength_psi", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(minYieldStrength);
    }

    public String getMaxYieldStrength() {
        String[] maxYieldStrength;
        maxYieldStrength = mpcDBHelper.getThis("Max_Yield_Strength_psi", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(maxYieldStrength);
    }

    public String getTensileStrength() {
        String[] tensileStrength;
        tensileStrength = mpcDBHelper.getThis("Tensile_Strength_psi", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(tensileStrength);
    }

    public String getChromeContent() {
        String[] chromeContent;
        chromeContent = mpcDBHelper.getThis("Chrome_Content_Percent", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(chromeContent);
    }

    public String getMotorSpeed() {
        String[] motorSpeed;
        motorSpeed = mpcDBHelper.getThis("Motor_Speed_rpm", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(motorSpeed);
    }

    public String getFeedRate() {
        String[] feedRate;
        feedRate = mpcDBHelper.getThis("Feed_Rate_mm_per_min", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        return mpcDBHelper.returnCheck(feedRate);
    }

    public String getBladeSize() {
        String[] bladeID;
        String[] bladeSize;
        bladeID = mpcDBHelper.getThis("Blade_ID", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        if (bladeID.length > 0) {
            bladeSize = mpcDBHelper.getThis("Blade_Size_mm", "BladeS",
                    new String[]{"_id"}, new String[]{bladeID[0]});
            return mpcDBHelper.returnCheck(bladeSize);
        }
        return "NULL";
    }

    public String getBladeType() {
        String[] bladeTypeID;
        String[] bladeType;
        bladeTypeID = mpcDBHelper.getThis("Blade_Type_ID", "Parms",
                new String[] {"Material"}, new String[] {mPipeMaterial});
        if (bladeTypeID.length > 0) {
            bladeType = mpcDBHelper.getThis("Blade_Type", "Blade_Type",
                    new String[]{"_id"}, new String[]{bladeTypeID[0]});
            return mpcDBHelper.returnCheck(bladeType);
        }
        return "NULL";
    }

    public String getMaxCut() {
        String[] bladeID;
        String[] bladeCut;
        bladeID = mpcDBHelper.getThis("Blade_ID", "Parms",
                new String[] {"_id"}, new String[] {mPipeID});
        if (bladeID.length > 0) {
            bladeCut = mpcDBHelper.getThis("Max_Blade_Cut", "BladeS ",
                    new String[]{"_id"}, new String[]{bladeID[0]});
            return mpcDBHelper.returnCheck(bladeCut);
        }
        return "NULL";
    }

    public String getClampID() {
        String[] clampID;
        clampID = mpcDBHelper.getThis("Setup_Clamp_1", "Pipe",
                new String[] {"_id"}, new String[] {mPipeID});
        return mpcDBHelper.returnCheck(clampID);
        }

    public String getClampSetup() {
        String[] clampSetup;
        String clampID;
        clampID = getClampID();
        if (!clampID.equals("NULL")) {
            clampSetup = mpcDBHelper.getThis("Setup", "ClampSetup",
                    new String[]{"_id"}, new String[]{clampID});
            if (clampSetup.length > 0)
                return clampSetup[0];
        }
        return "NULL";
    }

    public String getClampTopPartNo() {
        String clampID;
        String[] clampTopPartNo;
        clampID = getClampID();
        if (!clampID.equals("NULL")) {
            clampTopPartNo = mpcDBHelper.getThis("TopPartNo", "ClampSetup",
                    new String[]{"_id"}, new String[]{clampID});
            if (clampTopPartNo.length > 0)
                return clampTopPartNo[0];
        }
        return "NULL";
    }

    public String getClampBotPartNo() {
        String clampID;
        String[] clampBotPartNo;
        clampID = getClampID();
        if (!clampID.equals("NULL")) {
            clampBotPartNo = mpcDBHelper.getThis("BotPartNo", "ClampSetup",
                    new String[]{"_id"}, new String[]{clampID});
            if (clampBotPartNo.length > 0)
                return clampBotPartNo[0];
        }
        return "NULL";
    }
    public String getCuttingHeadID() {
        String[] cuttingHeadID;
        cuttingHeadID = mpcDBHelper.getThis("Setup_Saw_Disk_1", "Pipe",
                new String[] {"_id"}, new String[] {mPipeID});
        return mpcDBHelper.returnCheck(cuttingHeadID);

    }

    public String getCuttingHeadSetup() {
        String cuttingHeadID;
        String[] cuttingHeadSetupNo;
        if (!(cuttingHeadID = getCuttingHeadID()).equals("NULL")) {
            cuttingHeadSetupNo = mpcDBHelper.getThis("Size_mm", "cuttingHeads ",
                    new String[]{"_id"}, new String[]{cuttingHeadID});
            if (cuttingHeadSetupNo.length > 0)
                return cuttingHeadSetupNo[0];
        }
        return "NULL";
    }

    public String getMaxToolOD() {
        String cuttingHeadID = getCuttingHeadID();
        String MaxCuttingHeadOD = getMaxODCuttingHead(cuttingHeadID);
        String clampSetupID = getClampID();
        String MaxClampOD = getMaxClampSetupToolOD(clampSetupID);
        if (!MaxCuttingHeadOD.equals("NULL") && !MaxClampOD.equals("NULL")) {
            if (Float.valueOf(MaxCuttingHeadOD) >= Float.valueOf(MaxClampOD))
                return MaxCuttingHeadOD; //TODO: does not enter here!! Check next
            else
                return MaxClampOD;
        }
        return "NULL";
    }

    private String getMaxODCuttingHead(String cuttingHeadID) {
        String[] maxCuttingHeadOD;
        maxCuttingHeadOD = mpcDBHelper.getThis("Maximum_Head_OD", "cuttingHeads ",
                new String[]{"_id"}, new String[]{cuttingHeadID});
        return (mpcDBHelper.returnCheck(maxCuttingHeadOD));
    }


    private String getMaxClampSetupToolOD(String clampSetupID) {
        String[] maxClampSetupOD;
        maxClampSetupOD = mpcDBHelper.getThis("MaxODin", "ClampSetup",
                new String[]{"_id"}, new String[]{clampSetupID});
        return (mpcDBHelper.returnCheck(maxClampSetupOD));
    }


    public String getMaxBladeCut() {
        String[] bladeID = mpcDBHelper.getThis("Blade_id", "Parms",
                new String[]{"Material"}, new String[]{mPipeMaterial});
        String[] maxBladeCut = mpcDBHelper.getThis("Max_Blade_Cut", "BladeS",
                new String[]{"_id"}, new String[]{bladeID[0]});
        return (mpcDBHelper.returnCheck(maxBladeCut));
    }

    /*private String returnCheck(String[] queryReturn){
        if(queryReturn.length >0)
            return queryReturn[0];
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        String calling = stackTraceElements[stackTraceElements.length].getMethodName();
        Log.d("DB Return Check, ", calling + "@ returnCheck: Expected single return, returned multiple.");
        return "NULL";
    }*/


}
