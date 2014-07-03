package us.petrolog.nexus;


import android.graphics.Color;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;
import java.text.DecimalFormat;

/**
 * Created by Cesar on 7/31/13.
 */
public class FormatTrend {

    public static XYPlot format(XYPlot graph){
        XYPlot myGraph = graph;

        // Domain
        myGraph.setDomainBoundaries(1, 31, BoundaryMode.FIXED);
        myGraph.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 31);
        myGraph.setDomainStepValue(1);
        myGraph.setDomainValueFormat(new DecimalFormat("0"));

        //Range
        myGraph.setRangeBoundaries(0, 112, BoundaryMode.FIXED);
        myGraph.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 30);
        myGraph.setRangeStepValue(7);
        myGraph.setRangeValueFormat(new DecimalFormat("0"));

        myGraph.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getRangeLabelPaint().setColor(Color.WHITE);

        myGraph.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getDomainOriginLinePaint().setColor(Color.LTGRAY);

        return myGraph;
    }
}
