package us.petrolog.nexus;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYGraphWidget;
import com.androidplot.xy.XYPlot;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * Created by Cesar on 7/22/13.
 */
public class wellHistoricalRuntime_post {

    MainActivity myAct;
    private XYPlot History;

    private SimpleXYSeries beforeToday;
    private Paint bTLinePaint;
    private Paint bTFillPaint;
    private LineAndPointFormatter bTLineFormat;

    private SimpleXYSeries today;
    private Paint TLinePaint;
    private Paint TFillPaint;
    private LineAndPointFormatter TLineFormat;

    private SimpleXYSeries afterToday;
    private Paint aTLinePaint;
    private Paint aTFillPaint;
    private LineAndPointFormatter aTLineFormat;

    private Number[] serie = new Number[2];

    public wellHistoricalRuntime_post(MainActivity myActivity) {

        myAct = myActivity;
        beforeToday = new SimpleXYSeries(Arrays.asList(serie),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Current Month");
        today = new SimpleXYSeries(Arrays.asList(serie),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Today");
        afterToday = new SimpleXYSeries(Arrays.asList(serie),
                SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED, "Last Month");

        History = FormatTrend.format((XYPlot) myAct.findViewById(R.id.runtimeTrend));

        ImageView lastMonthLegend = (ImageView) myAct.findViewById(R.id.last_month_color);
        ImageView todayLegend = (ImageView) myAct.findViewById(R.id.today_color);
        ImageView currentMonthLegend = (ImageView) myAct.findViewById(R.id.current_month_color);

        XYGraphWidget myWidget = History.getGraphWidget();

        myWidget.setDrawMarkersEnabled(false);

        PointLabelFormatter label = new PointLabelFormatter(Color.BLUE);
        label.vOffset = -10f;
        label.hOffset = -6f;
        Paint textPaint = new Paint();
        textPaint.setTextSize(14);
        textPaint.setTypeface(Typeface.defaultFromStyle(0));
        textPaint.setColor(Color.BLUE);
        label.setTextPaint(textPaint);
        bTLineFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.RED,
                null,
                label);

        TLineFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.RED,
                null,
                label);

        aTLineFormat = new LineAndPointFormatter(
                Color.BLUE,
                Color.RED,
                null,
                label);

        /* beforeToday paint */
        bTLinePaint = new Paint();
        bTLinePaint.setStyle(Paint.Style.STROKE);
        bTLinePaint.setStrokeWidth(5);
        bTLinePaint.setShader(new LinearGradient(0, 0, 0, 1, Color.WHITE, Color.BLUE, Shader.TileMode.REPEAT));
        bTLineFormat.setLinePaint(bTLinePaint);

        bTFillPaint = new Paint();
        bTFillPaint.setStyle(Paint.Style.FILL);
        bTFillPaint.setAlpha(150);
        bTFillPaint.setShader(new LinearGradient(0, 600, 0, 0, Color.WHITE, Color.BLUE, Shader.TileMode.REPEAT));
        bTLineFormat.setFillPaint(bTFillPaint);

        currentMonthLegend.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawPaint(bTFillPaint);
            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        History.addSeries(beforeToday, bTLineFormat);

        /* today paint */
        TLinePaint = new Paint();
        TLinePaint.setStyle(Paint.Style.STROKE);
        TLinePaint.setStrokeWidth(5);
        TLinePaint.setShader(new LinearGradient(0, 0, 0, 1, Color.WHITE, Color.BLUE, Shader.TileMode.REPEAT));
        TLineFormat.setLinePaint(TLinePaint);

        TFillPaint = new Paint();
        TFillPaint.setStyle(Paint.Style.FILL);
        TFillPaint.setAlpha(150);
        TFillPaint.setShader(new LinearGradient(0, 600, 0, 0, Color.WHITE, Color.RED, Shader.TileMode.REPEAT));
        TLineFormat.setFillPaint(TFillPaint);

        todayLegend.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawPaint(TFillPaint);
            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        History.addSeries(today, TLineFormat);

        /* after today paint */
        aTLinePaint = new Paint();
        aTLinePaint.setStyle(Paint.Style.STROKE);
        aTLinePaint.setStrokeWidth(5);
        aTLinePaint.setShader(new LinearGradient(0, 0, 0, 1, Color.WHITE, Color.BLUE, Shader.TileMode.REPEAT));
        aTLineFormat.setLinePaint(aTLinePaint);

        aTFillPaint = new Paint();
        aTFillPaint.setStyle(Paint.Style.FILL);
        aTFillPaint.setAlpha(150);
        aTFillPaint.setShader(new LinearGradient(0, 600, 0, 0, Color.WHITE, Color.GRAY, Shader.TileMode.REPEAT));
        aTLineFormat.setFillPaint(aTFillPaint);

        lastMonthLegend.setBackground(new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.drawPaint(aTFillPaint);
            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return 0;
            }
        });

        History.addSeries(afterToday, aTLineFormat);

        History.getLegendWidget().setVisible(false);

    }

    public void post() {
        clean();
        int day = 1;
        try {
            day = Integer.valueOf(MainActivity.PetrologSerialCom.getPetrologClock().substring(9, 11));
        } catch (NumberFormatException e) {
            // TODO
        }

        for (int i = 1; i < 32; i++) {
            if (i < day) {
                beforeToday.addLast(i, (MainActivity.PetrologSerialCom.getHistoricalRuntime(i) * 100) / 86400);
                ;
            }
            if (i >= day - 1 && i <= day) {
                today.addLast(i, (MainActivity.PetrologSerialCom.getHistoricalRuntime(i) * 100) / 86400);
                ;
            }
            if (i >= day) {
                afterToday.addLast(i, (MainActivity.PetrologSerialCom.getHistoricalRuntime(i) * 100) / 86400);
                ;
            }
        }
        History.addSeries(beforeToday, bTLineFormat);
        History.addSeries(today, TLineFormat);
        History.addSeries(afterToday, aTLineFormat);

        History.redraw();
    }

    public void clean() {
        History.clear();

        try {
            while (true) {
                beforeToday.removeLast();
            }
        } catch (NoSuchElementException e) {
            /* End of Series */
        }
        try {
            while (true) {
                today.removeLast();
            }
        } catch (NoSuchElementException e) {
            /* End of Series */
        }
        try {
            while (true) {
                afterToday.removeLast();
            }
        } catch (NoSuchElementException e) {
            /* End of Series */
        }

        History.redraw();
    }
}
