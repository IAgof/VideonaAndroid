/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas Abascal
 * Verónica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.views.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.mvp.presenters.LoginPresenter;
import com.videonasocialmedia.videona.presentation.mvp.views.LoginView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;


public class LoginActivity extends Activity implements LoginView {

    /*VIEWS*/
    @InjectView(R.id.login_text_field)
    TextView userTextField;
    @InjectView(R.id.login_password_field)
    TextView passwordTextField;
    @InjectView(R.id.checkBox_remember_me)
    CheckBox rememberMe;

    private VideonaApplication app;
    private LoginPresenter loginPresenter;

    /*CONFIG*/
    private SharedPreferences config;

    /*ANALYTICS*/
    Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.inject(this);

        app = (VideonaApplication) getApplication();
        tracker = app.getTracker();

        loginPresenter = new LoginPresenter(this);
    }

    /**
     * Start the activity to create a new user when the new_user_button is clicked
     */

    @OnClick(R.id.new_user_button)
    public void goToUserSignUpActivity() {
        navigate(com.videonasocialmedia.videona.presentation.views.login.UserSignUpActivity.class);
    }


    /**
     * Try to login the user using the credentials provided by userTextField and passwordTextField
     */
    @OnClick(R.id.send_login_button)
    public void login(View v) {
        loginPresenter.userPasswordLogin(userTextField.getText().toString(), passwordTextField.getText().toString());

    }

    @Override
    public void showError(int errorMessageResource) {
        Toast.makeText(this, errorMessageResource, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigate(Class<? extends Activity> activity) {
        Intent i = new Intent(getApplicationContext(), activity);
        startActivity(i);
    }

    @OnClick({R.id.send_login_button, R.id.login_facebook_button, R.id.login_g_plus_button,
            R.id.login_twitter_button, R.id.new_user_button})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id the identifier of the clicked button
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.send_login_button:
                label = "Login with google";
                break;
            case R.id.login_facebook_button:
                label = "Login with google";
                break;
            case R.id.login_g_plus_button:
                label = "Login with google";
                break;
            case R.id.login_twitter_button:
                label = "Login with twitter";
                break;
            case R.id.new_user_button:
                label = "Create new User";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("EditActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }
}
