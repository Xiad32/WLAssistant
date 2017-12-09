package com.bhge.wirelineassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class displaySetupActivity extends AppCompatActivity {
    public ListView resultsListView;
    public ArrayList<Tuple> resultsNodeDetails;
    private ResultsListAdaptor mResultsListAdaptor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_setup);

        resultsNodeDetails = new ArrayList<>();
        mResultsListAdaptor = new ResultsListAdaptor(this, resultsNodeDetails);
        commonLibraries.parseDisplayList(resultsNodeDetails, getIntent());
        resultsListView = (ListView) findViewById(R.id.resultsListDisplay);
        resultsListView.setAdapter(mResultsListAdaptor);
    }



}
