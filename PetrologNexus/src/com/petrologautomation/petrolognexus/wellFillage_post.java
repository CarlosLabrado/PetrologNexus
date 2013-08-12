package com.petrologautomation.petrolognexus;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellFillage_post {

    MainActivity myAct;
    TextView currentFillage;
    TextView fillageSetting;
    TextView pumpOffDistance;

    public wellFillage_post(MainActivity myActivity){

        myAct = myActivity;

        currentFillage = (TextView)myAct.findViewById(R.id.fillage_v1TV);
        fillageSetting = (TextView)myAct.findViewById(R.id.fillage_v2TV);
        pumpOffDistance = (TextView)myAct.findViewById(R.id.fillage_v3TV);


    }

    public void post() {
        int fillSetting = MainActivity.PetrologSerialCom.getFillageSetting();
        int currentFill = MainActivity.PetrologSerialCom.getCurrentFillage();
        if (currentFill>100){
            currentFill = 100;
            currentFillage.setText(StringFormatValue.format(myAct,""+currentFill+"%", Color.BLUE, 1.2f, false));
        }
        else if (currentFill < 0){
            currentFillage.setText(StringFormatValue.format(myAct,""+currentFill, Color.BLUE, 1.2f, true));
        }
        else {
            currentFillage.setText(StringFormatValue.format(myAct,""+currentFill+"%", Color.BLUE, 1.2f, false));
        }
        int pumpOffDis = currentFill-fillSetting;
        fillageSetting.setText(StringFormatValue.format(myAct,""+fillSetting+"%", Color.BLUE, 1.2f, false));
        pumpOffDistance.setText(StringFormatValue.format(myAct,""+pumpOffDis+"%", Color.BLUE, 1.2f, false));

    }
}
