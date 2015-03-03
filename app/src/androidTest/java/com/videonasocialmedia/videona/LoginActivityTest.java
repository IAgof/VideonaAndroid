package com.videonasocialmedia.videona;

import android.test.ActivityInstrumentationTestCase2;

import com.videonasocialmedia.videona.presentation.views.login.LoginActivity;

/**
 * Created by jca on 20/1/15.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase2<LoginActivity> {

    LoginActivity activity;

    public LoginActivityTest() {
        super(LoginActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        this.activity = getActivity();
    }
}
