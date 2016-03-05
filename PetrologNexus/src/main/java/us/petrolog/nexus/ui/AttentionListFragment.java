package us.petrolog.nexus.ui;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.R;
import us.petrolog.nexus.misc.DevicesDetailAdapter;
import us.petrolog.nexus.misc.EmptyRecyclerView;
import us.petrolog.nexus.rest.model.DeviceDetail;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AttentionListFragment extends Fragment {
    private static final String TAG = AttentionListFragment.class.getSimpleName();
    @Bind(R.id.my_recycler_view)
    EmptyRecyclerView mRecyclerView;

    private List<DeviceDetail> mDeviceDetailList;

    private DevicesDetailAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

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
        getDevicesThatNeedAttention();

        if (mDeviceDetailList == null) {
            mRecyclerView.setVisibility(View.GONE);
        } else if (mDeviceDetailList.isEmpty()) {
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new DevicesDetailAdapter(mDeviceDetailList);
            mRecyclerView.setAdapter(mAdapter);
//            DetailFragment.bus.post(new CurrentSelectedMicrologEvent(null, true)); //telephone is null, thus is empty
        } else {
            // use a linear layout manager
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);

            mAdapter = new DevicesDetailAdapter(mDeviceDetailList);
            mRecyclerView.setAdapter(mAdapter);
        }

        return view;
    }

    /**
     * goes to the backend to get all the devices that this user can see
     */
    private void getDevicesThatNeedAttention() {

        mDeviceDetailList = new ArrayList<>();

        Call<List<DeviceDetail>> call = FirstApp.getRestClient().getApiService().getDevicesNeedAttention();
        call.enqueue(new Callback<List<DeviceDetail>>() {
            @Override
            public void onResponse(Call<List<DeviceDetail>> call, Response<List<DeviceDetail>> response) {
                if (response.body() != null) {
                    mDeviceDetailList = response.body();
                    drawTheList();
                    Log.d(TAG, "Callback devices that need attention successfully returned");
                }
            }

            @Override
            public void onFailure(Call<List<DeviceDetail>> call, Throwable t) {
                Log.e(TAG, "Callback devices that need attention failed");

            }
        });
    }

    public void drawTheList() {

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new DevicesDetailAdapter(mDeviceDetailList);
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
}
