package com.videonasocialmedia.videona.auth.presentation.mvp.views;

/**
 * Created by ruth on 21/07/16.
 */
public interface RegisterView {

    void resetErrorFields();

    void passwordFieldRequire();

    void emailFieldRequire();

    void emailInvalid();

    void passwordInvalid();

    void showProgressAuthenticationDialog();

    void hideProgressAuthenticationDialog();

    void showErrorRegister(int stringErrorRegister);

    void showSuccessRegister(int stringSuccessRegister);

    void showNoChekedPrivacyTerm (int stringNoChekedPrivacyTerm);

    void goToTermsOfService();

    void goToPrivacyPolicy();
}
