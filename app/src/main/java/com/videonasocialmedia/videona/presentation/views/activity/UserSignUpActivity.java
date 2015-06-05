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

package com.videonasocialmedia.videona.presentation.views.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.mvp.presenters.SignUpPresenter;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by jca on 22/1/15.
 */
public class UserSignUpActivity extends Activity implements RegisterView {

    /*VIEWS*/
    @InjectView(R.id.register_text_field)
    TextView userName;
    @InjectView(R.id.email_text_field)
    TextView email;
    @InjectView(R.id.register_password)
    TextView password;
    @InjectView(R.id.register_confirm_password)
    TextView confirmPassword;

    /*CONFIG*/
    SignUpPresenter presenter;
    /**
     * Tracker google analytics
     */
    private VideonaApplication app;
    private Tracker tracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        ButterKnife.inject(this);
        app = (VideonaApplication) getApplication();
        tracker = app.getTracker();
        presenter = new SignUpPresenter(this);

    }

    @OnClick(R.id.register_button)
    public void register() {
        String pass, confirmPass, usrName, email;

        pass = password.getText().toString();
        usrName = userName.getText().toString();
        confirmPass = confirmPassword.getText().toString();
        email = this.email.getText().toString();
        presenter.attemptSignUp(usrName, pass, email);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void showValidField() {
        //TODO mostrar tic cuando el campo de texto tenga un contenido válido
    }

    @Override
    public void showInvalidField() {
        //TODO mostrar un aspa cuando el campo de texto tenga un contenido no válido
    }

    @Override
    public void showErrorMessage(int errorMessageResource) {
        Toast.makeText(this, errorMessageResource, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void navigate(Class<? extends Activity> activity) {
        Intent i = new Intent(getApplicationContext(), activity);
        startActivity(i);
    }

    @OnClick({R.id.register_button})
    public void clickListener(View view) {
        sendButtonTracked(view.getId());
    }

    /**
     * Sends button clicks to Google Analytics
     *
     * @param id identifier of the clicked view
     */
    private void sendButtonTracked(int id) {
        String label;
        switch (id) {
            case R.id.register_button:
                label = "Register a new user";
                break;
            default:
                label = "Other";
        }
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory("SignUpActivity")
                .setAction("button clicked")
                .setLabel(label)
                .build());
        GoogleAnalytics.getInstance(this.getApplication().getBaseContext()).dispatchLocalHits();
    }
}
