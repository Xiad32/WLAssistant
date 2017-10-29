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
        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        resultsNodeDetails = new ArrayList<>();
        mResultsListAdaptor = new ResultsListAdaptor(this, resultsNodeDetails);
        parseDisplayList(resultsNodeDetails, getIntent());
        resultsListView = (ListView) findViewById(R.id.resultsListDisplay);
        resultsListView.setAdapter(mResultsListAdaptor);

    }

    private void parseDisplayList(ArrayList<Tuple> resultsNodeDetails, Intent callingIntent){
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

}
