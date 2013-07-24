package com.petrologautomation.petrolognexus;


import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellSettings_post {

    TextView wellParameters;

    public wellSettings_post(View view){

        wellParameters = (TextView) view;

    }

    public void post(Context myContext) {

        /* Format Pump Off Strokes Setting*/
        int tempInt = MainActivity.PetrologSerialCom.getPumpOffStrokesSetting();
        String title = myContext.getString(R.string.pump_off_setting);
        if (tempInt > 0) {
            /* No error found */
            wellParameters.setText(
                    StringFormat.format(myContext,title,String.valueOf(tempInt),Color.BLUE,false));
            wellParameters.append("\n");
        }
        else {
            /* Data error */
            wellParameters.setText(
                    StringFormat.format(myContext,title,"",Color.GRAY,true));
            wellParameters.append("\n");
        }

        /* Format % Fillage Setting*/
        tempInt = MainActivity.PetrologSerialCom.getFillageSetting();
        title = myContext.getString(R.string.fillage_setting);
        if (tempInt > 0) {
            /* No error found */
            wellParameters.append(
                    StringFormat.format(myContext,title,String.valueOf(tempInt),Color.BLUE,false));
            wellParameters.append("\n");
        }
        else {
            wellParameters.append(
                    StringFormat.format(myContext,title,"",Color.GRAY,true));
            wellParameters.append("\n");
        }
    }
}
