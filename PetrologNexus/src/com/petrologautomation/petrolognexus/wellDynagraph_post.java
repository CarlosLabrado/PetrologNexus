package com.petrologautomation.petrolognexus;


import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Set;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellDynagraph_post {

    MainActivity myAct;
    private XYPlot Dynagraph;
    private LineAndPointFormatter lineFormat;

    public wellDynagraph_post(MainActivity myActivity){

        myAct = myActivity;

        Dynagraph = FormatGraph.format((XYPlot) myAct.findViewById(R.id.dynagraph));

        XYGraphWidget myWidget = Dynagraph.getGraphWidget();

        Paint originPaint = new Paint();
        originPaint.setColor(Color.LTGRAY);
        myWidget.setDomainOriginLinePaint(originPaint);
        myWidget.setRangeOriginLinePaint(originPaint);
        myWidget.setDrawMarkersEnabled(false);

        lineFormat = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter (Color.TRANSPARENT));
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.BLUE);
        lineFormat.setLinePaint(myPaint);

    }

    public void post() {

        SimpleXYSeries newDyna = MainActivity.PetrologSerialCom.getDynagraph();
        Set<XYSeries> backup = Dynagraph.getSeriesSet();

        if (newDyna != null) {
            int count = 0;
            XYSeries toErase = null;
            for (XYSeries tempOld : backup) {
                if (count == 0) {
                    toErase = tempOld;
                }
                if (count >= 5) {
                    backup.remove(toErase);
                }
                count++;
            }
            clean();
            for (XYSeries tempNew : backup) {
                // TODO change line format to alpha function located in FormatGraph.java
                Dynagraph.addSeries(tempNew, lineFormat);
            }
            // TODO change line format to alpha function located in FormatGraph.java
            Dynagraph.addSeries(newDyna, lineFormat);
        }
        Dynagraph.redraw();
    }

    public void clean() {
        for (XYSeries temp : Dynagraph.getSeriesSet()) {
            Dynagraph.removeSeries(temp);
        }
        Dynagraph.redraw();
    }

}
