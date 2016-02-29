package us.petrolog.nexus.rest.service;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import us.petrolog.nexus.rest.model.Device;

/**
 * Created by carlos on 2/26/16.
 */
public interface ApiService {

    @Headers({"Authorization: Basic aXNhYWMub2plZGFAaW50ZWxlY3RpeC5jb206MTIzNDU2,ApiKey=Q2VzYXJBbmRyb2lkQXBw",
            "Content-Type: application/json"})
    @GET("/api/v2/devices")
    public Call<List<Device>> getDevices();
}
