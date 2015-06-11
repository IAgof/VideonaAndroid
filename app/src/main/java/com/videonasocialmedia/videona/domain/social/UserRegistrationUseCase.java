package com.videonasocialmedia.videona.domain.social;

import com.videonasocialmedia.videona.model.sources.VideonaRestSource;
import com.videonasocialmedia.videona.presentation.mvp.presenters.OnSignedUpListener;

import retrofit.RetrofitError;


/**
 * Created by jca on 5/3/15.
 */
public class UserRegistrationUseCase {
    VideonaRestSource restSource;

    public UserRegistrationUseCase() {
        restSource = VideonaRestSource.getInstance();
    }

    public void registerUser(String userName, String password, String email, OnSignedUpListener listener) {
        try {
            restSource.createUser(userName, email, password);
        } catch (RetrofitError error) {
            listener.onSignUpError();
        }
        listener.onSignUpSuccess();
    }


}
