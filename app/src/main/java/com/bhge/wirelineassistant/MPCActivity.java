package com.bhge.wirelineassistant;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MPCActivity extends AppCompatActivity {

    private final static String BLANK_SELECTION = "     ";
    private MPCDataBaseHelper mMPCDBHelper;
    public static ListView mMPCselectionListView;
    public static ArrayList<SelectionNodeDetails> selectionNodeDetails;// = new ArrayList<>();
    private SelectionListAdaptor mSelectionListAdaptor; // = new SelectionListAdaptor(this, selectionNodeDetails);
    private static final String[] nodesTitleText =
            {"Pipe Type", "Pipe OD [in]", "Pipe Weight [lb/ft]",
                    "Pipe Material", "Pipe ID Drift [in]",
                    "Max Hardness HRC", "Min Yield Strength [psi]",
                    "Max Yield Strength [psi]", "Tensile Strength [psi]",
                    "Chrome Content [%]", "Motor Speed [rpm]", "Feed Rate [mm/min]",
                    "Blade Size [mm]", "Blade Type", "Max Blade Cut Thickness [in]",
                    "Ideal Clamp Setup", "Top Clamp Part No", "Bot Clamp Part No",//"Second Choice Clamp",
                    "Ideal Cutting Head", "Max Tool OD in", "Max Pipe Cut", "Min Pipe ID in"};
                    //,"Third Choice Clamp", "First Choice Blade", "Second Choice Blade"};
    private final static int ALL_SELECTION = 4; //All Needed Input
    private Button goButton;
    public static int enabledTextColor;
    public static int disabledTextColor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mpc);
        goButton = (Button) findViewById(R.id.goButton);
        goButton.setTypeface(FontStyle.getTypeface(getApplicationContext()));
        goButton.setTextColor(getResources().getColor(R.color.colorPrimaryLightGrey));
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Tuple> results = createResultsData(mMPCDBHelper);
                Intent intent = new Intent (MPCActivity.this, displaySetupActivity.class);
                commonLibraries.addResultsToIntent(intent, results);
                startActivity(intent);
            }
        });

        //Load Database:
        mMPCDBHelper = new MPCDataBaseHelper(this);

        selectionNodeDetails = new ArrayList<>();
        mSelectionListAdaptor = new SelectionListAdaptor(this, selectionNodeDetails, goButton,  mMPCDBHelper);
        enabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryDarkGray);
        disabledTextColor = ContextCompat.getColor(getBaseContext(), R.color.colorPrimaryLightGrey);

        SelectionNodeDetails thisItem = new SelectionNodeDetails();
        thisItem.setSelectionText(nodesTitleText[0]);
        thisItem.setSelectionList(mMPCDBHelper.getPipeTypeList());
        thisItem.setSelectionList(true);
        selectionNodeDetails.add(thisItem);

        mMPCselectionListView= (ListView) findViewById(R.id.mpcSelectionListView);
        mMPCselectionListView.setAdapter(mSelectionListAdaptor);
    }

    private static ArrayList<Tuple> createResultsData (MPCDataBaseHelper mMPCDBHelper) {
        ArrayList<Tuple> resultsToDisplay = new ArrayList<>();
        String[] entries = commonLibraries.getAllSelections(MPCActivity.selectionNodeDetails, ALL_SELECTION);
        for (int i = 0; i < ALL_SELECTION; i++)
            resultsToDisplay.add(new Tuple(nodesTitleText[i], entries[i]));
        mMPCDBHelper.setInternalValues(entries[0], entries[1], entries[2], entries[3]);

        for (int i = ALL_SELECTION; i < nodesTitleText.length; i++)
            switch (i - ALL_SELECTION) {
                //get
                case 0: //Pipe Drift
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getPipeDrift()));
                    break;
                case 1: //Maximum Hardness
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMaxHardness()));
                    break;
                case 2: //Min Yield Strength
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMinYieldStrength()));
                    break;
                case 3: //Max Yield Strength
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMaxYieldStrength()));
                    break;
                case 4: //Tensile Strength
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getTensileStrength()));
                    break;
                case 5: //Chrome Content %
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getChromeContent()));
                    break;
                case 6: //Recommended Motor Speed
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMotorSpeed()));
                    break;
                case 7: //Recommended Feed Rate
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getFeedRate()));
                    break;
                case 8: //Recommended Blade Size
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getBladeSize()));
                    break;
                case 9: //Recommended Blade Type
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getBladeType()));
                    break;
                case 10: //Max blade cut thickness
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMaxCut()));
                    break;
                case 11: //Recommended Clamp setup
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getClampSetup()));
                    break;
                case 12: //Clamp Top Part No
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getClampTopPartNo()));
                    break;
                case 13: //Clamp Bot Part No
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getClampBotPartNo()));
                    break;
                case 14: //Ideal cutting Head
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getCuttingHeadSetup()));
                    break;
                case 15: //Max Tool OD
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMaxToolOD()));
                    break;
                case 16: //Max Blade Cut
                    resultsToDisplay.add(new Tuple(nodesTitleText[i], mMPCDBHelper.getMaxBladeCut()));
                    break;
            }
        return resultsToDisplay;
    }

    public static void modifyListOnSelection(Button goButton, int entryModified, String lastSelection,
                                             SelectionListAdaptor mSelectionListAdaptor, Object mpcDBHelper)
    {
        MPCDataBaseHelper mMPCDataBaseHelper = (MPCDataBaseHelper) mpcDBHelper;
        if(!lastSelection.equals(MPCActivity.selectionNodeDetails.get(entryModified).getEntrySelection())) {   //Entry modified is not the last. Need to re-select
            if (entryModified < mSelectionListAdaptor.getCount() && !lastSelection.equals(MPCActivity.selectionNodeDetails.get(entryModified).getEntrySelection())) {
                //deflate and free higher entries:
                for (int i = selectionNodeDetails.size() - 1; i > entryModified; i--)
                    selectionNodeDetails.remove(i);
            }
            // Enable Button if selection is complete
            if (mSelectionListAdaptor.getCount() == ALL_SELECTION && !lastSelection.equals(BLANK_SELECTION)) {
                goButton.setTextColor(MPCActivity.enabledTextColor);
                goButton.setEnabled(true);
                MPCActivity.selectionNodeDetails.get(entryModified).setSelectionEntry(lastSelection);
            } //Otherwise add next nodes
            else {
                //Deactivate button; Not all selection done
                goButton.setTextColor(MPCActivity.disabledTextColor);
                goButton.setEnabled(false);
                if (!lastSelection.equals(BLANK_SELECTION)) {
                    MPCActivity.selectionNodeDetails.get(entryModified).setSelectionEntry(lastSelection);
                    SelectionNodeDetails newNode = new SelectionNodeDetails();
                    newNode.setSelectionText(nodesTitleText[entryModified + 1]);
                    switch (entryModified) {
                        case 0:
                            newNode.setSelectionList(mMPCDataBaseHelper.getPipeSizeList(lastSelection));
                            break;
                        case 1:
                            newNode.setSelectionList(mMPCDataBaseHelper.getPipeWeightsList(
                                    selectionNodeDetails.get(0).getEntrySelection(), //Pipe Type Selected
                                    selectionNodeDetails.get(1).getEntrySelection()  //Pipe OD Selected
                            ));
                            break;
                        case 2:
                            newNode.setSelectionList(mMPCDataBaseHelper.getMaterialsList(
                                    MPCActivity.selectionNodeDetails.get(0).getEntrySelection() //Get selected pipe type, and filter accordingly
                            ));
                            break;
                    }
                    newNode.setSelectionList(false);
                    selectionNodeDetails.add(newNode);
                    mSelectionListAdaptor.notifyDataSetInvalidated();
                    Log.d("MPC_ADAPTOR_LIST", "modifyListOnSelection: Added new Node");
                }
            }
        }
    }

}
