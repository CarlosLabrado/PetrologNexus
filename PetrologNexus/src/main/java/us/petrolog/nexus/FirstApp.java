package us.petrolog.nexus;

import android.app.Application;

import us.petrolog.nexus.rest.RestClient;

/**
 * Created by carlos on 2/26/16.
 */
public class FirstApp extends Application {

    private static RestClient restClient;

    @Override
    public void onCreate() {
        super.onCreate();

        restClient = new RestClient();
    }

    public static RestClient getRestClient() {
        return restClient;
    }
}
