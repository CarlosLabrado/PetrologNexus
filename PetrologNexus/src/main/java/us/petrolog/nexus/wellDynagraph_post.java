package us.petrolog.nexus;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.db.chart.Tools;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import us.petrolog.nexus.misc.IntegerComparator;
import us.petrolog.nexus.misc.XYMerger;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellDynagraph_post {


    int mMinX;
    int mMaxX;
    int mMinY;
    int mMaxY;

    ArrayList mBackup = new ArrayList();

    MainActivity myAct;
    private LineChart mLineChartMP;
    private XYPlot Dynagraph;
    private LineAndPointFormatter lineFormat;

    public wellDynagraph_post(MainActivity myActivity) {

        myAct = myActivity;

        mLineChartMP = (LineChart) myAct.findViewById(R.id.linechartMP);

        Dynagraph = FormatGraph.format((XYPlot) myAct.findViewById(R.id.dynagraph));

        XYGraphWidget myWidget = Dynagraph.getGraphWidget();

        Paint originPaint = new Paint();
        originPaint.setColor(Color.LTGRAY);
        myWidget.setDomainOriginLinePaint(originPaint);
        myWidget.setRangeOriginLinePaint(originPaint);
        myWidget.setDrawMarkersEnabled(false);

        lineFormat = new LineAndPointFormatter(
                myAct.getResources().getColor(R.color.mainBlue),
                null,
                null,
                new PointLabelFormatter(Color.TRANSPARENT));
        Paint myPaint = new Paint();
        myPaint.setStyle(Paint.Style.STROKE);
        myPaint.setStrokeWidth(5);
        myPaint.setColor(myAct.getResources().getColor(R.color.mainBlue));
        lineFormat.setLinePaint(myPaint);

    }

    public void post() {


        try {
            SimpleXYSeries newDyna = MainActivity.PetrologSerialCom.getDynagraph();
            Set<XYSeries> backup = Dynagraph.getSeriesSet();

            ArrayList xValues1 = new ArrayList();
            ArrayList yValues1 = new ArrayList();

            ArrayList xValues2 = new ArrayList();
            ArrayList yValues2 = new ArrayList();



            ArrayList<Integer> dynaY = new ArrayList<>();

            if (newDyna != null) {
                mMinX = Integer.MAX_VALUE;
                mMaxX = Integer.MIN_VALUE;
                mMinY = Integer.MAX_VALUE;
                mMaxY = Integer.MIN_VALUE;

                Log.d("newDynaSize", String.valueOf(newDyna.size()));
                for(int i = 0; i < newDyna.size(); i++) {
                    int currentX = (int) newDyna.getX(i);
                    int currentY = (int) newDyna.getY(i);

                    dynaY.add(i, currentY);

                    if (mMinX > currentX) {
                        mMinX = currentX;
                    }
                    if(mMaxX < currentX ) {
                        mMaxX = currentX;
                    }
                    if (mMinY > currentY) {
                        mMinY = currentY;
                    }
                    if(mMaxY < currentY ) {
                        mMaxY = currentY;
                    }

                }

                boolean reachedMinPoint = false;
                for(int i = 0; i < newDyna.size(); i++) {
                    int currentX = (int) newDyna.getX(i);

                    if (currentX == mMinX) {
                        reachedMinPoint = true;
                    }
                    if (!reachedMinPoint) {
                        xValues1.add(currentX);
                    } else{
                        xValues2.add(currentX);
                    }
                }
                for (int i = 0; i < newDyna.size(); i++) {
                    if (i < xValues1.size()) {
                        yValues1.add(newDyna.getY(i));
                    } else {
                        yValues2.add(newDyna.getY(i));
                    }
                }

                Collections.reverse(xValues1);
                Collections.reverse(yValues1);

                startMerging(xValues1,xValues2,yValues1,yValues2);

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
                int alphaCounter = 0;
                for (XYSeries tempNew : backup) {
                    Dynagraph.addSeries(tempNew, FormatGraph.getDynaFormatterByPlace(alphaCounter));
                    alphaCounter++;
                }
                Dynagraph.addSeries(newDyna, lineFormat);
            }

//            mLineChartMP.animateX(2500, Easing.EasingOption.EaseInOutQuart);

            Dynagraph.redraw();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    //TODO change this to an async Task
    public void startMerging(ArrayList xValues1, ArrayList xValues2, ArrayList yValues1, ArrayList yValues2) throws ArrayIndexOutOfBoundsException {

        List<Integer> x1 = new ArrayList<Integer>();
        List<Integer> y1 = new ArrayList<Integer>();

        List<Integer> x2 = new ArrayList<Integer>();
        List<Integer> y2 = new ArrayList<Integer>();

        List<XYMerger> mergedData = getMergedData(xValues1, yValues1, xValues2);

        for (XYMerger xy : mergedData) {
            x1.add(xy.getX());
            y1.add(xy.getY());
        }

        mergedData = getMergedData(xValues2, yValues2, xValues1);

        for (XYMerger xy : mergedData) {
            x2.add(xy.getX());
            y2.add(xy.getY());
        }

        mergedData.clear();

        System.out.println(x1); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 35]
        System.out.println(y1); // [5, null, null, null, null, null, null, null, null, 112, 23, 34, 50, 100, 130]

        System.out.println("\n");

        System.out.println(x2); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 35] // X = X1 = X2
        System.out.println(y2); // [1, 5, 20, 15, 10, 30, 40, 70, 75, 100, null, null, null, null, null]

        // we make sure they have starting connecting points
        if(!x1.isEmpty() && !x2.isEmpty() && !y1.isEmpty() && !y2.isEmpty()) {
            try {
                x1.add(0, x2.get(0));
                y1.add(0, y2.get(0));
                // final connecting points
                if ((x1.get(x1.size() - 1)) != null && (y1.get(y1.size() - 1)) != null) {
                    int temp1 = x1.get(x1.size() - 1);
                    int temp2 = y1.get(y1.size() - 1);
                    Log.d("size?", temp1 + " " + temp2);
                    x2.add(temp1);
                    y2.add(temp2);
                }

                ArrayList<String> xVals = new ArrayList<String>();
                ArrayList<Entry> yVals = new ArrayList<Entry>();

                ArrayList<String> xVals2 = new ArrayList<String>();
                ArrayList<Entry> yVals2 = new ArrayList<Entry>();

                for (int i = 0; i < x1.size(); i++) {
                    xVals.add(i, String.valueOf(x1.get(i)));
                }


                for (int i = 0; i < y1.size(); i++) {
                    try {
                        yVals.add(new Entry(y1.get(i), i));

                    } catch (Exception e) {
                        Log.e("yvals", "not in the array");
                    }

                }

                for (int i = 0; i < x2.size(); i++) {
                    xVals2.add(i, String.valueOf(x2.get(i)));
                }


                for (int i = 0; i < y2.size(); i++) {
                    try {
                        yVals2.add(new Entry(y2.get(i), i));

                    } catch (Exception e) {
                        Log.e("yvals", "not in the array");
                    }

                }



                // create a dataset and give it a type
                LineDataSet set1 = new LineDataSet(yVals, "DataSet X");

                // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(15f, 10f, 0f);
//        set1.enableDashedHighlightLine(10f, 5f, 0f);
                set1.setColor(myAct.getResources().getColor(R.color.fillBlue));
                set1.setCircleColor(myAct.getResources().getColor(R.color.blue_600));
                set1.setLineWidth(Tools.fromDpToPx(1));
                set1.setCircleSize(0);
                set1.setDrawCircleHole(false);
                set1.setValueTextSize(0);
                set1.setFillAlpha(65);
                set1.setFillColor(myAct.getResources().getColor(R.color.grey_50));
//        set1.setDrawFilled(true);
                // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
                // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

                // create a dataset and give it a type
                LineDataSet set2 = new LineDataSet(yVals2, "DataSet Y");
                // set1.setFillAlpha(110);
                // set1.setFillColor(Color.RED);

                // set the line to be drawn like this "- - - - - -"
//        set2.enableDashedLine(15f, 10f, 0f);
//        set2.enableDashedHighlightLine(10f, 5f, 0f);
                set2.setColor(myAct.getResources().getColor(R.color.fillBlue));
                set2.setCircleColor(myAct.getResources().getColor(R.color.blue_600));
                set2.setLineWidth(Tools.fromDpToPx(1));
                set2.setCircleSize(0);
                set2.setDrawCircleHole(false);
                set2.setValueTextSize(0);
                set2.setFillAlpha(65);
                set2.setFillColor(myAct.getResources().getColor(R.color.fillBlue));
//        set2.setDrawFilled(true);
                // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
                // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

                ArrayList<LineDataSet> dataSets = new ArrayList<>();

                dataSets.add(set1); // add the datasets
                dataSets.add(set2); // add the datasets

                if (!mBackup.isEmpty()) {
                    // create a dataset and give it a type
                    LineDataSet set3 = new LineDataSet((List<Entry>) mBackup.get(0), "Backup X ");

                    // set the line to be drawn like this "- - - - - -"
//                    set3.enableDashedLine(15f, 10f, 0f);
                    set3.setColor(myAct.getResources().getColor(R.color.fillRed));
                    set3.setCircleColor(myAct.getResources().getColor(R.color.red_600));
                    set3.setLineWidth(Tools.fromDpToPx(1));
                    set3.setCircleSize(0);
                    set3.setDrawCircleHole(false);
                    set3.setValueTextSize(0);
                    set3.setFillAlpha(65);
                    set3.setFillColor(myAct.getResources().getColor(R.color.grey_50));

                     // create a dataset and give it a type
                    LineDataSet set4 = new LineDataSet((List<Entry>) mBackup.get(1), "Backup Y ");

                    // set the line to be drawn like this "- - - - - -"
//                    set4.enableDashedLine(15f, 10f, 0f);
                    set4.setColor(myAct.getResources().getColor(R.color.fillRed));
                    set4.setCircleColor(myAct.getResources().getColor(R.color.red_600));
                    set4.setLineWidth(Tools.fromDpToPx(1));
                    set4.setCircleSize(0);
                    set4.setDrawCircleHole(false);
                    set4.setValueTextSize(0);
                    set4.setFillAlpha(65);
                    set4.setFillColor(myAct.getResources().getColor(R.color.grey_50));

                    dataSets.add(set3);
                    dataSets.add(set4);

                    mBackup.clear();
                }

                mBackup.add(yVals);
                mBackup.add(yVals2);

                // create a data object with the datasets
                LineData data = new LineData(xVals, dataSets);

                int visibleRange = mMaxY - mMinX;

                // set data
                mLineChartMP.setData(data);
                mLineChartMP.setVisibleYRangeMaximum(visibleRange, YAxis.AxisDependency.LEFT);
                mLineChartMP.moveViewToY((mMaxY - (visibleRange / 2)) + 10, YAxis.AxisDependency.LEFT);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("MPChart", "Error creating the chart");
            }
            // set an alternative background color
//        mLineChartMP.setBackgroundColor(Color.LTGRAY);
        }
    }

    /**
     * @param orgXvals : Original X Values
     * @param orgYvals : Original Y Values
     * @param extdXvals : Extended X values to be merged
     * @return Sorted mergedData
     */
    private List<XYMerger> getMergedData(ArrayList orgXvals , ArrayList orgYvals , ArrayList extdXvals) {

        HashSet<Integer> tempSet = new HashSet<Integer>();
        List<XYMerger> tempMerger = new ArrayList<XYMerger>();

        tempSet.clear();
        tempMerger.clear();

        for (int i = 0; i < orgXvals.size(); i++) {
            tempSet.add((Integer) orgXvals.get(i));
            XYMerger xy = new XYMerger();
            xy.setX((Integer) orgXvals.get(i));
            xy.setY((Integer) orgYvals.get(i));
            tempMerger.add(xy);
        }

        for (int i = 0; i < extdXvals.size(); i++) {
            if (tempSet.add((Integer) extdXvals.get(i))) {
                XYMerger xy = new XYMerger();
                xy.setX((Integer) extdXvals.get(i));
                xy.setY(null);
                tempMerger.add(xy);
            }
        }
        Collections.sort(tempMerger, new IntegerComparator(true));
        return tempMerger;
    }

    public void clean() {
        for (XYSeries temp : Dynagraph.getSeriesSet()) {
            Dynagraph.removeSeries(temp);
        }
        Dynagraph.redraw();
    }

}
