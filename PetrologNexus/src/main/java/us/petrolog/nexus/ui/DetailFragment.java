package us.petrolog.nexus.ui;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextSwitcher;
import android.widget.TextView;

import com.db.chart.view.LineChartView;
import com.github.mikephil.charting.charts.LineChart;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.Constants;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.R;
import us.petrolog.nexus.rest.model.DeviceDetail;
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
    private DeviceDetail mDeviceDetail;
    private DeviceGraph mDeviceGraph;

    Animation mInAnim;
    Animation mOutAnim;

    private OnFragmentInteractionListener mListener;

    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param deviceId id passed from mainActivity
     * @return A new instance of fragment DetailFragment.
     */
    public static DetailFragment newInstance(int deviceId) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.ARG_DEVICE_ID, deviceId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDeviceId = getArguments().getInt(Constants.ARG_DEVICE_ID);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);

        getDeviceDetail(mDeviceId);
        getDeviceLastGraph(mDeviceId);

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

            mTextSWFillageCurrent.setText("N/A");
            mTextViewFillageSetting.setText(state.getPercentFillage().toString());
            mTextSWFillagePumpOffDistance.setText("N/A");
        }

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
