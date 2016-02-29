package us.petrolog.nexus.rest;

/**
 * Created by carlos on 2/26/16.
 */

import android.util.Base64;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import us.petrolog.nexus.Constants;
import us.petrolog.nexus.rest.service.ApiService;


public class RestClient {
    private static final String BASE_URL = "http://petrolog2.azurewebsites.net";
    private ApiService apiService;

    public RestClient() {

        // TODO don't hardcore this
        final String auth = new String(Base64.encode(("carlos@petrolog.us" + ":" + "Pd45v4f6Zsmk").getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();


        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request newRequest = request.newBuilder()
                        .addHeader("Authorization", "Basic " + auth + "," + Constants.API_KEY) // This should look something like this Authorization: Basic c29wb3J0ZUBpbnRlbGVjdGl4LmNvbToxMjM0NTY=,ApiKey= zkPlklei#
                        .addHeader("Content-Type", "application/json")
                        .build();
                return chain.proceed(newRequest);
            }
        };


        // Add the interceptor to OkHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.interceptors().add(interceptor);
        OkHttpClient client = builder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public ApiService getApiService() {
        return apiService;
    }
}