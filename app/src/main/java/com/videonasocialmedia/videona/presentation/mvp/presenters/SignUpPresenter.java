package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.social.LoginUseCase;
import com.videonasocialmedia.videona.domain.social.UserRegistrationUseCase;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.login.LoginActivity;
import com.videonasocialmedia.videona.presentation.views.login.RegisterView;


/**
 * Created by jca on 9/3/15.
 */
public class SignUpPresenter implements OnSignedUpListener, OnLoginFinishedListener {

    private LoginUseCase loginUseCase;
    private UserRegistrationUseCase userRegistrationUseCase;
    private RegisterView registerView;
    private String userName, password, email;

    public SignUpPresenter(RegisterView registerView) {
        this.loginUseCase = new LoginUseCase();
        this.userRegistrationUseCase = new UserRegistrationUseCase();
        this.registerView = registerView;
    }

    public void attemptSignUp(String userName, String password, String email) {
        this.userName = userName;
        this.email = email;
        this.password = email;
        userRegistrationUseCase.registerUser(userName, password, email, this);
    }

    @Override
    public void onSignUpSuccess() {
        loginUseCase.videonaLogin(userName, password, this);
    }

    @Override
    public void onSignUpError() {
        registerView.showErrorMessage(R.string.signUpError);
    }

    @Override
    public void onLoginCredentialsError() {
        registerView.showErrorMessage(R.string.loginError);
        registerView.navigate(LoginActivity.class);
    }

    @Override
    public void onLoginSuccess() {
        registerView.navigate(RecordActivity.class);
    }


}
