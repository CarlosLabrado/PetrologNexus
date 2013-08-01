package com.petrologautomation.petrolognexus;


import android.graphics.Color;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellSettings_post {

    MainActivity myAct;
    TextView settings_t1TV;
    TextView settings_v1TV;
    TextView settings_t2TV;
    TextView settings_v2TV;
    TextView settings_t3TV;
    TextView settings_v3TV;

    public wellSettings_post(MainActivity myActivity){

        myAct = myActivity;
        settings_t1TV = (TextView)myAct.findViewById(R.id.settings_t1TV);
        settings_v1TV = (TextView)myAct.findViewById(R.id.settings_v1TV);
        settings_t2TV = (TextView)myAct.findViewById(R.id.settings_t2TV);
        settings_v2TV = (TextView)myAct.findViewById(R.id.settings_v2TV);


    }

    public void post() {

        /* Format Pump Off Strokes Setting*/
        int tempInt = MainActivity.PetrologSerialCom.getPumpOffStrokesSetting();
        String data = String.valueOf(tempInt);
        String title = myAct.getString(R.string.pump_off_setting);
        settings_t1TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (tempInt > 0) {
            /* No error found */
            settings_v1TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v1TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }

        /* Format % Fillage Setting*/
        tempInt = MainActivity.PetrologSerialCom.getFillageSetting();
        title = myAct.getString(R.string.fillage_setting);
        settings_t2TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (tempInt > 0) {
            /* No error found */
            settings_v2TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v2TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }
    }
}
