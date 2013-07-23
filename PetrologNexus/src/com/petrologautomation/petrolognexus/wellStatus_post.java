package com.petrologautomation.petrolognexus;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellStatus_post {

    TextView wellStatus;

    public wellStatus_post (View view){

        wellStatus = (TextView) view;

    }

    public void post() {

        /* Format Well Status */
        String temp = MainActivity.PetrologSerialCom.getWellStatus();
        SpannableString ws = new SpannableString("Well Status: "+temp);
        if (temp.contains("On")){
            ws.setSpan(new ForegroundColorSpan(Color.BLUE),13,15,0);
        /* Changes the size of the text in proportion to its original size */
            //ws.setSpan(new RelativeSizeSpan(1f),13,15,0);
            // wellStatus.setText(ws);
        }
        else if (temp.contains("Off")){
            ws.setSpan(new ForegroundColorSpan(Color.RED),13,16,0);
        /* Changes the size of the text in proportion to its original size */
            //ws.setSpan(new RelativeSizeSpan(1.5f),13,16,0);
        }
        wellStatus.setText(ws);
        wellStatus.append("\n");
        /* Format Pump Off */
        temp = MainActivity.PetrologSerialCom.getPumpOffStatus();
        SpannableString po = new SpannableString("Pump Off: "+temp);
        if (temp.contains("Normal")){
            po.setSpan(new ForegroundColorSpan(Color.BLUE),10,16,0);
         /* Changes the size of the text in proportion to its original size */
            //  po.setSpan(new RelativeSizeSpan(2f),10,16,0);
        }
        else if (temp.contains("Pump Off")){
            po.setSpan(new ForegroundColorSpan(Color.RED),10,18,0);
        /* Changes the size of the text in proportion to its original size */
            //po.setSpan(new RelativeSizeSpan(2f),10,18,0);
        }
        wellStatus.append(po);
        wellStatus.append("\n");
        /* Format Pump Off Strokes*/
        int tempint = MainActivity.PetrologSerialCom.getPumpOffStrokes();
        SpannableString poS = new SpannableString("Pump Strokes: "+tempint);
        if(String.valueOf(tempint).length()==1){
            poS.setSpan(new ForegroundColorSpan(Color.BLUE),14,15,0);
        }else if(String.valueOf(tempint).length()==2){
            poS.setSpan(new ForegroundColorSpan(Color.BLUE),14,16,0);
        }else if (String.valueOf(tempint).length()==3){
            poS.setSpan(new ForegroundColorSpan(Color.BLUE),14,17,0);
        }

        wellStatus.append(poS);
        wellStatus.append("\n");
        /* Format Fillage*/
        tempint = MainActivity.PetrologSerialCom.getFillageSetting();
        SpannableString fill = new SpannableString("Fillage: "+tempint+" %");
        fill.setSpan(new ForegroundColorSpan(Color.BLUE),9,11,0);
        wellStatus.append(fill);
        wellStatus.append("\n");
    }
}
