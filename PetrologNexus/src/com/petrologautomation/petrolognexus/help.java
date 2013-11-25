package com.petrologautomation.petrolognexus;

import android.graphics.Typeface;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Cesar on 11/25/13.
 */
public class help {

    private ImageView helpConnected;
    private ImageView helpDisconnected;


    public help (MainActivity myActivity){

        /* Init Help */
        Typeface helpFont = Typeface.createFromAsset(myActivity.getAssets(),"fonts/gloriahallelujah.ttf");
        TextView current = (TextView)myActivity.findViewById(R.id.current_h);
        current.setTypeface(helpFont);
        TextView runtime = (TextView)myActivity.findViewById(R.id.runtime_h);
        runtime.setTypeface(helpFont);
        TextView runtime_trend = (TextView)myActivity.findViewById(R.id.runtime_trend_h);
        runtime_trend.setTypeface(helpFont);
        TextView map = (TextView)myActivity.findViewById(R.id.map_h);
        map.setTypeface(helpFont);
        TextView dyna = (TextView)myActivity.findViewById(R.id.dyna_h);
        dyna.setTypeface(helpFont);
        TextView settings = (TextView)myActivity.findViewById(R.id.settings_h);
        settings.setTypeface(helpFont);
        TextView fillage = (TextView)myActivity.findViewById(R.id.fillage_h);
        fillage.setTypeface(helpFont);

        helpConnected = (ImageView)myActivity.findViewById(R.id.help_connectedIV);
        helpDisconnected = (ImageView)myActivity.findViewById(R.id.help_disconnectedIV);

    }

    public void setConnected (){

        helpDisconnected.setVisibility(View.INVISIBLE);
        helpConnected.setVisibility(View.VISIBLE);

    }

    public void setDisconnected (){

        helpDisconnected.setVisibility(View.VISIBLE);
        helpConnected.setVisibility(View.INVISIBLE);

    }
}
