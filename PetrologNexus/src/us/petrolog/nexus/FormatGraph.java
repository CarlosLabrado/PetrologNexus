package us.petrolog.nexus;

import android.graphics.Color;
import android.graphics.Paint;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
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
        myGraph.setDomainBoundaries(1000, 4000, BoundaryMode.AUTO);


        // Range
        myGraph.setRangeValueFormat(new DecimalFormat("0"));
        myGraph.setRangeBoundaries(1000, 4000, BoundaryMode.AUTO);


        myGraph.getGraphWidget().getDomainLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getRangeLabelPaint().setColor(Color.BLACK);

        myGraph.getGraphWidget().getDomainOriginLabelPaint().setColor(Color.BLACK);
        myGraph.getGraphWidget().getDomainOriginLinePaint().setColor(Color.BLACK);


        // General
        myGraph.setGridPadding(5,5,5,5);
        myGraph.getLayoutManager().remove(myGraph.getLegendWidget());

        return myGraph;
    }

    public static LineAndPointFormatter getDynaFormatterByPlace(int place){
        Paint myPaint = new Paint();

        LineAndPointFormatter p0 = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter(Color.TRANSPARENT));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.RED);
        myPaint.setAlpha(250);
        p0.setLinePaint(myPaint);

        LineAndPointFormatter p1 = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter (Color.TRANSPARENT));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.RED);
        myPaint.setAlpha(200);
        p1.setLinePaint(myPaint);

        LineAndPointFormatter p2 = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter(Color.TRANSPARENT));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.RED);
        myPaint.setAlpha(150);
        p2.setLinePaint(myPaint);

        LineAndPointFormatter p3 = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter (Color.TRANSPARENT));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.RED);
        myPaint.setAlpha(100);
        p3.setLinePaint(myPaint);

        LineAndPointFormatter p4 = new LineAndPointFormatter(
                Color.BLUE,
                null,
                null,
                new PointLabelFormatter(Color.TRANSPARENT));
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(Color.RED);
        myPaint.setAlpha(50);
        p4.setLinePaint(myPaint);

        LineAndPointFormatter arr[] = new LineAndPointFormatter[] {p0, p1, p2, p3, p4};

        return arr[place];

    }
}
