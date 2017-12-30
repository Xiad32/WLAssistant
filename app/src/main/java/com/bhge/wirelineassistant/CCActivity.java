//Assumptions:
//  - All pipe size & weight entries are unique
//  - All part nos start with a 'F'



package com.bhge.wirelineassistant;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.util.ArrayList;

public class CCActivity extends AppCompatActivity {


    private CCDataBaseHelper ccDBHelper;
    public static ListView selectionListView;
    public static ArrayList<SelectionNodeDetails> selectionNodeDetails;// = new ArrayList<>();
    private SelectionListAdaptor mSelectionListAdaptor; // = new SelectionListAdaptor(this, selectionNodeDetails);
    private static final String[] nodesTitleText =
            {"Pipe Size:", "Pipe Details:", "Hydrostatic Pressure",
             "Slip Sub", "Cuttting Head", "Cylinder Data",
             "Top Propellant", "Bot Propellant", "Catalyst", "Choke", "Burst Disc", "Ignition Sub"};
    private final static int ALL_SELECTION = 3; //Number of nodes that needs selection
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
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Tuple> results = createResultsData(ccDBHelper);
                Intent intent = new Intent (CCActivity.this, displaySetupActivity.class);
                commonLibraries.addResultsToIntent(intent, results);
                startActivity(intent);
            }
        });

        //Load Database:
        ccDBHelper = new CCDataBaseHelper(this);

        selectionNodeDetails = new ArrayList<>();
        mSelectionListAdaptor = new SelectionListAdaptor(this, selectionNodeDetails, goButton, ccDBHelper);
        enabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDarkGray);
        disabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLightGrey);

        SelectionNodeDetails thisItem = new SelectionNodeDetails();
        thisItem.setSelectionText(nodesTitleText[0]);
        thisItem.setSelectionList(ccDBHelper.getPipeSizeList());
        thisItem.setSelectionList(true);
        selectionNodeDetails.add(thisItem);

        selectionListView = (ListView) findViewById(R.id.selectionlistview);
        selectionListView.setAdapter(mSelectionListAdaptor);


    }

    public static void modifyListOnSelection(Button goButton, int entryModified, String lastSelection, SelectionListAdaptor mSelectionListAdaptor,
                                             Object ccDBHelper)
    {
        CCDataBaseHelper mCCDataBaseHelper = (CCDataBaseHelper) ccDBHelper;
        if(!lastSelection.equals(CCActivity.selectionNodeDetails.get(entryModified).getEntrySelection())) {
            if (entryModified < mSelectionListAdaptor.getCount() && !lastSelection.equals(CCActivity.selectionNodeDetails.get(entryModified).getEntrySelection())) {
                //deflate and free higher entries:
                for (int i = selectionNodeDetails.size() - 1; i > entryModified; i--)
                    selectionNodeDetails.remove(i);
            }
            //add next nodes
            if (mSelectionListAdaptor.getCount() == ALL_SELECTION && !lastSelection.equals(BLANK_SELECTION)) {
                goButton.setTextColor(CCActivity.enabledTextColor);
                goButton.setEnabled(true);
                CCActivity.selectionNodeDetails.get(entryModified).setSelectionEntry(lastSelection);
            }
            else {
                goButton.setTextColor(CCActivity.disabledTextColor);
                goButton.setEnabled(false);
                if (!lastSelection.equals(BLANK_SELECTION)) {
                    CCActivity.selectionNodeDetails.get(entryModified).setSelectionEntry(lastSelection);
                    SelectionNodeDetails newNode = new SelectionNodeDetails();
                    newNode.setSelectionText(nodesTitleText[entryModified + 1]);
                    switch (entryModified) {
                        case 0:
                            newNode.setSelectionList(mCCDataBaseHelper.getPipeODsFromSize(lastSelection));
                            break;
                        case 1:
                            String size = CCActivity.selectionNodeDetails.get(0).getEntrySelection();
                            String weight = mCCDataBaseHelper.getVarFromParsedRow("Weight", CCActivity.selectionNodeDetails.get(1).getEntrySelection());
                            ArrayList<String> loadingTable = mCCDataBaseHelper.getLoadingTableForPipe(size, weight);
                            if (loadingTable.size() > 1) {
                                newNode.setBoolOptionText("Coil Tubing?");
                                newNode.setBoolOption(false);
                            }
                            newNode.setSelectionList(mCCDataBaseHelper.getHydStatPresOptions(loadingTable.get(0)));
                            break;
                    }
                    newNode.setSelectionList(false);
                    selectionNodeDetails.add(newNode);
                    mSelectionListAdaptor.notifyDataSetInvalidated();
                    Log.d("CC_ADAPTOR_LIST", "modifyListOnSelection: Added new Node");
                }
            }
        }
    }

    public static void modifyListOnSelection(int entryModified, SelectionListAdaptor mSelectionListAdaptor,
                                             Object ccDBHelper, Boolean checkBox)
    {
        CCDataBaseHelper mCCDataBaseHelper = (CCDataBaseHelper) ccDBHelper;
        String size = CCActivity.selectionNodeDetails.get(0).getEntrySelection();
        String weight = mCCDataBaseHelper.getVarFromParsedRow("Weight", CCActivity.selectionNodeDetails.get(1).getEntrySelection());
        ArrayList<String> loadingTable = mCCDataBaseHelper.getLoadingTableForPipe(size, weight);
        if (checkBox) {
            selectionNodeDetails.get(entryModified).setSelectionList(mCCDataBaseHelper.getHydStatPresOptions(loadingTable.get(1)));
        }
        else {
            selectionNodeDetails.get(entryModified).setSelectionList(mCCDataBaseHelper.getHydStatPresOptions(loadingTable.get(0)));
        }
        selectionNodeDetails.get(entryModified).setSelectionEntry(BLANK_SELECTION);
        selectionNodeDetails.get(entryModified).setBoolOption(checkBox);
        mSelectionListAdaptor.notifyDataSetInvalidated();
    }

    private static ArrayList<Tuple> createResultsData(CCDataBaseHelper ccDBHelper)
    {
        ArrayList<Tuple> resultsToDislay = new ArrayList();
        //Display Selection:
        for (int i = 0; i<ALL_SELECTION; i++)
            resultsToDislay.add(new Tuple (nodesTitleText[i], selectionNodeDetails.get(i).getEntrySelection()));
        ccDBHelper.setInternalPipeId(selectionNodeDetails.get(0).getEntrySelection(),
                ccDBHelper.getVarFromParsedRow("Weight", selectionNodeDetails.get(1).getEntrySelection()) );
        for (int i= ALL_SELECTION; i< nodesTitleText.length; i++ )
            switch (i - ALL_SELECTION){
                //getSlipSubDetails
                case 0:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getSlipSubDetails()));
                    break;
                //getCuttingHeadDetails
                case 1:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getCuttingHead()));
                    break;
                //get Cyclinder Details
                case 2:
                    ccDBHelper.setInternalLoadingTable(selectionNodeDetails.get(1).getBoolOption() == SelectionNodeDetails.BOOL_YES); //TODO: fix for CT support
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getCyclinderDetails()));
                    break;
                //get TopPropellant
                case 3:
                    ccDBHelper.setInternalHydPres(selectionNodeDetails.get(2).getEntrySelection());
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getTopProp()));
                    break;
                //get BotPropellant
                case 4:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getBotProp()));
                    break;
                //get CatalystSizeNo
                case 5:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getCatSizeNo()));
                    break;
                //get ChokeDia
                case 6:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getChokeDiameter()));
                    break;
                //getBurstDisc
                case 7:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getBurstDisc()));
                    break;
                //get IgnitionSub
                case 8:
                    resultsToDislay.add(new Tuple (nodesTitleText[i], ccDBHelper.getIgnitionLengthAndSub()));
                    break;
            }
        return resultsToDislay;
    }



}
