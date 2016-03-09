package us.petrolog.nexus.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.db.chart.Tools;
import com.db.chart.model.LineSet;
import com.db.chart.model.Point;
import com.db.chart.view.AxisController;
import com.db.chart.view.LineChartView;
import com.db.chart.view.YController;
import com.db.chart.view.animation.easing.BaseEasingMethod;
import com.db.chart.view.animation.easing.BounceEase;
import com.db.chart.view.animation.easing.QuintEase;
import com.db.chart.view.animation.style.DashAnimation;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.Constants;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.R;
import us.petrolog.nexus.misc.IntegerComparator;
import us.petrolog.nexus.misc.Utility;
import us.petrolog.nexus.misc.XYMerger;
import us.petrolog.nexus.rest.model.Coordinate;
import us.petrolog.nexus.rest.model.DeviceDetail;
import us.petrolog.nexus.rest.model.DeviceEfficiency;
import us.petrolog.nexus.rest.model.DeviceGraph;
import us.petrolog.nexus.rest.model.State;

/**
 * The detail fragment that will be called when the user clicks on the info window of a marker
 */
public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getSimpleName();

    @Bind(R.id.text_sw_well_status)
    TextSwitcher mTextSwWellStatus;
    @Bind(R.id.text_sw_pump_off)
    TextSwitcher mTextSwPumpOff;
    @Bind(R.id.text_sw_last_cycle)
    TextSwitcher mTextSwLastCycle;
    @Bind(R.id.runtime_today)
    ProgressBar mProgressBarRuntimeToday;
    @Bind(R.id.today_runtime_percentTV)
    TextView mTextViewTodayRuntimePercent;
    @Bind(R.id.today_runtime_time)
    TextView mTextViewTodayRuntimeTime;
    @Bind(R.id.runtime_yesterday)
    ProgressBar mProgressBarRuntimeYesterday;
    @Bind(R.id.yesterday_runtime_percentTV)
    TextView mTextViewYesterdayRuntimePercent;
    @Bind(R.id.yesterday_runtime_time)
    TextView mTextViewYesterdayRuntimeTime;
    @Bind(R.id.linechartHistory)
    LineChartView mLinechartHistory;
    @Bind(R.id.linechartGraph)
    LineChart mLinechartGraph;
    @Bind(R.id.progressBarDyna)
    ProgressBar mProgressBarDyna;
    @Bind(R.id.strokes_pump_up)
    TextView mTextViewStrokesPumpUp;
    @Bind(R.id.strokes_pump_off)
    TextView mTextViewStrokesPumpOff;
    @Bind(R.id.strokes_current_time_out)
    TextView mTextViewStrokesCurrentTimeOut;
    @Bind(R.id.strokes_automatic_time_out)
    TextView mTextViewStrokesAutomaticTimeOut;
    @Bind(R.id.fillage_current)
    TextSwitcher mTextSWFillageCurrent;
    @Bind(R.id.fillage_setting)
    TextView mTextViewFillageSetting;
    @Bind(R.id.fillage_pump_off_distance)
    TextSwitcher mTextSWFillagePumpOffDistance;
    @Bind(R.id.progressBar)
    ProgressBar mProgressBarWait;

    private int mDeviceId;
    private String mDeviceName;
    private String mLocationName;
    private DeviceDetail mDeviceDetail;
    private DeviceGraph mDeviceGraph;
    private List<DeviceEfficiency> mDeviceEfficiencyList;

    // MPChart for XY
    Animation mInAnim;
    Animation mOutAnim;

    static int mMinX;
    static int mMaxX;
    static int mMinY;
    static int mMaxY;

    static ArrayList<ArrayList<Entry>> mBackup = new ArrayList<>();

    // WilliamLinechartHistory for History
    private static float mCurrOverlapFactor;
    private static int[] mCurrOverlapOrder;
    // Ease
    private static BaseEasingMethod mCurrEasing;
    // Enter
    private static float mCurrStartX;
    private static float mCurrStartY;
    // Alpha
    private static int mCurrAlpha;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param deviceId id passed from mainActivity
     * @param name
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(int deviceId, String name, String locationName) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_DEVICE_ID, deviceId);
        args.putString(Constants.ARG_DEVICE_NAME, name);
        args.putString(Constants.ARG_DEVICE_LOCATION_NAME, locationName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceId = getArguments().getInt(Constants.ARG_DEVICE_ID);
            mDeviceName = getArguments().getString(Constants.ARG_DEVICE_NAME);
            mLocationName = getArguments().getString(Constants.ARG_DEVICE_LOCATION_NAME);
        }
        mBackup = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        /** History Chart things **/
        mCurrOverlapFactor = .5f;
        mCurrEasing = new QuintEase();
        mCurrStartX = -1;
        mCurrStartY = 0;
        mCurrAlpha = -1;

        getDeviceDetail(mDeviceId);
        getDeviceLastGraph(mDeviceId);
        getDeviceEfficiencyList(mDeviceId);

        // Declare the in and out animations and initialize them
        mInAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_in);
        mOutAnim = AnimationUtils.loadAnimation(getContext(), R.anim.push_down_out);

        mTextSwWellStatus.setInAnimation(mInAnim);
        mTextSwWellStatus.setOutAnimation(mOutAnim);
        mTextSWFillageCurrent.setInAnimation(mInAnim);
        mTextSWFillageCurrent.setOutAnimation(mOutAnim);
        mTextSWFillagePumpOffDistance.setInAnimation(mInAnim);
        mTextSWFillagePumpOffDistance.setOutAnimation(mOutAnim);
        mTextSwLastCycle.setInAnimation(mInAnim);
        mTextSwLastCycle.setOutAnimation(mOutAnim);
        mTextSwPumpOff.setInAnimation(mInAnim);
        mTextSwPumpOff.setOutAnimation(mOutAnim);

        return view;
    }

    private void updateUI() {

        if (mDeviceDetail != null) {
            State state = mDeviceDetail.getState();

            String pumpOff;
            if (state.getPumpOff()) {
                pumpOff = "Yes";
            } else {
                pumpOff = "No";
            }
            mTextSwWellStatus.setText(state.getRemoteDeviceStatusDescription());
            mTextSwPumpOff.setText(pumpOff);
            mTextSwLastCycle.setText(state.getStrokesThisCycle().toString());

            mTextViewTodayRuntimePercent.setText("N/A");
            mTextViewTodayRuntimeTime.setText("N/A");
            mTextViewYesterdayRuntimePercent.setText("N/A");
            mTextViewYesterdayRuntimeTime.setText("N/A");

            mTextViewStrokesPumpUp.setText("N/A");
            mTextViewStrokesPumpOff.setText("N/A");
            mTextViewStrokesCurrentTimeOut.setText(state.getTimeOut().toString());
            String automaticTimeOut;
            if (state.getAutomatic()) {
                automaticTimeOut = "Yes";
            } else {
                automaticTimeOut = "No";
            }
            mTextViewStrokesAutomaticTimeOut.setText(automaticTimeOut);

            mTextSWFillageCurrent.setText(state.getPercentFillage().toString());
            mTextViewFillageSetting.setText(state.getPercentFillageSetting().toString());
            mTextSWFillagePumpOffDistance.setText("N/A");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(mDeviceName + " - " + mLocationName + " - " + mDeviceId, null, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    /**
     * Goes to the backend and gets the full detail of the device (minus the graph)
     *
     * @param remoteDeviceId self
     */
    private void getDeviceDetail(int remoteDeviceId) {

        Call<DeviceDetail> call = FirstApp.getRestClient().getApiService().getDeviceDetail(remoteDeviceId);
        call.enqueue(new Callback<DeviceDetail>() {
            @Override
            public void onResponse(Call<DeviceDetail> call, Response<DeviceDetail> response) {
                if (response.body() != null) {
                    mDeviceDetail = response.body();
                    updateUI();
                    Log.d(TAG, "Callback device detail successfully returned");
                }
            }

            @Override
            public void onFailure(Call<DeviceDetail> call, Throwable t) {
                Log.e(TAG, "Callback device detail failed");

            }
        });
    }

    /**
     * Goes to the backend and gets the graph of the device
     *
     * @param remoteDeviceId self
     */
    private void getDeviceLastGraph(int remoteDeviceId) {
        Call<DeviceGraph> call = FirstApp.getRestClient().getApiService().getDeviceGraph(remoteDeviceId);
        call.enqueue(new Callback<DeviceGraph>() {
            @Override
            public void onResponse(Call<DeviceGraph> call, Response<DeviceGraph> response) {
                if (response.body() != null) {
                    mDeviceGraph = response.body();
                    updateUI();
                    updateXYGraph();
                    Log.d(TAG, "Callback device graph successfully returned");
                }
            }

            @Override
            public void onFailure(Call<DeviceGraph> call, Throwable t) {
                Log.e(TAG, "Callback device graph failed");
            }
        });
    }

    /**
     * Gets the efficiency from the backend, it gets 4 readings per day, these are classified by:
     * - the "number" field, from 1 to 4
     * - the efficiency is the runtime percentage
     * - the percentFillage is the average of efficiency
     * - the percentFillageSetting is the configured desired efficiency
     *
     * @param remoteDeviceId self
     */
    private void getDeviceEfficiencyList(int remoteDeviceId) {
        mDeviceEfficiencyList = new ArrayList<>();
        Call<List<DeviceEfficiency>> call = FirstApp.getRestClient().getApiService().getDeviceEfficiency(remoteDeviceId);
        call.enqueue(new Callback<List<DeviceEfficiency>>() {
            @Override
            public void onResponse(Call<List<DeviceEfficiency>> call, Response<List<DeviceEfficiency>> response) {
                mDeviceEfficiencyList = response.body();
                updateHistoryGraph(mDeviceEfficiencyList);
                Log.d(TAG, "Callback device efficiency successfully returned");
            }

            @Override
            public void onFailure(Call<List<DeviceEfficiency>> call, Throwable t) {
                Log.e(TAG, "Callback device efficiency failed");

            }
        });

    }

    private void updateHistoryGraph(List<DeviceEfficiency> deviceEfficiencyList) {

        Calendar c = Calendar.getInstance();
        int today = c.get(Calendar.DAY_OF_MONTH);
        int currentMonth = c.get(Calendar.MONTH);

        try {
            LineSet dataSetBeforeToday = new LineSet();
            LineSet dataSetToday = new LineSet();
            LineSet dataSetLastMonth = new LineSet();
            LineSet dataSetAverageFillage = new LineSet();
            LineSet dataSetConfiguredFillage = new LineSet();
            LineSet bottom = new LineSet();

            int highestYValue = 0;
            int maxDrawYValue = 100; // this will normally be 100 all the time but, some things happen
            int stepForYValues = 10;

            Point point;
            Point dummyPoint;
            Point averageFillagePoint;
            Point configuredFillagePoint;
            Point dummyPointBottom;

            // we use these to know where to start/stop drawing each of the charts
            int indexBeforeToday = 0;
            int indexLastMonth = 0;

            /** The logic behind the dummyPoint is that, for this charts we can't just put a chart
             * that is not the same size that the other ones, so we fill it with dummy transparent
             * points and only when is "his turn" we fill it with an actual reading
             */

            for (int i = 0; i < deviceEfficiencyList.size(); i++) {
                DeviceEfficiency deviceEfficiency = deviceEfficiencyList.get(i);

                int efficiency = deviceEfficiency.getEfficiency();
                int averageFillage = deviceEfficiency.getPercentFillage();
                int configuredFillage = deviceEfficiency.getPercentFillageSetting();

                Calendar date = Utility.getFormattedDate(deviceEfficiency.getDateTimeStamp());
                SimpleDateFormat formatter = new SimpleDateFormat("MMM-dd");
                String dateString = formatter.format(date.getTime());

                // we will only want to draw the date for the first of the four date values of that day
                if (deviceEfficiency.getNumber() == 1) {
                    point = new Point(dateString, efficiency);
                    dummyPoint = new Point(dateString, 0);
                    averageFillagePoint = new Point(dateString, averageFillage);
                    configuredFillagePoint = new Point(dateString, configuredFillage);
                    dummyPointBottom = new Point(dateString, 0);
                } else {
                    point = new Point("", efficiency);
                    dummyPoint = new Point("", 0);
                    averageFillagePoint = new Point("", averageFillage);
                    configuredFillagePoint = new Point("", configuredFillage);
                    dummyPointBottom = new Point("", 0);
                }
                dummyPoint.setColor(getContext().getResources().getColor(R.color.transparent));
                dummyPoint.setRadius(2);

                //dataSetToday.addPoint(point);

                if (today == date.get(Calendar.DAY_OF_MONTH)) {
                    dataSetToday.addPoint(point);
                    dataSetBeforeToday.addPoint(dummyPoint);
                    dataSetLastMonth.addPoint(dummyPoint);
                } else if (currentMonth == date.get(Calendar.MONTH)) {
                    dataSetBeforeToday.addPoint(point);
                    dataSetLastMonth.addPoint(dummyPoint);
                    dataSetToday.addPoint(dummyPoint);

                    indexBeforeToday = i;
                } else {
                    dataSetLastMonth.addPoint(point);
                    dataSetBeforeToday.addPoint(dummyPoint);
                    dataSetToday.addPoint(dummyPoint);

                    indexLastMonth = i;
                }
                dataSetAverageFillage.addPoint(averageFillagePoint);
                dataSetConfiguredFillage.addPoint(configuredFillagePoint);
                bottom.addPoint(dummyPointBottom);
                if (highestYValue <= efficiency) {
                    highestYValue = efficiency;
                }
            }


            dataSetAverageFillage.setColor(getContext().getResources().getColor(R.color.green_300))
                    .setDotsRadius(Tools.fromDpToPx(2))
                    .setDotsColor(getContext().getResources().getColor(R.color.green_300))
                    .setThickness(Tools.fromDpToPx(2));
            mLinechartHistory.addData(dataSetAverageFillage);


            dataSetConfiguredFillage.setColor(getContext().getResources().getColor(R.color.yellow_300))
                    .setDotsRadius(Tools.fromDpToPx(2))
                    .setDotsColor(getContext().getResources().getColor(R.color.yellow_300))
                    .setThickness(Tools.fromDpToPx(2));
            mLinechartHistory.addData(dataSetConfiguredFillage);

            Paint mLineGridPaint = new Paint();
            mLineGridPaint.setColor(getContext().getResources().getColor(R.color.gridBlue));
            mLineGridPaint.setPathEffect(new DashPathEffect(new float[]{4, 4}, 0));
            mLineGridPaint.setStyle(Paint.Style.STROKE);
            mLineGridPaint.setAntiAlias(true);
            mLineGridPaint.setStrokeWidth(Tools.fromDpToPx(.5f));

            dataSetBeforeToday.setColor(getContext().getResources().getColor(R.color.blue_600))
                    .setFill(getContext().getResources().getColor(R.color.fillBlue))
                    .setDotsRadius(Tools.fromDpToPx(2))
                    //.setDashed(new float[]{10, 10})
//                .setDotsStrokeThickness(Tools.fromDpToPx(2))
                    .setDotsColor(getContext().getResources().getColor(R.color.blue_800))
                    .beginAt(indexLastMonth)
                    .endAt(indexBeforeToday + 1)
                    .setThickness(Tools.fromDpToPx(2));
            mLinechartHistory.addData(dataSetBeforeToday);

            dataSetToday.setColor(getContext().getResources().getColor(R.color.red_600))
                    .setFill(getContext().getResources().getColor(R.color.fillRed))
                    .setDotsRadius(Tools.fromDpToPx(2))
                    .setDotsColor(getContext().getResources().getColor(R.color.red_600))
                    .beginAt(indexBeforeToday)
                    .setThickness(Tools.fromDpToPx(2));
            mLinechartHistory.addData(dataSetToday);

            dataSetLastMonth.setColor(getContext().getResources().getColor(R.color.grey_600))
                    .setFill(getContext().getResources().getColor(R.color.fillGrey))
                    .setDotsRadius(Tools.fromDpToPx(2))
                    .setDotsColor(getContext().getResources().getColor(R.color.grey_600))
                    .endAt(indexLastMonth + 1)
                    .setThickness(Tools.fromDpToPx(2));
            mLinechartHistory.addData(dataSetLastMonth);


            // These are weird cases, but we don't want the graph to look ugly
            if (highestYValue > maxDrawYValue) {
                maxDrawYValue = highestYValue;
                stepForYValues = Utility.GCD(0, maxDrawYValue);
//                if (stepForYValues == maxDrawYValue) { // we don't want to show just one number?
//                    stepForYValues = 1;
//                }
            }

            // Chart
            mLinechartHistory.setBorderSpacing(Tools.fromDpToPx(10))
                    .setStep(1)
                    .setGrid(LineChartView.GridType.FULL, mLineGridPaint)
                    .setAxisBorderValues(0, maxDrawYValue, stepForYValues) // Normally 0, 100, 10
                    .setYLabels(AxisController.LabelPosition.NONE)
                    .setLabelsColor(getContext().getResources().getColor(R.color.grey_600))
                    .setYLabels(YController.LabelPosition.OUTSIDE)
                    .setXAxis(false)
                    .setYAxis(false);

            mLinechartHistory.setPadding(10, 60, 10, 10);

            mLinechartHistory.animateSet(0, new DashAnimation());

            com.db.chart.view.animation.Animation anim = new com.db.chart.view.animation.Animation()
                    .setEasing(new BounceEase());
            mLinechartHistory.show(anim);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * For the History chart
     *
     * @return
     */
    private com.db.chart.view.animation.Animation getAnimation() {
        return new com.db.chart.view.animation.Animation()
                .setAlpha(mCurrAlpha)
                .setEasing(mCurrEasing)
                .setOverlap(mCurrOverlapFactor, mCurrOverlapOrder)
                .setStartPoint(mCurrStartX, mCurrStartY);
    }

    private void updateXYGraph() {

        if (mDeviceGraph != null) {
            MergeAndCalculate task = new MergeAndCalculate();
            task.setListener(new MergeAndCalculateTaskListener() {
                @Override
                public void onComplete(ArrayList<ArrayList> chartValues, Exception e) {
                    buildChart(chartValues);
                    updateXYGraph();
                }
            }).execute(mDeviceGraph);
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
            set1.setColor(getContext().getResources().getColor(R.color.mainBlue));
            set1.setCircleColor(getContext().getResources().getColor(R.color.mainBlue));
            set1.setLineWidth(Tools.fromDpToPx(1));
//            set1.setCircleRadius(Tools.fromDpToPx(1)/2);
            set1.setDrawCircleHole(false);
            set1.setDrawValues(false);
            set1.setDrawCubic(true);
            set1.setDrawCircles(false);
            set1.setFillAlpha(65);
            set1.setFillColor(getContext().getResources().getColor(R.color.mainBlue));
//        set1.setDrawFilled(true);
            // set1.setShader(new LinearGradient(0, 0, 0, mLinechartHistory.getHeight(),
            // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

            // create a dataset and give it a type
            LineDataSet set2 = new LineDataSet(yVals2, "DataSet Y");
            // set1.setFillAlpha(110);
            // set1.setFillColor(Color.RED);

            // set the line to be drawn like this "- - - - - -"
//        set2.enableDashedLine(15f, 10f, 0f);
//        set2.enableDashedHighlightLine(10f, 5f, 0f);
            set2.setColor(getContext().getResources().getColor(R.color.mainBlue));
            set2.setCircleColor(getContext().getResources().getColor(R.color.mainBlue));
            set2.setLineWidth(Tools.fromDpToPx(1));
//            set2.setCircleRadius(Tools.fromDpToPx(1)/2);
            set2.setDrawCircles(false);
            set2.setDrawCircleHole(false);
            set2.setDrawValues(false);
            set2.setDrawCubic(true);
            set2.setFillAlpha(65);
            set2.setFillColor(getContext().getResources().getColor(R.color.mainBlue));
            set2.setLabel("");
//        set2.setDrawFilled(true);
            // set1.setShader(new LinearGradient(0, 0, 0, mLinechartHistory.getHeight(),
            // Color.BLACK, Color.WHITE, Shader.TileMode.MIRROR));

            ArrayList<ILineDataSet> dataSets = new ArrayList<>();


            if (!mBackup.isEmpty()) {
                int alphaDecrement = 10;
                Log.e("backup", "not empy");
                for (int i = mBackup.size() - 1; i >= 0; i = i - 2) {
                    // create a dataset and give it a type
                    LineDataSet set3 = new LineDataSet(mBackup.get(i), "");

                    // set the line to be drawn like this "- - - - - -"
//                    set3.enableDashedLine(10f, 10f, 0f);
                    set3.setColor(ColorUtils.setAlphaComponent(Color.RED, 50 - (alphaDecrement)));
//                    set3.setCircleColor(getContext().getResources().getColor(R.color.mainRedAlpha));
                    set3.setLineWidth(Tools.fromDpToPx(1));
                    set3.setDrawCircles(false);

                    set3.setDrawCircleHole(false);
                    set3.setDrawValues(false);
                    set3.setDrawCubic(true);
                    set3.setFillAlpha(65 + i);
                    set3.setFillColor(getContext().getResources().getColor(R.color.grey_50));

                    // create a dataset and give it a type
                    LineDataSet set4 = new LineDataSet(mBackup.get(i - 1), "");

                    // set the line to be drawn like this "- - - - - -"
//                    set4.enableDashedLine(10f, 10f, 0f);
                    set4.setColor(ColorUtils.setAlphaComponent(Color.RED, 50 - (alphaDecrement)));

//                    set4.setCircleColor(getContext().getResources().getColor(R.color.mainRedAlpha));
                    set4.setLineWidth(Tools.fromDpToPx(1));
                    set4.setDrawCircles(false);
                    set4.setDrawCircleHole(false);
                    set4.setDrawValues(false);
                    set4.setDrawCubic(true);
                    set4.setFillAlpha(65 + i);
                    set4.setFillColor(getContext().getResources().getColor(R.color.grey_50));

                    dataSets.add(set3);
                    dataSets.add(set4);
                    alphaDecrement = alphaDecrement + 5;
                }

                if (mBackup.size() > 10) {
                    mBackup.remove(0);
                    mBackup.remove(1);
                }

//                mBackup.clear();
            }

            dataSets.add(set1); // add the datasets
            dataSets.add(set2); // add the datasets

            mBackup.add(yVals);
            mBackup.add(yVals2);

            // create a data object with the datasets
            LineData data = new LineData(xVals, dataSets);

            int visibleRange = mMaxY - mMinX;

            // set data
            mLinechartGraph.setData(data);
            mLinechartGraph.invalidate();
//            mLinechartGraph.setVisibleYRangeMaximum(visibleRange, YAxis.AxisDependency.LEFT);
//            mLinechartGraph.moveViewToX((mMaxX - (visibleRange / 2)) + 10);
//            mLinechartGraph.moveViewToY((mMaxY - (visibleRange / 2)) + 10, YAxis.AxisDependency.LEFT);

            mLinechartGraph.setDescription("");
            mLinechartGraph.setDrawGridBackground(false);

            YAxis rightAxis = mLinechartGraph.getAxisRight();
            rightAxis.setDrawGridLines(false);
            rightAxis.setDrawLabels(false);
            rightAxis.setAxisLineColor(getContext().getResources().getColor(R.color.gridBlue));

            YAxis leftAxis = mLinechartGraph.getAxisLeft();
            leftAxis.setGridColor(getContext().getResources().getColor(R.color.gridBlue));
            leftAxis.enableGridDashedLine(4f, 4f, 0f);
            leftAxis.setTextColor(getContext().getResources().getColor(R.color.mainGray));
            leftAxis.setAxisLineColor(getContext().getResources().getColor(R.color.gridBlue));


            XAxis xAxis = mLinechartGraph.getXAxis();
            xAxis.setGridColor(getContext().getResources().getColor(R.color.gridBlue));
            xAxis.enableGridDashedLine(4f, 4f, 0f);
            xAxis.setDrawAxisLine(true);
            xAxis.setTextColor(getContext().getResources().getColor(R.color.mainGray));
            xAxis.setAxisLineColor(getContext().getResources().getColor(R.color.gridBlue));

//            xAxis.setXOffset(20);


            mLinechartGraph.setClickable(false);
            mLinechartGraph.setClipChildren(false);
            Legend legend = mLinechartGraph.getLegend();
            legend.setCustom(
                    new int[]{
                            getContext().getResources().getColor(R.color.mainBlue), getContext().getResources().getColor(R.color.mainRed)
                    }, new String[]{getContext().getString(R.string.legend_current), getContext().getString(R.string.legend_past)});
//            mLinechartGraph.notifyDataSetChanged();
//            mLinechartGraph.animateX(1000, Easing.getEasingFunctionFromOption(Easing.EasingOption.EaseOutQuart));

        } catch (IllegalArgumentException ex) {
            mLinechartGraph.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    public static class MergeAndCalculate extends AsyncTask<DeviceGraph, Void, ArrayList<ArrayList>> {
        ArrayList<Integer> newCoordinateOrderX = new ArrayList<>();
        ArrayList<Integer> newCoordinateOrderY = new ArrayList<>();

        ArrayList<Integer> xValuesFirst = new ArrayList<>();
        ArrayList<Integer> yValuesFirst = new ArrayList<>();
        ArrayList<Integer> xValuesSecond = new ArrayList<>();
        ArrayList<Integer> yValuesSecond = new ArrayList<>();
        //ArrayList<Integer> arrayListYvalues = new ArrayList<>();
        private Exception mError = null;
        private MergeAndCalculateTaskListener mListener = null;


        @Override
        protected ArrayList<ArrayList> doInBackground(DeviceGraph... deviceGraphs) {
            mMinX = Integer.MAX_VALUE;
            mMaxX = Integer.MIN_VALUE;
            mMinY = Integer.MAX_VALUE;
            mMaxY = Integer.MIN_VALUE;

            int minIndexX = 0;
            int maxIndexX = 0;

            DeviceGraph deviceGraph = deviceGraphs[0];
            List<Coordinate> coordinates = deviceGraph.getCoordinates();

            /**
             * We want to imagine these coordinates from "Top" to "Bottom", and expect that the
             * higher values of X are in the top. If they aren't then we reverse them to be that way
             * the whole following logic expects that.
             */
//            Log.d("newDynaSize", String.valueOf(coordinates.size()));
            for (int i = 0; i < coordinates.size(); i++) {
                int currentX = coordinates.get(i).getY();
                int currentY = coordinates.get(i).getX();
//                Log.d("Coordinates", coordinates.get(i).getX().toString() + " " + coordinates.get(i).getY().toString() + " index " + i);

                newCoordinateOrderX.add(currentX);
                newCoordinateOrderY.add(currentY);

                if (mMinX > currentX) {
                    mMinX = currentX;
                    minIndexX = i;
                }
                if (mMaxX < currentX) {
                    mMaxX = currentX;
                    maxIndexX = i;
                }
                if (mMinY > currentY) {
                    mMinY = currentY;
                }
                if (mMaxY < currentY) {
                    mMaxY = currentY;
                }

            }


//            System.out.print("min index X " + minIndexX);
//            System.out.println("\n");
//
//            System.out.print("MAX index X " + maxIndexX);
//            System.out.println("\n");
//
//            System.out.print("min x " + mMinX);
//            System.out.println("\n");
//
//            System.out.print("MAX x " + mMaxX);
//            System.out.println("\n");

//            for (int i = 0; i < newCoordinateOrderX.size(); i++) {
//                Log.d("new Coordinates", newCoordinateOrderX.get(i).toString() + " " + newCoordinateOrderY.get(i).toString() + " index " + i);
//            }

            /* We don't want this scenario because we are predicting that the top values are going to be first
               so if this happens we have to invert everything and search for the new indexes too
              */
            if (minIndexX < maxIndexX) {
                Collections.reverse(newCoordinateOrderX);
                Collections.reverse(newCoordinateOrderY);
                mMinX = Integer.MAX_VALUE;
                mMaxX = Integer.MIN_VALUE;
                for (int i = 0; i < newCoordinateOrderX.size(); i++) {
                    Log.d("new Coordinates R", newCoordinateOrderX.get(i).toString() + " " + newCoordinateOrderY.get(i).toString() + " index " + i);
                    int currentX = newCoordinateOrderX.get(i);

                    if (mMinX > currentX) {
                        mMinX = currentX;
                        minIndexX = i;
                    }
                    if (mMaxX < currentX) {
                        mMaxX = currentX;
                        maxIndexX = i;
                    }
                }
            }

            // We save the values from 0 to the maxIndexY that were left out so we can later add them
            // to the second graph
            ArrayList<Integer> valuesLeftOutX = new ArrayList<>();
            ArrayList<Integer> valuesLeftOutY = new ArrayList<>();
            for (int i = 0; i < maxIndexX; i++) {
                valuesLeftOutX.add(newCoordinateOrderX.get(i));
                valuesLeftOutY.add(newCoordinateOrderY.get(i));
            }

            // Now we go from MaxIndexY to the minIndexY this will be the first graph (this is
            // confusing because the values here go from Right to Left so in the end we must reverse
            // them
            for (int i = maxIndexX; i < minIndexX; i++) {
                xValuesFirst.add(newCoordinateOrderX.get(i));
                yValuesFirst.add(newCoordinateOrderY.get(i));
            }
            Collections.reverse(xValuesFirst);
            Collections.reverse(yValuesFirst);

            // Now the rest, from minIndexY to the full size of the coordinates list, and in the end
            // we must add the values that were left out at the beginning, we don't reverse this
            // because is in the right order, from Left to Right
            for (int i = minIndexX; i < coordinates.size(); i++) {
                xValuesSecond.add(newCoordinateOrderX.get(i));
                yValuesSecond.add(newCoordinateOrderY.get(i));
            }

            // we must reverse these too because they are taken from the top and we want them now at
            // the "bottom"
            Collections.reverse(valuesLeftOutX);
            Collections.reverse(valuesLeftOutY);

            // add the values that were left out
            for (int i = 0; i < valuesLeftOutX.size(); i++) {
                xValuesSecond.add(valuesLeftOutX.get(i));
                yValuesSecond.add(valuesLeftOutY.get(i));
            }
            return startMerging(xValuesFirst, xValuesSecond, yValuesFirst, yValuesSecond);
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

//            System.out.println(x1); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 35]
//            System.out.println(y1); // [5, null, null, null, null, null, null, null, null, 112, 23, 34, 50, 100, 130]
//
//            System.out.println("\n");
//
//            System.out.println(x2); // [0, 1, 2, 3, 4, 5, 6, 7, 8, 10, 15, 20, 25, 30, 35] // X = X1 = X2
//            System.out.println(y2); // [1, 5, 20, 15, 10, 30, 40, 70, 75, 100, null, null, null, null, null]

            // we make sure they have starting connecting points
            if (!x1.isEmpty() && !x2.isEmpty() && !y1.isEmpty() && !y2.isEmpty()) {
                try {
                    x1.add(0, x2.get(0));
                    y1.add(0, y2.get(0));
                    // final connecting points
                    if ((x1.get(x1.size() - 1)) != null && (y1.get(y1.size() - 1)) != null) {
                        int temp1 = x1.get(x1.size() - 1);
                        int temp2 = y1.get(y1.size() - 1);
//                        Log.d("size?", temp1 + " " + temp2);
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

    public interface MergeAndCalculateTaskListener {
        void onComplete(ArrayList<ArrayList> chartValues, Exception e);
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
