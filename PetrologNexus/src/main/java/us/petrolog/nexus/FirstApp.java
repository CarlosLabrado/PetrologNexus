package us.petrolog.nexus;

import android.app.Application;
import android.util.Log;

import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.log.CustomLogger;

import us.petrolog.nexus.rest.RestClient;
import us.petrolog.nexus.rest.RestClientForLogin;

/**
 * Created by carlos on 2/26/16.
 */
public class FirstApp extends Application {

    private static RestClient restClient;
    private static RestClientForLogin restClientForLogin;

    private static FirstApp instance;
    private JobManager jobManager;

    public FirstApp() {
        instance = this;
    }



    @Override
    public void onCreate() {
        super.onCreate();

        restClient = new RestClient(getApplicationContext());
        restClientForLogin = new RestClientForLogin();
        configureJobManager();

    }

    public void recreateRestClient() {
        restClient = new RestClient(getApplicationContext());
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }


                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public JobManager getJobManager() {
        return jobManager;
    }

    public static FirstApp getInstance() {
        return instance;
    }

    public static RestClient getRestClient() {
        return restClient;
    }

    public static RestClientForLogin getRestClientForLogin() {
        return restClientForLogin;
    }
}
