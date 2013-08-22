package com.petrologautomation.petrolognexus;


import android.content.Context;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.util.Arrays;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellHistoricalRuntime_post {

    MainActivity myAct;
    private XYPlot History;
    private SimpleXYSeries toHis;
    private Number[] serie = new Number[2];

    public wellHistoricalRuntime_post(MainActivity myActivity){

        myAct = myActivity;
        toHis = new SimpleXYSeries(Arrays.asList(serie),SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "");
        History = (XYPlot) myAct.findViewById(R.id.runtimeTrend);

        XYGraphWidget myWidget = History.getGraphWidget();

        myWidget.setDrawMarkersEnabled(false);

        PointLabelFormatter label = new PointLabelFormatter(Color.BLUE);
        label.vOffset = -.25f;
        label.hOffset = 5f;
        Paint textPaint = new Paint();
        textPaint.setTextSize(12);
        textPaint.setTypeface(Typeface.defaultFromStyle(1));
        textPaint.setColor(Color.BLUE);
        label.setTextPaint(textPaint);


        LineAndPointFormatter lineFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.RED,
                null,
                label);
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        myPaint.setStrokeWidth(3);
        myPaint.setAlpha(250);
        myPaint.setShader(new LinearGradient(0, 0, 0, 250, Color.WHITE, Color.RED, Shader.TileMode.REPEAT));
        lineFormat.setLinePaint(myPaint);

        History.getLayoutManager().remove(History.getLegendWidget());

        History.addSeries(toHis,lineFormat);


    }

    public void post() {

        for (int i=1;i<32;i++){
            toHis.addLast(i, MainActivity.PetrologSerialCom.getHistoricalRuntime(i)/3600);
        }
        History.redraw();
    }
}
