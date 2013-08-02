package com.petrologautomation.petrolognexus;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import de.passsy.holocircularprogressbar.HoloCircularProgressBar;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellRuntime_post {

    MainActivity myAct;
    TextView todayRuntime;
    TextView todayRuntimePercent;
    TextView yesterdayRuntime;
    TextView yesterdayRuntimePercent;

    HoloCircularProgressBar TodayRuntimePB;
    HoloCircularProgressBar YesterdayRuntimePB;


    public wellRuntime_post(MainActivity myActivity){

        myAct = myActivity;

        yesterdayRuntime = (TextView)myAct.findViewById(R.id.yesterday_runtime_time);
        yesterdayRuntimePercent = (TextView)myAct.findViewById(R.id.yesterday_runtime_percentTV);
        todayRuntime = (TextView)myAct.findViewById(R.id.today_runtime_time);
        todayRuntimePercent = (TextView)myAct.findViewById(R.id.today_runtime_percentTV);

        TodayRuntimePB = (HoloCircularProgressBar) myAct.findViewById(R.id.runtime_today);
        YesterdayRuntimePB = (HoloCircularProgressBar) myAct.findViewById(R.id.runtime_yesterday);

    }

    public void post() {

        /* Yesterday's Runtime */
        int secYesterday = MainActivity.PetrologSerialCom.getYesterdayRuntime();
        if (secYesterday > 0) {
            /* Time */
            String data = getDurationString(secYesterday);
            yesterdayRuntime.setText(StringFormatValue.format(myAct,data, Color.BLUE,1.2f,false));
            /* % */
            YesterdayRuntimePB.setProgress((float)secYesterday/86400);
            data = String.valueOf((secYesterday*100)/86400)+"%";
            yesterdayRuntimePercent.setText(StringFormatValue.format(myAct,data, Color.BLUE,1f,false));
        }
        else {
            yesterdayRuntime.setText(StringFormatValue.format(myAct,"", Color.GRAY,1.2f,true));
            YesterdayRuntimePB.setProgress(0);
            yesterdayRuntimePercent.setText(StringFormatValue.format(myAct,"", Color.GRAY,1f,true));
        }


        /* Today's Runtime */
        int secToday = MainActivity.PetrologSerialCom.getTodayRuntime();
        if (secToday > 0) {
            /* Time */
            String data = getDurationString(secToday);
            todayRuntime.setText(StringFormatValue.format(myAct,data, Color.BLUE,1.2f,false));
            /* % */
            String InternalClock = MainActivity.PetrologSerialCom.getPetrologClock();
            int totalSecToday = Integer.valueOf(InternalClock.substring(0,2))*3600 +
                                Integer.valueOf(InternalClock.substring(3,5))*60   +
                                Integer.valueOf(InternalClock.substring(6,8));
            data = String.valueOf((secToday*100)/totalSecToday)+"%";
            todayRuntimePercent.setText(StringFormatValue.format(myAct,data, Color.BLUE,1f,false));
            TodayRuntimePB.setProgress((float)secToday/totalSecToday);
        }
        else {
            todayRuntime.setText(StringFormatValue.format(myAct,"", Color.GRAY,1.2f,true));
            todayRuntimePercent.setText(StringFormatValue.format(myAct,"", Color.GRAY,1f,true));
        }

    }

    private String getDurationString(int seconds) {

        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        seconds = seconds % 60;

        return twoDigitString(hours) + " : " + twoDigitString(minutes) + " : " + twoDigitString(seconds);
    }

    private String twoDigitString(int number) {

        if (number == 0) {
            return "00";
        }

        if (number / 10 == 0) {
            return "0" + number;
        }

        return String.valueOf(number);
    }
}
