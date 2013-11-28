package com.petrologautomation.petrolognexus;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Cesar on 11/25/13.
 */
public class help {

    private ImageView helpConnectedStopped;
    private ImageView helpDisconnected;
    private ImageView helpConnectedRunning;


    public help (MainActivity myActivity){

        /* Init Help */
        Typeface helpFont = Typeface.createFromAsset(myActivity.getAssets(),"fonts/gloriahallelujah.ttf");
        TextView current = (TextView)myActivity.findViewById(R.id.current_h);
        current.setTypeface(helpFont);
        TextView runtime = (TextView)myActivity.findViewById(R.id.runtime_h);
        runtime.setTypeface(helpFont);
        TextView runtime_trend = (TextView)myActivity.findViewById(R.id.runtime_trend_h);
        runtime_trend.setTypeface(helpFont);
        TextView dyna = (TextView)myActivity.findViewById(R.id.dyna_h);
        dyna.setTypeface(helpFont);
        TextView settings = (TextView)myActivity.findViewById(R.id.settings_h);
        settings.setTypeface(helpFont);
        TextView fillage = (TextView)myActivity.findViewById(R.id.fillage_h);
        fillage.setTypeface(helpFont);

        helpConnectedStopped = (ImageView)myActivity.findViewById(R.id.help_connected_stoppedIV);
        helpDisconnected = (ImageView)myActivity.findViewById(R.id.help_disconnectedIV);
        helpConnectedRunning =  (ImageView)myActivity.findViewById(R.id.help_connected_runningIV);

    }

    public void setConnectedStopped(){

        helpDisconnected.setVisibility(View.INVISIBLE);
        helpConnectedStopped.setVisibility(View.VISIBLE);
        helpConnectedRunning.setVisibility(View.INVISIBLE);

    }

    public void setDisconnected (){

        helpDisconnected.setVisibility(View.VISIBLE);
        helpConnectedStopped.setVisibility(View.INVISIBLE);
        helpConnectedRunning.setVisibility(View.INVISIBLE);

    }

    public void setConnectedRunning (){

        helpDisconnected.setVisibility(View.INVISIBLE);
        helpConnectedStopped.setVisibility(View.INVISIBLE);
        helpConnectedRunning.setVisibility(View.VISIBLE);

    }
}
