/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.mvp.presenters;

import android.util.Patterns;
import android.widget.CheckBox;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.auth.domain.usecase.LoginUser;
import com.videonasocialmedia.videona.auth.domain.usecase.RegisterUser;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnLoginListener;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnRegisterListener;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.presentation.views.activity.SettingsActivity;

/**
 * Created by alvaro on 14/06/16.
 */
public class LoginPresenter implements OnLoginListener, OnRegisterListener {

    private LoginView loginView;
    private String email;
    private String password;
    private LoginUser loginUser;
    private RegisterUser registerUser;
    private CheckBox checkBox;

    public LoginPresenter(LoginView loginView) {
        this.loginUser = new LoginUser();
        this.loginView = loginView;
        this.registerUser = new RegisterUser();
    }

    public void tryToSignInOrLogIn(String email, String password) {
        loginView.resetErrorFields();
        loginView.showProgressAuthenticationDialog();
        if (isEmailValidAndNotEmpty(email) && isPasswordValidAndNotEmpty(password)) {
            this.email = email;
            this.password = password;
            //registerUser.register(email, password, this);
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

    public boolean isCheckedPrivacyTerm(CheckBox checkBox){

        if (!checkBox.isChecked()){
            loginView.showNoChekedPrivacyTerm(R.string.error_No_Cheked_PrivacyTerm);
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
        return password.length() >= 6;
    }

    @Override
    public void onLoginError(OnLoginListener.Causes causes) {
        loginView.hideProgressAuthenticationDialog();
        loginView.showErrorLogin(R.string.defaultErrorMessage);
    }

    @Override
    public void onLoginSuccess() {

        loginView.showSuccessLogin(R.string.success_login);
        loginView.exitLoginActivity();

    }

    @Override
    public void onRegisterError(OnRegisterListener.Causes cause) {

        loginView.hideProgressAuthenticationDialog();

        switch (cause) {
            case NETWORK_ERROR:
                loginView.showErrorLogin(R.string.networkError);
                break;
            case UNKNOWN_ERROR:
                loginView.showErrorLogin(R.string.error);
                break;
            case USER_ALREADY_EXISTS:
                loginUser.login(email, password, this);
                break;
            case INVALID_EMAIL:
                loginView.showErrorLogin(R.string.error_invalid_email);
                break;
            case MISSING_REQUEST_PARAMETERS:
                loginView.showErrorLogin(R.string.error_field_required);
                break;
            case INVALID_PASSWORD:
                loginView.showErrorLogin(R.string.error_invalid_email);
                break;
        }

    }

    @Override
    public void onRegisterSuccess() {
        loginUser.login(email, password, this);
    }
}
