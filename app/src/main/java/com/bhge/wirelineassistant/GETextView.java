package com.bhge.wirelineassistant;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import com.bhge.wirelineassistant.FontStyle;

/**
 * Created by xicko on 10/16/17.
 */
;

public class GETextView extends android.support.v7.widget.AppCompatTextView {

    private void getFontTypeface(Context context){
        Typeface GEFont = FontStyle.getTypeface(context);
        setTypeface(GEFont);
    }
    public GETextView(Context context) {
        super(context);
        getFontTypeface(context);
    }

    public GETextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getFontTypeface(context);
    }

    public GETextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getFontTypeface(context);
    }

    /*
    public GETextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getFontTypeface(context);
    }*/
}
