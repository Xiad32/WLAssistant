package com.bhge.wirelineassistant;

import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;

/**
 * Created by xicko on 10/13/17.
 */

public class CCCustomOnSpinnerItemSelectedListener implements OnItemSelectedListener {


    private String selection;
    //private boolean checkBoxSelection;
    private AdapterView<?> adapterView;
    private View node;
    private Button mgoButton;
    SelectionListAdaptor mSelectionListAdaptor;
    CCDataBaseHelper mccDatabaseHelper;

    public CCCustomOnSpinnerItemSelectedListener(Button goButton, CCDataBaseHelper ccDatabaseHelper){
        mgoButton = goButton;
        mccDatabaseHelper = ccDatabaseHelper;}

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //parent is spinner Grandparent LinearLayout GGParent node LinearLayout GGG adapterView
        adapterView = (AdapterView) parent.getParent().getParent().getParent();
        mSelectionListAdaptor = (SelectionListAdaptor) adapterView.getAdapter();
        node = (View) parent.getParent().getParent();
        selection = parent.getItemAtPosition(pos).toString();
        Log.i("View ID", "onItemSelected: " + adapterView.getPositionForView(node) );
        CCActivity.modifyListOnSelection(mgoButton, adapterView.getPositionForView(node), selection,
                    mSelectionListAdaptor, mccDatabaseHelper);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        //Do nothing
    }

}
