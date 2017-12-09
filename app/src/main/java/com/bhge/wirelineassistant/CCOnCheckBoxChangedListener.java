package com.bhge.wirelineassistant;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;

/**
 * Created by xicko on 12/3/17.
 */

class CCOnCheckBoxChangedListener implements android.view.View.OnClickListener {
    private String selection;
    private boolean checkBoxSelection;
    private AdapterView<?> adapterView;
    private Button mgoButton;
    private View node;
    SelectionListAdaptor mSelectionListAdaptor;
    CCDataBaseHelper mccDatabaseHelper;

    public CCOnCheckBoxChangedListener(Button goButton, CCDataBaseHelper ccDatabaseHelper){
        mgoButton = goButton;
        mccDatabaseHelper = ccDatabaseHelper;}

    @Override
    public void onClick(View view) {
        checkBoxSelection = ((CheckBox) view).isChecked();
        adapterView = (AdapterView) view.getParent().getParent().getParent();
        mSelectionListAdaptor = (SelectionListAdaptor) adapterView.getAdapter();
        node = (View) view.getParent().getParent();
        CCActivity.modifyListOnSelection( adapterView.getPositionForView(node),
                mSelectionListAdaptor, mccDatabaseHelper, checkBoxSelection); //special overload to change list options
    }
}
