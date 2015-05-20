package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.presenters.InitAppPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.InitAppView;

/**
 *  InitAppActivity.
 *
 *  According to clean code and model, use InitAppView, InitAppPresenter for future use.
 *
 *  Main Activity of the app, launch from manifest.
 *
 *  First activity when the user open the app.
 *
 *  Show a dummy splash screen and initialize all data needed to start
 *
 */

public class InitAppActivity extends Activity implements InitAppView {

    /**
     * LOG_TAG
     */
    private final String LOG_TAG = this.getClass().getSimpleName();

    /**
     * Init app presenter. Needed to expand app context between model layers.
     */
    private InitAppPresenter initAppPresenter;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_init_app);

        initAppPresenter = new InitAppPresenter(this, getApplicationContext());

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onResume(){
        super.onResume();

        initAppPresenter.start();
    }


    /*+++++++++*/
    /* SESSION */
    /*+++++++++*/

    private boolean isSessionActive() {
        SharedPreferences config = getApplicationContext()
                .getSharedPreferences("USER_INFO", MODE_PRIVATE);
        boolean remembered = config.getBoolean("rememberUser", false);
        String sessionCookie = config.getString("sessionCookie", null);
        String rememberMeCookie = config.getString("rememberMeCookie", null);
        if (remembered && sessionCookie != null && rememberMeCookie != null) {
           /*
              Falla getApiHeaders()

            VideonaApplication app = (VideonaApplication) getApplication();
            app.getApiHeaders().setSessionCookieValue(sessionCookie);
            app.getApiHeaders().setRememberMeCookieValue(rememberMeCookie);
            ApiClient apiClient = app.getApiClient();
          */

            //apiClient.getUserName();
            //TODO Check session against server
            return true;
        }
        return false;
    }

    @Override
    public void navigate(Class cls) {

        startActivity(new Intent(getApplicationContext(), cls));

    }




}