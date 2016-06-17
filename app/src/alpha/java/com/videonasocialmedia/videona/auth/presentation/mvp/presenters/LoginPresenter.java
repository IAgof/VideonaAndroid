/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.mvp.presenters;

import android.util.Patterns;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.auth.domain.usecase.LoginUser;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnLoginListener;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.presentation.views.activity.SettingsActivity;

/**
 * Created by alvaro on 14/06/16.
 */
public class LoginPresenter implements OnLoginListener {

    private LoginView loginView;

    public LoginPresenter(LoginView loginView) {
        this.loginView = loginView;
    }

    public void tryToLogIn(String email, String password) {
        loginView.resetErrorFields();
        loginView.showProgressAuthenticationDialog();
        if (isEmailValidAndNotEmpty(email) && isPasswordValidAndNotEmpty(password)) {
            LoginUser loginUser = new LoginUser();
            loginUser.login(email, password, this);
        }
    }

    public boolean isEmailValidAndNotEmpty(String email) {
        if (isEmptyField(email)) {
            loginView.emailFieldRequire();
            return false;
        }
        if (!isEmailValid(email)) {
            loginView.emailInvalid();
            return false;
        }
        return true;
    }

    public boolean isPasswordValidAndNotEmpty(String password) {
        if (isEmptyField(password)) {
            loginView.passwordFieldRequire();
            return false;
        }
        if (!isPasswordValid(password)) {
            loginView.passwordInvalid();
            return false;
        }
        return true;
    }

    private boolean isEmptyField(String field) {
        return field == null || field.length() == 0;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        // TODO:(alvaro.martinez) 15/06/16 will there be a rule for passwords?
        return password.length() > 4;
    }

    @Override
    public void onLoginError(Causes causes) {
        loginView.hideProgressAuthenticationDialog();
        loginView.showErrorLogin(R.string.defaultErrorMessage);
    }

    @Override
    public void onLoginSuccess() {
        loginView.hideProgressAuthenticationDialog();
        loginView.navigateTo(SettingsActivity.class);
    }
}
