/*
package com.videonasocialmedia.videona.presentation.views.login;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.model.rest.ApiHeaders;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;


public class LoginActivity extends Activity {

    */
/*VIEWS*//*

    @InjectView(R.id.login_text_field)
    TextView userTextField;
    @InjectView(R.id.login_password_field)
    TextView passwordTextField;
    @InjectView(R.id.checkBox_remember_me)
    CheckBox rememberMe;

    */
/*API*//*

    private ApiClient apiClient;
    private VideonaApplication app;

    */
/*CONFIG*//*

    private SharedPreferences config;

    */
/*ANALYTICS*//*

    Tracker t;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        apiClient = app.getApiClient();
        config = getApplicationContext()
                .getSharedPreferences("USER_INFO", MODE_PRIVATE);
        t = app.getTracker();
    }


    */
/**
 * Start the activity to create a new user when the new_user_button is clicked
 *//*

    @OnClick(R.id.new_user_button)
    public void goToUserSignUpActivity() {
        startActivity(new Intent(getApplicationContext(), UserSignUpActivity.class));
    }

    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.send_login_button:
                label = "login with google";
                break;
            case R.id.login_facebook_button:
                label = "login with google";
                break;
            case R.id.login_g_plus_button:
                label = "login with google";
                break;
            case R.id.login_twitter_button:
                label = "Login with twitter";
                break;
            case R.id.new_user_button:
                label = "Create new User";
                break;
            default:
                label = "other";
        }
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Login")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(app.getBaseContext()).dispatchLocalHits();
    }

    */
/**
 * Try to login the user using the credentials provided by userTextField and passwordTextField
 *//*

    @OnClick(R.id.send_login_button)
    public void login(View v) {
        sendButtonTracked(v.getId());
        t.send(new HitBuilders.EventBuilder()
                .setCategory("Categoría")
                .setAction("Acción")
                .setLabel("Etiqueta")
                .build());


        //TODO remove next line when remember-me is working and uncomment the rest of the method

//        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(new Intent(getApplicationContext(), CameraActivity.class));
//        } else {
            startActivity(new Intent(getApplicationContext(), RecordActivity.class));
//        }

       */
/* String source = userTextField.getText().toString() + ":"
                + passwordTextField.getText().toString();
        try {
            String auth = "Basic " + Base64.encodeToString(source.getBytes("UTF-8"),
                    Base64.DEFAULT);

            int rememberQueryParam = 0;
            boolean rememberMeChecked = rememberMe.isChecked();
            if (rememberMeChecked) {
                rememberQueryParam = 1;
            }

            apiClient.login(auth, rememberQueryParam, new BasicLoginCallback());

            //Store remember user config
            config.edit().putBoolean("rememberUser", rememberMeChecked);

        } catch (UnsupportedEncodingException e) {
            Toast.makeText(getApplicationContext(), "Error durante login",
                    Toast.LENGTH_SHORT).show();
        }*//*

    }


}*/
