package com.bhge.wirelineassistant;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;

import static com.bhge.wirelineassistant.CCActivity.modifyListOnSelection;

/**
 * Created by xicko on 10/13/17.
 */

public class CustomOnSpinnerItemSelectedListener implements OnItemSelectedListener {


    private String selection;
    //private boolean checkBoxSelection;
    private AdapterView<?> adapterView;
    private View node;
    private Button mgoButton;
    private Activity mCallingActivity;
    private Object mCallingClass;
    SelectionListAdaptor mSelectionListAdaptor;
    Object mccDatabaseHelper;

    public CustomOnSpinnerItemSelectedListener(Button goButton, Object databaseHelper){
                                                 //Activity callingActivity){
        mgoButton = goButton;
        mccDatabaseHelper = databaseHelper;
        mCallingActivity = null;}

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        //parent is spinner Grandparent LinearLayout GGParent node LinearLayout GGG adapterView
        mCallingActivity = (Activity) parent.getContext();
        mCallingClass = mCallingActivity.getClass();
        adapterView = (AdapterView) parent.getParent().getParent().getParent();
        mSelectionListAdaptor = (SelectionListAdaptor) adapterView.getAdapter();
        node = (View) parent.getParent().getParent();
        selection = parent.getItemAtPosition(pos).toString();
        Log.i("View ID", "onItemSelected: " + adapterView.getPositionForView(node) );
        if (mCallingClass == CCActivity.class)
            CCActivity.modifyListOnSelection(mgoButton, adapterView.getPositionForView(node), selection,
                    mSelectionListAdaptor, mccDatabaseHelper);
        else if(mCallingClass == MPCActivity.class)
            MPCActivity.modifyListOnSelection(mgoButton, adapterView.getPositionForView(node), selection,
                    mSelectionListAdaptor, mccDatabaseHelper);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        //Do nothing
    }

}
