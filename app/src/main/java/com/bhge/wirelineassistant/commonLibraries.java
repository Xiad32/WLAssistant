package com.bhge.wirelineassistant;

import android.content.Intent;
import java.util.ArrayList;

/**
 * Created by xicko on 12/9/17.
 */

public class commonLibraries {
    public static void addResultsToIntent(Intent intent, ArrayList<Tuple> results){
        String allValues = "";
        for (int i = 0; i<results.size(); i++)
        {
            intent.putExtra(results.get(i).title, results.get(i).result);
            allValues = allValues + results.get(i).title + "_";
        }
        intent.putExtra("allValues", allValues);
    }
    public static void parseDisplayList(ArrayList<Tuple> resultsNodeDetails, Intent callingIntent){
        Tuple thisItem;// = new Tuple("","");
        String values = callingIntent.getStringExtra("allValues");
        String[] titles = values.split("_");
        for(String title : titles) {
            String resultSee = callingIntent.getStringExtra(title);
            thisItem = new Tuple("","");
            thisItem.title = title;
            thisItem.result = resultSee;
            resultsNodeDetails.add(thisItem);
        }
    }

    public static String[] getAllSelections(ArrayList<SelectionNodeDetails> mSelectionNodeDetails,
                                            int numberOfEntries){
        String[] entries = new String[numberOfEntries];
        for (int i = 0; i < numberOfEntries; i++){
            entries[i] = mSelectionNodeDetails.get(i).getEntrySelection();
        }
        return entries;
    }
}
