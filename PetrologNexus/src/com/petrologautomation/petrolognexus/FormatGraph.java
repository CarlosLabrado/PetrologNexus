package com.petrologautomation.petrolognexus;

import android.graphics.Color;
import com.androidplot.xy.XYPlot;
import java.text.DecimalFormat;

/**
 * Created by Cesar on 7/31/13.
 */
public class FormatGraph {

    public static XYPlot format(XYPlot graph){
        XYPlot myGraph = graph;

        // Domain
        myGraph.setDomainValueFormat(new DecimalFormat("0"));

        // Range
        myGraph.setRangeValueFormat(new DecimalFormat("0"));

        myGraph.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        myGraph.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);


        // General
        myGraph.setGridPadding(5,5,5,5);
        myGraph.getLayoutManager().remove(myGraph.getLegendWidget());

        return myGraph;
    }
}
