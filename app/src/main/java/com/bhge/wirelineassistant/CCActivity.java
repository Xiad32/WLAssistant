package com.bhge.wirelineassistant;

import android.content.Context;
import android.database.SQLException;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

public class CCActivity extends AppCompatActivity {


    private DataBaseHelper ccDBHelper;
    public ListView selectionListView;
    public static ArrayList<SelectionNodeDetails> selectionNodeDetails;// = new ArrayList<>();
    private SelectionListAdaptor mSelectionListAdaptor; // = new SelectionListAdaptor(this, selectionNodeDetails);
    private static final String[] selectionPointsText = {"Pipe Size:", "Pipe Details:", "Hydrostatic Pressure"};
    private final static int ALL_SELECTION = 3;
    private Button goButton;
    public static int enabledTextColor;
    public static int disabledTextColor;
    private final static String BLANK_SELECTION = "     ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cc);

        goButton = (Button) findViewById(R.id.goButton);
        goButton.setTypeface(FontStyle.getTypeface(getApplicationContext()));
        goButton.setTextColor(getResources().getColor(R.color.colorPrimaryLightGrey));



        //Load Database:
        ccDBHelper = new DataBaseHelper(this);
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

        selectionNodeDetails = new ArrayList<>();
        mSelectionListAdaptor = new SelectionListAdaptor(this, selectionNodeDetails, goButton, ccDBHelper);
        enabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDarkGray);
        disabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLightGrey);

        SelectionNodeDetails thisItem = new SelectionNodeDetails();
        thisItem.setSelectionText(selectionPointsText[0]);
        thisItem.setSelectionList(ccDBHelper.getPipeSizeList());
        thisItem.setSelectionList(true);
        selectionNodeDetails.add(thisItem);

        selectionListView = (ListView) findViewById(R.id.selectionlistview);
        selectionListView.setAdapter(mSelectionListAdaptor);


    }

    public static void modifyListOnSelection(Button goButton, int entryModified, String lastSelection, SelectionListAdaptor mSelectionListAdaptor,
                                             DataBaseHelper ccDBHelper)
    {
        if (entryModified < mSelectionListAdaptor.getCount())
        {
            //deflate and free higher entries:
            for (int i = selectionNodeDetails.size()-1; i > entryModified; i--)
                selectionNodeDetails.remove(i);
        }
        //add next nodes
        if(mSelectionListAdaptor.getCount() == ALL_SELECTION && !lastSelection.equals(BLANK_SELECTION) )
        {
            goButton.setTextColor(CCActivity.enabledTextColor);
            goButton.setEnabled(true);
        }
        else {
            goButton.setTextColor(CCActivity.disabledTextColor);
            goButton.setEnabled(false);
            if(!lastSelection.equals(BLANK_SELECTION)){
                if (entryModified == 1)
                    lastSelection = parseWeightFromSelection(lastSelection);
                CCActivity.selectionNodeDetails.get(entryModified).setSelectionEntry(lastSelection);
                SelectionNodeDetails newNode = new SelectionNodeDetails();
                newNode.setSelectionText(selectionPointsText[entryModified + 1]);
                switch (entryModified) {
                    case 0:
                        newNode.setSelectionList(ccDBHelper.getPipeODsFromSize(lastSelection));
                        break;
                    case 1:
                        String size = CCActivity.selectionNodeDetails.get(0).getEntrySelection();
                        String weight = CCActivity.selectionNodeDetails.get(1).getEntrySelection();
                        ArrayList<Integer> loadingTable = ccDBHelper.getLoadingTableForPipe(size, weight);
                        newNode.setSelectionList(ccDBHelper.getHydStatPresOptions(loadingTable.get(0)));
                        break;
                }
                newNode.setSelectionList(false);
                selectionNodeDetails.add(newNode);
            }
        }
    }

    private static String parseWeightFromSelection(String s){
        int start = 8; int end;
        end = s.indexOf(" ID");


        return s.substring(start, end);
    }

    private static void createResultsData()
    {
        //getSlipSubDetails

        //getCuttingHeadDetails

        //get CyclinderDetails

        //get TopPropellant

        //get BotPropellant

        //get CatalystSizeNo

        //get ChokeDia

        //getBurstDisc

        //get IgnitionSub





    }


}
