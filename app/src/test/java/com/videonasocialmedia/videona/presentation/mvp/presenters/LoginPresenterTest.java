/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.presentation.views.activity.SettingsActivity;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;


/**
 * Created by alvaro on 15/06/16.
 */


public class LoginPresenterTest {

    private String email = "a@a.com";
    private String password = "1234567";


    private LoginPresenter loginPresenter;

    @Mock
    private LoginView loginView;


    @Before
    public void setupLoginPresenter() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        loginPresenter = new LoginPresenter(loginView);
    }

    @Test
    public void loginFieldsAreCorrect(){

        loginPresenter.isPasswordValidAndNotEmpty("1234");
        verify(loginView).passwordInvalid(); // less than 5 letters

        loginPresenter.isPasswordValidAndNotEmpty("");
        verify(loginView).passwordFieldRequire();

        loginPresenter.isEmailValidAndNotEmpty("");
        verify(loginView).emailFieldRequire();

        assertThat(loginPresenter.isPasswordValidAndNotEmpty(password), is(true));

        //Problem with this assert
        //assertThat(loginPresenter.isEmailValidAndNotEmpty(password), is(true));
    }

    @Test
    public void hideProgressDialogOnLoginFinished(){
        loginPresenter.onLoginSuccess();
        verify(loginView).hideProgressAuthenticationDialog();
        verify(loginView).navigateTo(SettingsActivity.class);
    }

    @Test
    public void hideProgressDialogOnLoginError(){
        loginPresenter.onLoginError(R.string.error);
        verify(loginView).hideProgressAuthenticationDialog();
        verify(loginView).showErrorLogin(R.string.error);
    }

}
