package com.petrologautomation.petrolognexus;

import android.content.Context;
import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellStatus_post {

    MainActivity myAct;
    TextView current_t1TV;
    TextView current_v1TV;
    TextView current_t2TV;
    TextView current_v2TV;
    TextView current_t3TV;
    TextView current_v3TV;

    public wellStatus_post (MainActivity myActivity){

        myAct = myActivity;
        current_t1TV = (TextView)myAct.findViewById(R.id.current_t1TV);
        current_v1TV = (TextView)myAct.findViewById(R.id.current_v1TV);
        current_t2TV = (TextView)myAct.findViewById(R.id.current_t2TV);
        current_v2TV = (TextView)myAct.findViewById(R.id.current_v2TV);
        current_t3TV = (TextView)myAct.findViewById(R.id.current_t3TV);
        current_v3TV = (TextView)myAct.findViewById(R.id.current_v3TV);

    }

    public void post() {

    /* Format Well Status */
        String data = MainActivity.PetrologSerialCom.getWellStatus();
        String title = myAct.getString(R.string.well_status);
        current_t1TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (data.contains("Running")){
            current_v1TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));

        /* Format Pump Off */
            title = myAct.getString(R.string.pump_off);
            current_t2TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
            data = MainActivity.PetrologSerialCom.getPumpOffStatus();
            if (data.contains("No")){
                current_v2TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
            }
            else if (data.contains("Yes")){
                current_v2TV.setText(StringFormatValue.format(myAct,data,Color.RED,1.2f,false));
            }
            else {
                current_v2TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
            }

        /* Format Strokes this Cycle */
            int dataInt = MainActivity.PetrologSerialCom.getStrokesThis();
            data = String.valueOf(dataInt);
            title = myAct.getString(R.string.strokes_this);
            current_t3TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
            if (dataInt > 0){
                current_v3TV.setText(StringFormatValue.format(myAct,data,Color.RED,1.2f,false));
            }
            else {
                current_v3TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
            }
        }
        else if (data.contains("Stopped")){
            current_v1TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));

        /* Format Next Start */
            title = myAct.getString(R.string.next_start);
            current_t2TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
            int min = MainActivity.PetrologSerialCom.getMinNextStart();
            int sec = MainActivity.PetrologSerialCom.getSecNextStart();
            if ((min >= 0 && min != 255) && (sec >= 0 && sec != 255)){
                data = String.format("%02d",min)+":"+String.format("%02d",sec);
                current_v2TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
            }
            else {
                current_v2TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
            }

        /* Format Strokes last Cycle */
            title = myAct.getString(R.string.strokes_last);
            current_t3TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
            int dataInt = MainActivity.PetrologSerialCom.getStrokesLast();
            String data1 = String.valueOf(dataInt);
            if (dataInt > 0){
                current_v3TV.setText(StringFormatValue.format(myAct,data1,Color.BLUE,1.2f,false));
            }
            else {
                current_v3TV.setText(StringFormatValue.format(myAct,data1,Color.GRAY,1.2f,true));
            }
        }
        else {
            data = myAct.getString(R.string.n_a);
            current_v1TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
    }
}
