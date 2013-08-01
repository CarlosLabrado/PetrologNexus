package com.petrologautomation.petrolognexus;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Created by Cesar on 7/23/13.
 */
public class StringFormatTitle {

    public static SpannableString format(String title, int color, float size){

        SpannableString ok = new SpannableString(title);
        ok.setSpan(new ForegroundColorSpan(color),0,title.length(),0);
        /* Changes the size of the text in proportion to its original size */
        ok.setSpan(new RelativeSizeSpan(size),0,title.length(),0);
        return ok;

    }
}
