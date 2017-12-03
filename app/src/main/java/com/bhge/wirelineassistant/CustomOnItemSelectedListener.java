package com.bhge.wirelineassistant;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;

/**
 * Created by xicko on 10/13/17.
 */

public class CustomOnItemSelectedListener implements OnItemSelectedListener {


    private String selection;
    private boolean checkBoxSelection;
    private AdapterView<?> grandParent;
    private Button mgoButton;
    SelectionListAdaptor mSelectionListAdaptor;
    CCDataBaseHelper mccDatabaseHelper;

    public CustomOnItemSelectedListener(Button goButton, CCDataBaseHelper ccDatabaseHelper){
        mgoButton = goButton;
        mccDatabaseHelper = ccDatabaseHelper;}

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        grandParent = (AdapterView) parent.getParent().getParent().getParent();
        mSelectionListAdaptor = (SelectionListAdaptor) grandParent.getAdapter();
        if (view instanceof Spinner) { //Change in Selection List
            selection = parent.getItemAtPosition(pos).toString();
            CCActivity.modifyListOnSelection(mgoButton, grandParent.getPositionForView(view), selection,
                    mSelectionListAdaptor, mccDatabaseHelper);
        }
        else if (view instanceof CheckBox) //Change in option Status
        {
            checkBoxSelection = ((CheckBox) view).isChecked();
            CCActivity.modifyListOnSelection(grandParent.getPositionForView(view),
                    mSelectionListAdaptor, mccDatabaseHelper, checkBoxSelection); //special overload to change list options
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        //Do nothing
    }

}
