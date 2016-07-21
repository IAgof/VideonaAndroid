package com.videonasocialmedia.videona.auth.presentation.mvp.presenters;

import android.util.Patterns;
import android.widget.CheckBox;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.auth.domain.usecase.LoginUser;
import com.videonasocialmedia.videona.auth.domain.usecase.RegisterUser;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnLoginListener;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.callback.OnRegisterListener;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.RegisterView;

/**
 * Created by ruth on 21/07/16.
 */
public class RegisterPresenter implements OnLoginListener, OnRegisterListener {

    private LoginView loginView;
    private RegisterView registerView;
    private String email;
    private String password;
    private LoginUser loginUser;
    private RegisterUser registerUser;
    private CheckBox checkBox;

    public RegisterPresenter(RegisterView registerView) {
        this.loginUser = new LoginUser();
        this.registerView = registerView;
        this.registerUser = new RegisterUser();
    }

    public void tryToSignInOrLogIn(String email, String password) {
        registerView.resetErrorFields();
        registerView.showProgressAuthenticationDialog();
        if (isEmailValidAndNotEmpty(email) && isPasswordValidAndNotEmpty(password)) {
            this.email = email;
            this.password = password;
            registerUser.register(email, password, this);

        }
    }

    public boolean isEmailValidAndNotEmpty(String email) {
        if (isEmptyField(email)) {
            registerView.emailFieldRequire();
            return false;
        }
        if (!isEmailValid(email)) {
            registerView.emailInvalid();
            return false;
        }
        return true;
    }

    public boolean isPasswordValidAndNotEmpty(String password) {
        if (isEmptyField(password)) {
            registerView.passwordFieldRequire();
            return false;
        }
        if (!isPasswordValid(password)) {
            registerView.passwordInvalid();
            return false;
        }
        return true;
    }

    public boolean isCheckedPrivacyTerm(CheckBox checkBox){

        if (!checkBox.isChecked()){
            registerView.showNoChekedPrivacyTerm(R.string.error_No_Cheked_PrivacyTerm);
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
    public void onRegisterError(OnRegisterListener.Causes cause) {

        registerView.hideProgressAuthenticationDialog();

        switch (cause) {
            case NETWORK_ERROR:
                registerView.showErrorRegister(R.string.networkError);
                break;
            case UNKNOWN_ERROR:
                registerView.showErrorRegister(R.string.error);
                break;
            case USER_ALREADY_EXISTS:
                registerView.showErrorRegister(R.string.error_already_exits);
                //loginUser.login(email, password, this);
                break;
            case INVALID_EMAIL:
                registerView.showErrorRegister(R.string.error_invalid_email);
                break;
            case MISSING_REQUEST_PARAMETERS:
                registerView.showErrorRegister(R.string.error_field_required);
                break;
            case INVALID_PASSWORD:
                registerView.showErrorRegister(R.string.error_invalid_email);
                break;
        }

    }

    @Override
    public void onRegisterSuccess() {
        loginUser.login(email, password, this);
        registerView.showSuccessRegister(R.string.success_register);
    }


    @Override
    public void onLoginError(OnLoginListener.Causes causes) {
        loginView.hideProgressAuthenticationDialog();
        loginView.showErrorLogin(R.string.defaultErrorMessage);
    }

    @Override
    public void onLoginSuccess() {
        //loginView.showSuccessLogin(R.string.success_login);

    }
}
