package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.util.Log;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.domain.social.LoginUseCase;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.login.LoginView;

/**
 * Created by jca on 9/3/15.
 */
public class LoginPresenter implements OnLoginFinishedListener {

    LoginUseCase loginUseCase;
    LoginView loginView;

    public LoginPresenter (LoginView loginView){
        loginUseCase= new LoginUseCase();
        this.loginView=loginView;
    }

    public void userPasswordLogin(String userName, String password) {
       loginUseCase.videonaLogin(userName, password, this);
    }

    public boolean thirdPartyLogin(){
        boolean result = false;
        return result;
    }

    @Override
    public void onLoginCredentialsError() {
        //TODO mejorar info de error
        loginView.showError(R.string.loginError);
    }

    @Override
    public void onLoginSuccess() {
        Log.d("LoginPresenter", "onLoginSuccess");
        loginView.navigate(RecordActivity.class);
    }
}
