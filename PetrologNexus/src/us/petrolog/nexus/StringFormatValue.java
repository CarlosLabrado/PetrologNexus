package us.petrolog.nexus;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;


/**
 * Created by Cesar on 7/23/13.
 */
public class StringFormatValue {

    public static SpannableString format(Context myContext, String data, int color, float size, Boolean err){

        if (err){
            /* Error */
            SpannableString e = new SpannableString(myContext.getString(R.string.n_a));
            e.setSpan(new ForegroundColorSpan(Color.LTGRAY),0,myContext.getString(R.string.n_a).length(),0);
            e.setSpan(new RelativeSizeSpan(1),0,myContext.getString(R.string.n_a).length(), 0);
            return e;

        }
        else{
            /* Data Ok */
            SpannableString ok = new SpannableString(data);
            ok.setSpan(new ForegroundColorSpan(color),0,data.length(),0);
            /* Changes the size of the text in proportion to its original size */
            ok.setSpan(new RelativeSizeSpan(size),0,data.length(),0);
            return ok;
        }

    }
}
