package com.bhge.wirelineassistant;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
/**
 * Created by xicko on 10/13/17.
 */

public class CustomOnItemSelectedListener implements OnItemSelectedListener {


    private String selection;
    private AdapterView<?> grandParent;
    private Button mgoButton;
    SelectionListAdaptor mSelectionListAdaptor;
    CCDataBaseHelper mccDatabaseHelper;

    public CustomOnItemSelectedListener(Button goButton, CCDataBaseHelper ccDatabaseHelper){
        mgoButton = goButton;
        mccDatabaseHelper = ccDatabaseHelper;}

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        grandParent = (AdapterView) parent.getParent().getParent();
        mSelectionListAdaptor = (SelectionListAdaptor) grandParent.getAdapter();

        selection = parent.getItemAtPosition(pos).toString();
        CCActivity.modifyListOnSelection(mgoButton, grandParent.getPositionForView(view), selection, mSelectionListAdaptor, mccDatabaseHelper);
    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0){
        //Do nothing
    }

}
