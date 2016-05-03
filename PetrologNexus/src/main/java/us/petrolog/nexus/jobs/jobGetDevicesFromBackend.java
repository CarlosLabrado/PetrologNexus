package us.petrolog.nexus.jobs;

import android.util.Log;

import com.birbit.android.jobqueue.Job;
import com.birbit.android.jobqueue.Params;
import com.birbit.android.jobqueue.RetryConstraint;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import us.petrolog.nexus.FirstApp;
import us.petrolog.nexus.events.SendDeviceListEvent;
import us.petrolog.nexus.rest.model.Device;
import us.petrolog.nexus.ui.MapPetrologFragment;

/**
 * goes to the backend to get all the devices that this user can see
 */
public class JobGetDevicesFromBackend extends Job {
    boolean responseOk = false;
    boolean retry = true;
    List<Device> devices = new ArrayList<>();


    public JobGetDevicesFromBackend() {
        super(new Params(1000).requireNetwork().groupBy("getDevices"));
    }

    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {


        Call<List<Device>> call = FirstApp.getRestClient().getApiService().getDevices();
        call.enqueue(new Callback<List<Device>>() {
            @Override
            public void onResponse(Call<List<Device>> call, Response<List<Device>> response) {
                if (response.body() != null) {
                    for (int i = 0; i < response.body().size(); i++) {
                        devices.add(response.body().get(i));
                        response.body();
                    }
                    MapPetrologFragment.mBus.post(new SendDeviceListEvent(devices, false));
                    //getDeviceDetail(devices.get(0).getRemoteDeviceId());

                    Log.d("job", "Callback successfully returned");
                }

            }

            @Override
            public void onFailure(Call<List<Device>> call, Throwable t) {
                Log.e("job", "Callback failed");
            }
        });

    }

    @Override
    protected void onCancel(int cancelReason) {
        responseOk = false;
    }

    @Override
    protected RetryConstraint shouldReRunOnThrowable(Throwable throwable, int runCount, int maxRunCount) {
        return null;
    }

}
