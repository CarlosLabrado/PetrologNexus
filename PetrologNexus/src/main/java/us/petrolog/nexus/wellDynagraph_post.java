package us.petrolog.nexus;


import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.util.Log;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.db.chart.Tools;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

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


    static int mMinX;
    static int mMaxX;
    static int mMinY;
    static int mMaxY;

    ArrayList<ArrayList> mBackup = new ArrayList<>();

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


            if (newDyna != null) {

                MergeAndCalculate task = new MergeAndCalculate();
                task.setListener(new MergeAndCalculateTaskListener() {
                    @Override
                    public void onComplete(ArrayList<ArrayList> chartValues, Exception e) {
//                        Log.e("onComplete", "async task completed");
                        buildChart(chartValues);
                    }
                }).execute(newDyna);

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


            Dynagraph.redraw();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * builds and draws the MPChart
     *
     * @param chartValues has 3 sets of values, the 2 lines that make the polygon and the 3rd is the
     *                    x axis to be drawn into
     */
    private void buildChart(ArrayList<ArrayList> chartValues) {

        try {
            ArrayList<Entry> yVals = chartValues.get(0);
            ArrayList<Entry> yVals2 = chartValues.get(1);
            ArrayList<String> xVals = chartValues.get(2);

            // create a dataset and give it a type
            LineDataSet set1 = new LineDataSet(yVals, "DataSet X");

            // set the line to be drawn like this "- - - - - -"
//        set1.enableDashedLine(15f, 10f, 0f);
//        set1.enableDashedHighlightLine(10f, 5f, 0f);
            set1.setColor(myAct.getResources().getColor(R.color.mainBlue));
            set1.setCircleColor(myAct.getResources().getColor(R.color.mainBlue));
            set1.setLineWidth(Tools.fromDpToPx(1));
//            set1.setCircleRadius(Tools.fromDpToPx(1)/2);
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);
            set1.setDrawCubic(true);
            set1.setDrawCircles(false);
            set1.setFillAlpha(65);
            set1.setFillColor(myAct.getResources().getColor(R.color.mainBlue));
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
            set2.setColor(myAct.getResources().getColor(R.color.mainBlue));
            set2.setCircleColor(myAct.getResources().getColor(R.color.mainBlue));
            set2.setLineWidth(Tools.fromDpToPx(1));
//            set2.setCircleRadius(Tools.fromDpToPx(1)/2);
            set2.setDrawCircles(false);
            set2.setDrawCircleHole(false);
            set2.setDrawValues(false);
            set2.setDrawCubic(true);
            set2.setFillAlpha(65);
            set2.setFillColor(myAct.getResources().getColor(R.color.mainBlue));
            set2.setLabel("");
//        set2.setDrawFilled(true);
            // set1.setShader(new LinearGradient(0, 0, 0, mChart.getHeight(),
            // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            if (!mBackup.isEmpty()) {
                // create a dataset and give it a type
                LineDataSet set3 = new LineDataSet(mBackup.get(0), "Backup X ");

                // set the line to be drawn like this "- - - - - -"
                set3.enableDashedLine(10f, 10f, 0f);
                set3.setColor(myAct.getResources().getColor(R.color.mainRedAlpha));
                set3.setCircleColor(myAct.getResources().getColor(R.color.mainRedAlpha));
                set3.setLineWidth(Tools.fromDpToPx(1));
//                set3.setCircleRadius(Tools.fromDpToPx(1)/2);
                set3.setDrawCircles(false);

                set3.setDrawCircleHole(false);
                set3.setDrawValues(false);
                set3.setDrawCubic(true);
                set3.setFillAlpha(65);
                set3.setFillColor(myAct.getResources().getColor(R.color.grey_50));

                // create a dataset and give it a type
                LineDataSet set4 = new LineDataSet(mBackup.get(1), "Backup Y ");

                // set the line to be drawn like this "- - - - - -"
                set4.enableDashedLine(10f, 10f, 0f);
                set4.setColor(myAct.getResources().getColor(R.color.mainRedAlpha));
                set4.setCircleColor(myAct.getResources().getColor(R.color.mainRedAlpha));
                set4.setLineWidth(Tools.fromDpToPx(1));
//                set4.setCircleRadius(Tools.fromDpToPx(1)/2);
                set4.setDrawCircles(false);
                set4.setDrawCircleHole(false);
                set4.setDrawValues(false);
                set4.setDrawCubic(true);
                set4.setFillAlpha(65);
                set4.setFillColor(myAct.getResources().getColor(R.color.grey_50));

                dataSets.add(set3);
                dataSets.add(set4);

                mBackup.clear();
            }

            dataSets.add(set1); // add the datasets
            dataSets.add(set2); // add the datasets

            mBackup.add(yVals);
            mBackup.add(yVals2);
            mBackup.add(xVals);

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            int visibleRange = mMaxY - mMinX;

            // set data
            mLineChartMP.setData(data);
            mLineChartMP.invalidate();
            mLineChartMP.setVisibleYRangeMaximum(visibleRange, YAxis.AxisDependency.LEFT);
//            mLineChartMP.moveViewToX((mMaxX - (visibleRange / 2)) + 10);
            mLineChartMP.moveViewToY((mMaxY - (visibleRange / 2)) + 10, YAxis.AxisDependency.LEFT);

            mLineChartMP.setDescription("");
            mLineChartMP.setDrawGridBackground(false);

            YAxis rightAxis = mLineChartMP.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawLabels(false);
            rightAxis.setAxisLineColor(myAct.getResources().getColor(R.color.gridBlue));

            YAxis leftAxis = mLineChartMP.getAxisLeft();
            leftAxis.setGridColor(myAct.getResources().getColor(R.color.gridBlue));
            leftAxis.enableGridDashedLine(4f, 4f, 0f);
            leftAxis.setTextColor(myAct.getResources().getColor(R.color.mainGray));
            leftAxis.setAxisLineColor(myAct.getResources().getColor(R.color.gridBlue));


            XAxis xAxis = mLineChartMP.getXAxis();
            xAxis.setGridColor(myAct.getResources().getColor(R.color.gridBlue));
            xAxis.enableGridDashedLine(4f, 4f, 0f);
            xAxis.setTextColor(myAct.getResources().getColor(R.color.mainGray));
            xAxis.setAxisLineColor(myAct.getResources().getColor(R.color.gridBlue));

//            xAxis.setXOffset(20);


            Legend legend = mLineChartMP.getLegend();
            legend.setCustom(
                    new int[]{
                            myAct.getResources().getColor(R.color.mainBlue), myAct.getResources().getColor(R.color.mainRed)
                    }, new String[]{myAct.getString(R.string.legend_current), myAct.getString(R.string.legend_past)});
            mLineChartMP.notifyDataSetChanged();
            mLineChartMP.animateX(1000, Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseOutQuart));

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }


    public void clean() {
        for (XYSeries temp : Dynagraph.getSeriesSet()) {
            Dynagraph.removeSeries(temp);
        }
        Dynagraph.redraw();
    }


    public interface MergeAndCalculateTaskListener {
        void onComplete(ArrayList<ArrayList> chartValues, Exception e);
    }

    /**
     * But why do we have to merge and calculate?
     * Because, MPChart does not support an XYPlot or a time series, so what we do is that we take
     * all these data as if it were two separate sets that converge and create the illusion of
     * a polygon.
     * <p/>
     * Also we do an union, because we need some values to be null, so the space between points
     * could be different.
     * <p/>
     * Got merge info from here:
     * http://stackoverflow.com/questions/28116357/mpandroidchart-how-to-represent-multiple-dataset-object-with-different-number-of
     */
    public static class MergeAndCalculate extends AsyncTask<SimpleXYSeries, Void, ArrayList<ArrayList>> {
        ArrayList<Number> xValues1 = new ArrayList<>();
        ArrayList<Number> yValues1 = new ArrayList<>();
        ArrayList<Number> xValues2 = new ArrayList<>();
        ArrayList<Number> yValues2 = new ArrayList<>();
        ArrayList<Integer> dynaY = new ArrayList<>();
        private Exception mError = null;
        private MergeAndCalculateTaskListener mListener = null;


        @Override
        protected ArrayList<ArrayList> doInBackground(SimpleXYSeries... simpleXYSeries) {
            mMinX = Integer.MAX_VALUE;
            mMaxX = Integer.MIN_VALUE;
            mMinY = Integer.MAX_VALUE;
            mMaxY = Integer.MIN_VALUE;

            SimpleXYSeries newDyna = simpleXYSeries[0];

            Log.d("newDynaSize", String.valueOf(newDyna.size()));
            for (int i = 0; i < newDyna.size(); i++) {
                int currentX = (int) newDyna.getX(i);
                int currentY = (int) newDyna.getY(i);

                dynaY.add(i, currentY);

                if (mMinX > currentX) {
                    mMinX = currentX;
                }
                if (mMaxX < currentX) {
                    mMaxX = currentX;
                }
                if (mMinY > currentY) {
                    mMinY = currentY;
                }
                if (mMaxY < currentY) {
                    mMaxY = currentY;
                }

            }

            boolean reachedMinPoint = false;
            for (int i = 0; i < newDyna.size(); i++) {
                int currentX = (int) newDyna.getX(i);

                if (currentX == mMinX) {
                    reachedMinPoint = true;
                }
                if (!reachedMinPoint) {
                    xValues1.add(currentX);
                } else {
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


            return startMerging(xValues1, xValues2, yValues1, yValues2);
        }


        public ArrayList<ArrayList> startMerging(ArrayList xValues1, ArrayList xValues2, ArrayList yValues1, ArrayList yValues2) throws ArrayIndexOutOfBoundsException {

            List<Integer> x1 = new ArrayList<>();
            List<Integer> y1 = new ArrayList<>();

            List<Integer> x2 = new ArrayList<>();
            List<Integer> y2 = new ArrayList<>();

            ArrayList<ArrayList> dataSets = new ArrayList<>();

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
            if (!x1.isEmpty() && !x2.isEmpty() && !y1.isEmpty() && !y2.isEmpty()) {
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

                    ArrayList<String> xVals = new ArrayList<>();
                    ArrayList<Entry> yVals = new ArrayList<>();

                    ArrayList<String> xVals2 = new ArrayList<>();
                    ArrayList<Entry> yVals2 = new ArrayList<>();

                    for (int i = 0; i < x1.size(); i++) {
                        xVals.add(i, String.valueOf(x1.get(i)));
                    }


                    for (int i = 0; i < y1.size(); i++) {
                        try {
                            yVals.add(new Entry(y1.get(i), i));

                        } catch (Exception e) {
//                            Log.e("yvals", "not in the array");
                        }

                    }

                    for (int i = 0; i < x2.size(); i++) {
                        xVals2.add(i, String.valueOf(x2.get(i)));
                    }


                    for (int i = 0; i < y2.size(); i++) {
                        try {
                            yVals2.add(new Entry(y2.get(i), i));

                        } catch (Exception e) {
//                            Log.e("yvals", "not in the array");
                        }

                    }

                    dataSets.add(yVals);
                    dataSets.add(yVals2);
                    dataSets.add(xVals);
                } catch (Exception e) {
                    mError = e;

                    e.printStackTrace();
                    Log.d("MPChart", "Error creating the chart");
                }
            }
            return dataSets;
        }

        /**
         * @param orgXvals  : Original X Values
         * @param orgYvals  : Original Y Values
         * @param extdXvals : Extended X values to be merged
         * @return Sorted mergedData
         */
        private List<XYMerger> getMergedData(ArrayList orgXvals, ArrayList orgYvals, ArrayList extdXvals) {

            HashSet<Integer> tempSet = new HashSet<>();
            List<XYMerger> tempMerger = new ArrayList<>();

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

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
//            if (mProgressBar != null) {
//                mProgressBar.setVisibility(View.VISIBLE);
//            }
        }

        /**
         * Using the listener pattern to help testing
         *
         * @param listener we use this to call onComplete
         * @return listener
         */
        public MergeAndCalculate setListener(MergeAndCalculateTaskListener listener) {
            this.mListener = listener;
            return this;
        }

        @Override
        protected void onPostExecute(ArrayList<ArrayList> result) {
//            if (mProgressBar != null) {
//                mProgressBar.setVisibility(View.GONE);
//            }
            if (this.mListener != null && !result.isEmpty() && result.size() == 3) {
                this.mListener.onComplete(result, mError);
            } else if (mError != null) {
                mError.printStackTrace();
            }
        }


    }

}
