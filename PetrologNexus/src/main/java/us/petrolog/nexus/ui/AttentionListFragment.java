package us.petrolog.nexus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.github.amlcurran.showcaseview.ShowcaseView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.R;
import us.petrolog.nexus.events.StartDetailFragmentEvent;
import us.petrolog.nexus.misc.DevicesDetailAdapter;
import us.petrolog.nexus.misc.EmptyRecyclerView;
import us.petrolog.nexus.misc.RecyclerViewClickListener;
import us.petrolog.nexus.rest.model.DeviceDetail;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AttentionListFragment extends Fragment implements RecyclerViewClickListener {
    private static final String TAG = AttentionListFragment.class.getSimpleName();
    @Bind(R.id.my_recycler_view)
    EmptyRecyclerView mRecyclerView;
    @Bind(R.id.progressBarAttention)
    ProgressBar mProgressBarAttention;

    private List<DeviceDetail> mDeviceDetailList;

    private DevicesDetailAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private ShowcaseView mShowcaseView;
    private int mShowCaseCounter = 0;

    public AttentionListFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_attention_list, container, false);
        ButterKnife.bind(this, view);

        mProgressBarAttention.setVisibility(View.GONE);
        getDevicesThatNeedAttention();

        if (mDeviceDetailList == null) {
            mRecyclerView.setVisibility(View.GONE);
        } else if (mDeviceDetailList.isEmpty()) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new DevicesDetailAdapter(mDeviceDetailList, this);
            mRecyclerView.setAdapter(mAdapter);
//            DetailFragment.bus.post(new CurrentSelectedMicrologEvent(null, true)); //telephone is null, thus is empty
        } else {
            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new DevicesDetailAdapter(mDeviceDetailList, this);
            mRecyclerView.setAdapter(mAdapter);
        }

        setHasOptionsMenu(true);

        return view;
    }

    /**
     * goes to the backend to get all the devices that this user can see
     */
    private void getDevicesThatNeedAttention() {

        mProgressBarAttention.setVisibility(View.VISIBLE);

        mDeviceDetailList = new ArrayList<>();

        Call<List<DeviceDetail>> call = FirstApp.getRestClient().getApiService().getDevicesNeedAttention();
        call.enqueue(new Callback<List<DeviceDetail>>() {
            @Override
            public void onResponse(Call<List<DeviceDetail>> call, Response<List<DeviceDetail>> response) {
                mProgressBarAttention.setVisibility(View.GONE);
                if (response.body() != null) {
                    mDeviceDetailList = response.body();
                    drawTheList();
                    Log.d(TAG, "Callback devices that need attention successfully returned");
                }
            }

            @Override
            public void onFailure(Call<List<DeviceDetail>> call, Throwable t) {
                mProgressBarAttention.setVisibility(View.GONE);
                Log.e(TAG, "Callback devices that need attention failed");

            }
        });
    }

    public void drawTheList() {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DevicesDetailAdapter(mDeviceDetailList, this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setActionBarTitle(getResources().getString(R.string.topbar_title_attention), null, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        DeviceDetail currentDevice = mDeviceDetailList.get(position);
        MainActivity.mBus.post(new StartDetailFragmentEvent(currentDevice.getRemoteDeviceId(), currentDevice.getName(), currentDevice.getLocation()));

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_attention, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_help_attention) {
            showShowcaseHelp();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showShowcaseHelp() {

        // this is to put the button on the left
        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lps.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        mShowcaseView = new ShowcaseView.Builder(getActivity())
                .setContentText(getString(R.string.help_attention))
                .setStyle(R.style.CustomShowcaseTheme4)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mShowcaseView.hide();
                        mShowCaseCounter = -1;
                    }
                })
                .build();
        mShowcaseView.setButtonText(getString(R.string.next));
        mShowcaseView.setHideOnTouchOutside(true);

    }
}
