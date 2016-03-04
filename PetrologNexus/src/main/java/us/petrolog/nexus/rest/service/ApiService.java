package us.petrolog.nexus.rest.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import us.petrolog.nexus.rest.model.Device;
import us.petrolog.nexus.rest.model.DeviceDetail;
import us.petrolog.nexus.rest.model.DeviceEfficiency;
import us.petrolog.nexus.rest.model.DeviceGraph;

/**
 * This interface holds the usable API methods
 */
public interface ApiService {

    @GET("/api/v2/devices")
    Call<List<Device>> getDevices();

    @GET("/api/v2/devices/{deviceId}")
    Call<DeviceDetail> getDeviceDetail(@Path("deviceId") Integer deviceId);

    @GET("/api/v2/graph/{deviceId}")
    Call<DeviceGraph> getDeviceGraph(@Path("deviceId") Integer deviceId);

    @GET("/api/v2/graph/{deviceId}/efficiency")
    Call<List<DeviceEfficiency>> getDeviceEfficiency(@Path("deviceId") Integer deviceId);

}
