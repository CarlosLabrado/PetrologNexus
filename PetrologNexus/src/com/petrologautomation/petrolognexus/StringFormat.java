package com.petrologautomation.petrolognexus;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;

/**
 * Created by Cesar on 7/23/13.
 */
public class StringFormat {

    public static SpannableString format(Context myContext, String title, String data, int color, Boolean err){

        if (err){
            /* Error */
            SpannableString e = new SpannableString(title+myContext.getString(R.string.n_a));
            e.setSpan(new ForegroundColorSpan(Color.GRAY),title.length(),title.length()
                    +myContext.getString(R.string.n_a).length(),0);
            e.setSpan(new RelativeSizeSpan(1.2f), title.length(),title.length()
                    +myContext.getString(R.string.n_a).length(),0);
            return e;

        }
        else{
            /* Data Ok */
            SpannableString ok = new SpannableString(title+data);
            ok.setSpan(new ForegroundColorSpan(color),title.length(),title.length()
                    +data.length(),0);
            /* Changes the size of the text in proportion to its original size */
            ok.setSpan(new RelativeSizeSpan(1.2f), title.length(),title.length()
                    +data.length(),0);
            return ok;
        }

    }
}
