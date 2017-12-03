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
    private int boolOption = BOOL_NULL;
    public static final int BOOL_YES = 867;
    public static final int BOOL_NO = 869;
    public static final int BOOL_NULL = 868;
    private String boolOptionText;

    public String getSelectionText() {return selectionText;}
    public ArrayList<String> getSelectionList() { return selectionList;}
    public boolean isSelectionAList() {return isSelectionAList;}
    public String getEntrySelection() {return  entrySelection;}
    public int getBoolOption() {return boolOption;}
    public String getBoolOptionText() {return boolOptionText;}

    public void setSelectionText (String text) {selectionText = text;}
    public void setSelectionList (ArrayList<String> list) {selectionList = list;}
    public void setSelectionList (boolean option) {isSelectionAList= option;}
    public void setSelectionEntry (String text) {entrySelection = text;}
    public void setBoolOption (Boolean option) { if (option) boolOption = BOOL_YES; else boolOption = BOOL_NO;}
    public void setBoolOptionText(String optionText) {boolOptionText = optionText;}


}
