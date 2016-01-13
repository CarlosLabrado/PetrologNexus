package us.petrolog.nexus;


import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.YController;
import com.db.chart.view.animation.Animation;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.QuintEase;
import com.db.chart.view.animation.style.DashAnimation;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellHistoricalRuntime_post {

    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    /**
     * Ease
     */
    private static BaseEasingMethod mCurrEasing;
    /**
     * Enter
     */
    private static float mCurrStartX;
    private static float mCurrStartY;
    /**
     * Alpha
     */
    private static int mCurrAlpha;
    MainActivity myAct;
    private LineChartView mChart;
    private Handler mHandler;

    public wellHistoricalRuntime_post(MainActivity myActivity) {

        myAct = myActivity;


        // chart things
        mChart = (LineChartView) myAct.findViewById(R.id.linechart);

        /** Chart things **/
        mCurrOverlapFactor = .5f;
        mCurrEasing = new QuintEase();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        mHandler = new Handler();
//
//        mHandler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//
//                    animateInvitationTextFadeIn();
//
//            }
//        }, 500);


    }

    public void post() {
        clean();
        int day = 1;
        try {
            day = Integer.valueOf(MainActivity.PetrologSerialCom.getPetrologClock().substring(9, 11));
        } catch (NumberFormatException e) {
            // TODO
        }

        LineSet dataSetBeforeToday = new LineSet();
        LineSet dataSetToday = new LineSet();
        LineSet dataSetAfterToday = new LineSet();

        int highestYValue = 0;

        Point point;
        Point dummyPoint;
        for (int i = 1; i < 32; i++) {
            int petrologHistoricalRuntimeReading = (MainActivity.PetrologSerialCom.getHistoricalRuntime(i) * 100) / 86400;

            point = new Point(String.valueOf(i), petrologHistoricalRuntimeReading);
            dummyPoint = new Point(String.valueOf(i), 0);
            dummyPoint.setColor(myAct.getResources().getColor(R.color.trans));
            dummyPoint.setRadius(2);

            if (i < day) { //Before Today

                dataSetBeforeToday.addPoint(point);

                dataSetAfterToday.addPoint(dummyPoint);
                dataSetToday.addPoint(dummyPoint);
            }
            if (i == day) {
                dataSetToday.addPoint(point);

                dataSetBeforeToday.addPoint(dummyPoint);
                dataSetAfterToday.addPoint(dummyPoint);
            }
            if (i >day) { // After today
                dataSetAfterToday.addPoint(point);

                dataSetToday.addPoint(dummyPoint);
                dataSetBeforeToday.addPoint(dummyPoint);
            }
            if (highestYValue <= petrologHistoricalRuntimeReading) {
                highestYValue = petrologHistoricalRuntimeReading;
            }
        }

        Paint mLineGridPaint = new Paint();
        mLineGridPaint.setColor(myAct.getResources().getColor(R.color.blue_200));
        mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
        mLineGridPaint.setStyle(Paint.Style.STROKE);
        mLineGridPaint.setAntiAlias(true);
        mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.5f));

        Log.e("TIME", "Post ran");

        dataSetBeforeToday.setColor(myAct.getResources().getColor(R.color.blue_600))
                .setFill(myAct.getResources().getColor(R.color.fillBlue))
                .setDotsRadius(Tools.fromDpToPx(2))
                .setDashed(new float[]{10, 10})

//                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsColor(myAct.getResources().getColor(R.color.blue_800))
                .setThickness(Tools.fromDpToPx(2));
        mChart.addData(dataSetBeforeToday);

        dataSetToday.setColor(myAct.getResources().getColor(R.color.red_600))
                .setFill(myAct.getResources().getColor(R.color.fillRed))
                .setDotsRadius(Tools.fromDpToPx(2))
//                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsColor(myAct.getResources().getColor(R.color.red_600))
                .setThickness(Tools.fromDpToPx(2));
        mChart.addData(dataSetToday);

        dataSetAfterToday.setColor(myAct.getResources().getColor(R.color.grey_600))
                .setFill(myAct.getResources().getColor(R.color.fillGrey))
                .setDotsRadius(Tools.fromDpToPx(2))
//                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                .setDotsColor(myAct.getResources().getColor(R.color.grey_600))
                .setThickness(Tools.fromDpToPx(2));
        mChart.addData(dataSetAfterToday);


        // Chart
        mChart.setBorderSpacing(Tools.fromDpToPx(4))
                .setStep(3)
                .setGrid(LineChartView.GridType.FULL, mLineGridPaint)
                .setAxisBorderValues(0, highestYValue, 1)
                .setYLabels(AxisController.LabelPosition.NONE)
                .setLabelsColor(myAct.getResources().getColor(R.color.grey_600))
                .setYLabels(YController.LabelPosition.OUTSIDE)
                .setXAxis(false)
                .setYAxis(false);

        mChart.animateSet(0, new DashAnimation());

        mChart.show(getAnimation(true).setEndAction(null));
    }

    private Animation getAnimation(boolean newAnim) {
            return new Animation()
                    .setAlpha(mCurrAlpha)
                    .setEasing(mCurrEasing)
                    .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                    .setStartPoint(mCurrStartX, mCurrStartY);
    }

    public void clean() {
        try {
            mChart.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
//        History.clear();
//
//        try {
//            while (true) {
//                beforeToday.removeLast();
//            }
//        } catch (NoSuchElementException e) {
//            /* End of Series */
//        }
//        try {
//            while (true) {
//                today.removeLast();
//            }
//        } catch (NoSuchElementException e) {
//            /* End of Series */
//        }
//        try {
//            while (true) {
//                afterToday.removeLast();
//            }
//        } catch (NoSuchElementException e) {
//            /* End of Series */
//        }
//
//        History.redraw();
    }
}
