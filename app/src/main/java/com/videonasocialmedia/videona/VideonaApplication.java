package com.videonasocialmedia.videona;

import android.app.Application;

import com.squareup.okhttp.OkHttpClient;
import com.videonasocialmedia.videona.api.ApiClient;
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

        client = new OkHttpClient();
        manager = new CustomCookieManager();
        client.setCookieHandler(manager);
        restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(client))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                        //TODO cambiar el endpoint a la dirección de producción
                .setEndpoint("http://192.168.0.22/Videona/web/app_dev.php/api")
                .build();
        apiClient = restAdapter.create(ApiClient.class);
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

}
