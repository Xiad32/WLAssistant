package com.bhge.wirelineassistant;

import android.content.Context;
import android.graphics.Typeface;


/**
 * Created by xicko on 10/16/17.
 */

public class FontStyle {
    private static Typeface tfFont = null;

    public static Typeface getTypeface(Context context) {

        if (tfFont == null) {
            try {
                tfFont = Typeface.createFromAsset(context.getAssets(), "inspira_regular2.ttf");
            } catch (Exception e) {
                return null;
            }
        }
        return tfFont;
    }
}
