package com.bhge.wirelineassistant;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by xicko on 10/27/17.
 */

class ResultsListAdaptor extends BaseAdapter {
    private Context mContext;
    private ArrayList<Tuple> mResultsNodes;
    private LayoutInflater mInflater;

    public ResultsListAdaptor(Context c, ArrayList<Tuple> resultsNodeDetails ) {
        mContext = c;
        mResultsNodes = resultsNodeDetails;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public int getCount() {
        return mResultsNodes.size();
    }


    public Object getItem(int position) {
        return mResultsNodes.get(position);
    }


    public long getItemId(int position) {
        return position;
    }


    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mInflater.inflate(R.layout.results_display_node, null);
        TextView textSelection = (TextView) view.findViewById(R.id.titleText);
        TextView resultsSelection = (TextView) view.findViewById(R.id.results);
        final Tuple currentNode = mResultsNodes.get(position);
        textSelection.setText(currentNode.title);
        resultsSelection.setText(currentNode.result);
        return view;
    }
}
