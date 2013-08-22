package com.petrologautomation.petrolognexus;


import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellFillage_post {

    MainActivity myAct;
    TextSwitcher currentFillage;
    TextView fillageSetting;
    TextSwitcher pumpOffDistance;
    ProgressBar pumpFillage;

    public wellFillage_post(MainActivity myActivity){

        myAct = myActivity;

        currentFillage = (TextSwitcher)myAct.findViewById(R.id.fillage_v1TV);
        fillageSetting = (TextView)myAct.findViewById(R.id.fillage_v2TV);
        pumpOffDistance = (TextSwitcher)myAct.findViewById(R.id.fillage_v3TV);
        pumpFillage = (ProgressBar)myAct.findViewById(R.id.tank);

        Animation in = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation out = AnimationUtils.loadAnimation(myAct,R.anim.push_down_out);

        Animation inPD = AnimationUtils.loadAnimation(myAct, R.anim.push_down_in);
        Animation outPD = AnimationUtils.loadAnimation(myAct,R.anim.push_down_out);

        currentFillage.setInAnimation(in);
        currentFillage.setOutAnimation(out);

        pumpOffDistance.setInAnimation(inPD);
        pumpOffDistance.setOutAnimation(outPD);
    }

    public void post() {
        int fillSetting = MainActivity.PetrologSerialCom.getFillageSetting();
        int currentFill = MainActivity.PetrologSerialCom.getCurrentFillage();
        if (currentFill>100){
            return;
        }
        if (currentFill < 0){
            return;
        }
        else {

            TextView TempTV = (TextView)currentFillage.getCurrentView();
            if (!TempTV.getText().toString().equals(String.valueOf(currentFill)+"%")){
                Log.i("PN - TextSwitcher", TempTV.getText().toString());
                currentFillage.setText(StringFormatValue.format(myAct,""+currentFill+"%", Color.BLUE, 1.2f, false));
            }

            if (currentFill < fillSetting){
                pumpFillage.setProgressDrawable(myAct.getResources().getDrawable(R.drawable.progress_bar_vertical_below));
                pumpFillage.setProgress(currentFill);
                pumpFillage.setSecondaryProgress(fillSetting);
            }
            else {
                pumpFillage.setProgressDrawable(myAct.getResources().getDrawable(R.drawable.progress_bar_vertical_above));
                pumpFillage.setProgress(fillSetting);
                pumpFillage.setSecondaryProgress(currentFill);
            }
        }
        int pumpOffDis = currentFill-fillSetting;
        fillageSetting.setText(StringFormatValue.format(myAct,""+fillSetting+"%", Color.BLUE, 1.2f, false));
        if (pumpOffDis < 0) {

            TextView TempTV = (TextView)pumpOffDistance.getCurrentView();
            if (!TempTV.getText().toString().equals(String.valueOf(pumpOffDis)+"%")){
                Log.i("PN - TextSwitcher", TempTV.getText().toString());
                pumpOffDistance.setText(StringFormatValue.format(myAct,""+pumpOffDis+"%", Color.RED, 1.2f, false));
            }

        }
        else {
            TextView TempTV = (TextView)pumpOffDistance.getCurrentView();
            if (!TempTV.getText().toString().equals(String.valueOf(pumpOffDis)+"%")){
                Log.i("PN - TextSwitcher", TempTV.getText().toString());
                pumpOffDistance.setText(StringFormatValue.format(myAct,""+pumpOffDis+"%", Color.BLUE, 1.2f, false));
            }
        }

    }
}
