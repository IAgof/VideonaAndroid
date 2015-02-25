package com.videonasocialmedia.videona;

import android.app.Application;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.squareup.okhttp.OkHttpClient;
import com.videonasocialmedia.videona.api.ApiClient;
import com.videonasocialmedia.videona.api.ApiHeaders;
import com.videonasocialmedia.videona.api.CustomCookieManager;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by jca on 3/2/15.
 */
public class VideonaApplication extends Application {

    CustomCookieManager manager;
    RestAdapter restAdapter;
    private ApiClient apiClient;
    private OkHttpClient client;
    private ApiHeaders apiHeaders;

    Tracker app_tracker;

    /**
     * Called when the application is starting, before any activity, service,
     * or receiver objects (excluding content providers) have been created.
     * Implementations should be as quick as possible (for example using
     * lazy initialization of state) since the time spent in this function
     * directly impacts the performance of starting the first activity,
     * service, or receiver in a process.
     * If you override this method, be sure to call super.onCreate().
     */
    @Override
    public void onCreate() {
        super.onCreate();

        //rest client setup
        client = new OkHttpClient();
        manager = new CustomCookieManager();
        apiHeaders = new ApiHeaders();
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                        //TODO cambiar el endpoint a la dirección de producción
                .setEndpoint("http://192.168.0.22/Videona/web/app_dev.php/api")
                .setRequestInterceptor(apiHeaders)
                .build();
        apiClient = restAdapter.create(ApiClient.class);

        //Analytics setup
        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
        app_tracker = analytics.newTracker(R.xml.app_tracker);
        app_tracker.enableAdvertisingIdCollection(true);
    }

    /**
     * @return single Videona api client
     */
    public ApiClient getApiClient() {
        return apiClient;
    }

    /**
     * @return apiHeaders
     */
    public ApiHeaders getApiHeaders() {
        return apiHeaders;
    }

    /**
     *
     * @return google analytics tracker
     */
    public synchronized Tracker getTracker() {
        return app_tracker;
    }
}
