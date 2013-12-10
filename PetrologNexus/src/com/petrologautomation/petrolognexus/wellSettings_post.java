package com.petrologautomation.petrolognexus;


import android.graphics.Color;
import android.util.Log;
import android.widget.EditText;
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
    TextView settings_t4TV;
    TextView settings_v4TV;
    TextView settings_t5TV;
    TextView settings_v5TV;

    public wellSettings_post(MainActivity myActivity){

        myAct = myActivity;
        settings_t1TV = (TextView)myAct.findViewById(R.id.settings_t1TV);
        settings_v1TV = (TextView)myAct.findViewById(R.id.settings_v1TV);
        settings_t2TV = (TextView)myAct.findViewById(R.id.settings_t2TV);
        settings_v2TV = (TextView)myAct.findViewById(R.id.settings_v2TV);
        settings_t4TV = (TextView)myAct.findViewById(R.id.settings_t4TV);
        settings_v4TV = (TextView)myAct.findViewById(R.id.settings_v4TV);
        settings_t5TV = (TextView)myAct.findViewById(R.id.settings_t5TV);
        settings_v5TV = (TextView)myAct.findViewById(R.id.settings_v5TV);

    }

    public void post() {

        /* Format Pump Up Setting */
        int tempInt = MainActivity.PetrologSerialCom.getPumpUpSetting();
        String data = String.valueOf(tempInt);
        String title = myAct.getString(R.string.pump_up_setting);
        settings_t1TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (tempInt > 0) {
            /* No error found */
            settings_v1TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v1TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }

        /* Format Pump Off Strokes Setting*/
        tempInt = MainActivity.PetrologSerialCom.getPumpOffStrokesSetting();
        data = String.valueOf(tempInt);
        title = myAct.getString(R.string.pump_off_setting);
        settings_t2TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (tempInt > 0) {
            /* No error found */
            settings_v2TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v2TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }

        /* Format Current Timeout Setting*/
        tempInt = MainActivity.PetrologSerialCom.getCurrentTimeoutSetting();
        data = String.valueOf(tempInt)+" min";
        title = myAct.getString(R.string.timeout_setting);
        settings_t4TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (tempInt > 0) {
            /* No error found */
            settings_v4TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v4TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }

        /* Format % Fillage Setting*/
        data = MainActivity.PetrologSerialCom.getAutomaticTOSetting();
        title = myAct.getString(R.string.auto_timeout_setting);
        settings_t5TV.setText(StringFormatTitle.format(title,Color.BLACK,1f));
        if (data.contains("Yes")||data.contains("No")) {
            /* No error found */
            settings_v5TV.setText(StringFormatValue.format(myAct,data,Color.BLUE,1.2f,false));
        }
        else {
            /* Data error */
            settings_v5TV.setText(StringFormatValue.format(myAct,data,Color.GRAY,1.2f,true));
        }
    }
}
