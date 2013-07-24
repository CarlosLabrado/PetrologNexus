package com.petrologautomation.petrolognexus;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellStatus_post {

    TextView wellStatus;

    public wellStatus_post (View view){

        wellStatus = (TextView) view;

    }

    public void post(Context myContext) {

    /* Format Well Status */
        String data = MainActivity.PetrologSerialCom.getWellStatus();
        String title = myContext.getString(R.string.well_status);
        if (data.contains("Running")){
            wellStatus.setText(StringFormat.format(myContext,title,data,Color.BLUE,false));
            wellStatus.append("\n");

        /* Format Pump Off */
            title = myContext.getString(R.string.pump_off);
            data = MainActivity.PetrologSerialCom.getPumpOffStatus();
            if (data.contains("No")){
                wellStatus.append(StringFormat.format(myContext,title,data,Color.BLUE,false));
                wellStatus.append("\n");
            }
            else if (data.contains("Yes")){
                wellStatus.append(StringFormat.format(myContext,title,data,Color.RED,false));
                wellStatus.append("\n");
            }
            else {
                wellStatus.append(StringFormat.format(myContext,title,"",Color.GRAY,true));
                wellStatus.append("\n");
            }

        /* Format Strokes this Cycle */
            title = myContext.getString(R.string.strokes_this);
            int dataInt = MainActivity.PetrologSerialCom.getStrokesThis();
            if (dataInt > 0){
                wellStatus.append(StringFormat.format(myContext,title,String.valueOf(dataInt),Color.BLUE,false));
            }
            else {
                wellStatus.append(StringFormat.format(myContext, title, "", Color.GRAY, true));
            }
        }
        else if (data.contains("Stopped")){
            wellStatus.setText(StringFormat.format(myContext,title,data,Color.BLUE,false));
            wellStatus.append("\n");

        /* Format Next Start */
            title = myContext.getString(R.string.next_start);
            int min = MainActivity.PetrologSerialCom.getMinNextStart();
            int sec = MainActivity.PetrologSerialCom.getSecNextStart();
            if ((min >= 0 && min != 255) && (sec >= 0 && sec != 255)){
                data = String.format("%02d",min)+":"+String.format("%02d",sec);
                wellStatus.append(StringFormat.format(myContext,title,data,Color.BLUE,false));
                wellStatus.append("\n");
            }
            else {
                wellStatus.append(StringFormat.format(myContext,title,"",Color.GRAY,true));
                wellStatus.append("\n");
            }

        /* Format Strokes this Cycle */
            title = myContext.getString(R.string.strokes_last);
            int dataInt = MainActivity.PetrologSerialCom.getStrokesLast();
            if (dataInt > 0){
                wellStatus.append(StringFormat.format(myContext,title,String.valueOf(dataInt),Color.BLUE,false));
            }
            else {
                wellStatus.append(StringFormat.format(myContext,title,"",Color.GRAY,true));
            }
        }
        else {
            wellStatus.setText(StringFormat.format(myContext,title,"",Color.GRAY,true));
            wellStatus.append("\n");
        }
    }
}
