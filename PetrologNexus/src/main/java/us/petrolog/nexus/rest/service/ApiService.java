package us.petrolog.nexus.rest.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import us.petrolog.nexus.rest.model.Device;
import us.petrolog.nexus.rest.model.DeviceDetail;
import us.petrolog.nexus.rest.model.DeviceEfficiency;
import us.petrolog.nexus.rest.model.DeviceGraph;
import us.petrolog.nexus.rest.model.User;
import us.petrolog.nexus.rest.model.UserBody;

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

    @GET("/api/v2/devices/withAttention")
    Call<List<DeviceDetail>> getDevicesNeedAttention();

    @Headers({"Authorization: ApiKey=Q2VzYXJBbmRyb2lkQXBw", "Content-Type: application/json"})
    @POST("/api/v2/auth/login")
    Call<User> userLogin(@Body UserBody userBody);
}
