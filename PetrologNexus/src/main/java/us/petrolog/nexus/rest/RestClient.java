package us.petrolog.nexus.rest;

/**
 * Created by carlos on 2/26/16.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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
    private ApiService apiService;
    private Context mContext;
    private String mFullAuthString;

    public RestClient(Context applicationContext) {

        mContext = applicationContext;

        SharedPreferences settings =
                PreferenceManager.getDefaultSharedPreferences(mContext);
        boolean isUserLogged = settings.getBoolean(Constants.SP_IS_USER_LOGGED, false);

        if (isUserLogged) {
            String userEmailFromSP = settings.getString(Constants.SP_USER_EMAIL, "");
            String userPasswordFromSP = settings.getString(Constants.SP_USER_PASSWORD, "");
            String auth = new String(Base64.encode((userEmailFromSP + ":" + userPasswordFromSP).getBytes(), Base64.URL_SAFE | Base64.NO_WRAP));
            mFullAuthString = "Basic " + auth + "," + Constants.API_KEY; // This should look something like this Authorization: Basic c29wb3J0ZUBpbnRlbGVjdGl4LmNvbToxMjM0NTY=,ApiKey= zkPlklei#

        } else {
            mFullAuthString = Constants.API_KEY;
        }

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
                .create();


        Interceptor interceptor = new Interceptor() {
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request request = chain.request();

                Request newRequest = request.newBuilder()
                        .addHeader("Authorization", mFullAuthString)
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
                .baseUrl(Constants.BASE_URL)
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