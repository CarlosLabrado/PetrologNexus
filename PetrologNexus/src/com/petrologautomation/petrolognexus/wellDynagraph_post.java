package com.petrologautomation.petrolognexus;


import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellDynagraph_post {

    MainActivity myAct;
    private XYPlot Dynagraph;
    private Number[] serie = new Number[2];
    private SimpleXYSeries toDyna;
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

        toDyna = new SimpleXYSeries(Arrays.asList(serie),SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "");
        Dynagraph.addSeries(toDyna, lineFormat);

    }

    public void post() {
        clean();
        toDyna = MainActivity.PetrologSerialCom.getDynagraph();
        Dynagraph.redraw();
    }

    public void clean() {
        try {
            while (true){
                toDyna.removeLast();
            }
        }
        catch (NoSuchElementException e){
            /* End of Series */
        }
    }

}
