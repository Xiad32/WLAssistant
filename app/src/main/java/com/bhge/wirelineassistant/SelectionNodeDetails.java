package com.bhge.wirelineassistant;

import java.util.ArrayList;

/**
 * Created by xicko on 10/12/17.
 */

public class SelectionNodeDetails {
    private String selectionText;
    private ArrayList<String> selectionList;
    boolean isSelectionAList;
    private String entrySelection;

    public String getSelectionText() {return selectionText;}
    public ArrayList<String> getSelectionList() { return selectionList;}
    public boolean isSelectionAList() {return isSelectionAList;}
    public String getEntrySelection() {return  entrySelection;}

    public void setSelectionText (String text) {selectionText = text;}
    public void setSelectionList (ArrayList<String> list) {selectionList = list;}
    public void setSelectionList (boolean option) {isSelectionAList= option;}
    public void setSelectionEntry (String text) {entrySelection = text;}


}
