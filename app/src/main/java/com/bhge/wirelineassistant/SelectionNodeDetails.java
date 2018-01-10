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
    private int spinnerSelectionPos = 0;
    private int boolOption = BOOL_NULL;
    public static final int BOOL_YES = 867;
    public static final int BOOL_NO = 869;
    public static final int BOOL_NULL = 868;
    private String boolOptionText = "";
    private String insertedText = "Insert Value";

    //Getters:
    public String getSelectionText() {return selectionText;}
    public ArrayList<String> getSelectionList() { return selectionList;}
    public boolean isSelectionAList() {return isSelectionAList;} //Otherwise Text
    public String getEntrySelection() {return  entrySelection;}
    public int getBoolOption() {return boolOption;}
    public String getBoolOptionText() {return boolOptionText;}
    public int getSpinnerSelectionPos() {return spinnerSelectionPos;}
    public String getInsertedText() {return insertedText;}

    //Setters
    public void setSelectionText (String text) {selectionText = text;}
    public void setSelectionList (ArrayList<String> list) {selectionList = list;}
    public void setSelectionAsList(boolean option) {isSelectionAList= option;}
    public void setSelectionEntry (String text) {
        entrySelection = text;
        spinnerSelectionPos = selectionList.indexOf(text);
        if (spinnerSelectionPos == -1) spinnerSelectionPos = 0;}
    public void setBoolOption (Boolean option) { if (option) boolOption = BOOL_YES; else boolOption = BOOL_NO;}
    public void setBoolOptionText(String optionText) {boolOptionText = optionText;}
    public void setInsertedText(String InsertedText) {insertedText = InsertedText;}

}
