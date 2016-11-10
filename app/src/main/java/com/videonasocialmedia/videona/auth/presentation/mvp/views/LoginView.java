/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.mvp.views;

/**
 * Created by alvaro on 14/06/16.
 */
public interface LoginView {

    void resetErrorFields();

    void passwordFieldRequire();

    void emailFieldRequire();

    void emailInvalid();

    void passwordInvalid();

    void showProgressAuthenticationDialog();

    void hideProgressAuthenticationDialog();

    void showErrorLogin(int stringErrorLogin);

    void showSuccess(int stringSuccesLogin);

    void exitLoginActivity();

    void showErrorRegister(int stringErrorRegister);

    void showNoChekedPrivacyTerm (int stringNoChekedPrivacyTerm);

}
