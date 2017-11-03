package com.bhge.wirelineassistant;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import static com.bhge.wirelineassistant.R.id.goButton;

/**
 * Created by xicko on 10/11/17.
 */

public class SelectionListAdaptor extends BaseAdapter{

    private Context mContext;
    private ArrayList<SelectionNodeDetails> mSelectionNodes;
    private LayoutInflater mInflater;
    private Button mgoButton;
    private CCDataBaseHelper mccDatabaseHelper;

    public SelectionListAdaptor(Context c, ArrayList<SelectionNodeDetails> selectionNodes, Button goButton,
                                CCDataBaseHelper ccDatabaseHelper) {
        mContext = c;
        mSelectionNodes = selectionNodes;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mgoButton = goButton;
        mccDatabaseHelper = ccDatabaseHelper;
    }

    public int getCount() {
        return mSelectionNodes.size();
    }

    public Object getItem(int position) {
            return mSelectionNodes.get(position);
        }

    public long getItemId(int position) {
            return position;
        }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.selection_node, null);
        TextView textSelection = (TextView) view.findViewById(R.id.selectionText);
        final SelectionNodeDetails currentNode = mSelectionNodes.get(position);
        textSelection.setText(currentNode.getSelectionText());
        Spinner selectionList = view.findViewById(R.id.selectionList);
        ArrayAdapter <String> selectionListAdapter = new
                ArrayAdapter<String> (mContext, android.R.layout.simple_spinner_dropdown_item, currentNode.getSelectionList());
        selectionList.setAdapter(selectionListAdapter);
        selectionList.setOnItemSelectedListener(new CustomOnItemSelectedListener(mgoButton, mccDatabaseHelper));
        return view;
        }
}