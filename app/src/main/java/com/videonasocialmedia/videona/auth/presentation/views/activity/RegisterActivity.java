/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.auth.presentation.views.activity;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.RegisterPresenter;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.RegisterView;
import com.videonasocialmedia.videona.presentation.views.activity.PrivacyPolicyActivity;
import com.videonasocialmedia.videona.presentation.views.activity.SettingsActivity;
import com.videonasocialmedia.videona.presentation.views.activity.TermsOfServiceActivity;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * Created by ruth on 21/07/16.
 */
public class RegisterActivity extends VideonaActivity implements RegisterView {

    @Bind(R.id.email_register_text_input)
    TextInputLayout emailregisterTextInput;
    @Bind(R.id.password_register_text_input)
    TextInputLayout passwordRegisterTextInput;
    @Bind(R.id.email_register_edit_text)
    EditText emailRegisterEditText;
    @Bind(R.id.password_register_edit_text)
    EditText passwordRegisterEditText;
    @Bind(R.id.register_progress_view)
    View registerProgressView;
    @Bind(R.id.register_form_view)
    View loginFormView;
    @Bind(R.id.email_register_button)
    Button emailRegisterButton;
    @Bind(R.id.accept_term_service_text_view)
    TextView accepTermServiceTextView;

    @Bind(R.id.check_box_Accept_Term)
    CheckBox checkBoxAcceptTerm;
    @Bind(R.id.register_progress_layout)
    View registerProgressLayout;
    @Bind(R.id.email_register_form)
    View emailRegisterForm;
    @Bind(R.id.register_progress_text_view)
    TextView registerProgressTextView;


    private RegisterPresenter registerPresenter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        setupToolbar();
        registerPresenter = new RegisterPresenter(this);

        addTextViewWithMultipleLink(accepTermServiceTextView);
    }

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @OnClick(R.id.email_register_button)
    public void emailRegisterButtonClickListener(){
        attemptRegister();
    }

    private void attemptRegister() {

        String email = emailRegisterEditText.getText().toString();
        String password = passwordRegisterEditText.getText().toString();

        if (registerPresenter.isEmailValidAndNotEmpty(email) &&
                registerPresenter.isPasswordValidAndNotEmpty(password)&& registerPresenter.isCheckedPrivacyTerm(checkBoxAcceptTerm)) {

            registerPresenter.tryToSignInOrLogIn(email, password);
        }
    }

    @OnClick (R.id.accept_term_service_text_view)
    public void goToPrivacyTermClickListener(){
        goToTermsOfService();
    }

    @OnEditorAction(R.id.password_register_edit_text)
    public boolean onEditorAction(int id, KeyEvent key) {

        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptRegister();
            return true;
        }
        return false;
    }

    @Override
    public void resetErrorFields() {
        emailregisterTextInput.setError(null);
        passwordRegisterTextInput.setError(null);

    }

    @Override
    public void passwordFieldRequire() {
        passwordRegisterTextInput.setError(getString(R.string.error_invalid_password_register));
    }

    @Override
    public void emailFieldRequire() {
        emailregisterTextInput.setError(getString(R.string.error_field_required_register));
    }

    @Override
    public void emailInvalid() {
        emailregisterTextInput.setError(getString(R.string.error_invalid_email_register));
    }

    @Override
    public void passwordInvalid() {
        passwordRegisterTextInput.setError(getString(R.string.error_invalid_password_register));
    }

    @Override
    public void showProgressAuthenticationDialog() {
        showProgress(true);
    }


    @Override
    public void hideProgressAuthenticationDialog() {
            showProgress(false);
    }

    @Override
    public void showErrorRegister(int stringErrorRegister) {
        showMessage(stringErrorRegister);
    }


    @Override
    public void showSuccessRegister(int stringSuccesRegister) {
        registerProgressView.setVisibility(View.INVISIBLE);
        registerProgressTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        registerProgressTextView.setText(stringSuccesRegister);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToSettingsActivity();
            }
        },3000);
    }

    @Override
    public void showNoChekedPrivacyTerm(int stringNoChekedPrivacyTerm) {
        showMessage(stringNoChekedPrivacyTerm);
    }

    private void showMessage(int stringResource) {
        Snackbar snackbar = Snackbar.make(emailRegisterButton, stringResource, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void goToSettingsActivity(){
        Intent intent = new Intent(VideonaApplication.getAppContext(), SettingsActivity.class);
        startActivity(intent);

    }

    @Override
    public void goToTermsOfService() {
        Intent intent = new Intent(VideonaApplication.getAppContext(), TermsOfServiceActivity.class);
        startActivity(intent);
    }

    @Override
    public void goToPrivacyPolicy() {
        Intent intent = new Intent(VideonaApplication.getAppContext(), PrivacyPolicyActivity.class);
        startActivity(intent);
    }

    public void addTextViewWithMultipleLink(TextView textView){

        SpannableStringBuilder textWithLink = new SpannableStringBuilder(getString(R.string.term_of_service_link));

        textWithLink.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToTermsOfService();
            }
        }, textWithLink.length() - getString(R.string.term_of_service_link).length(), textWithLink.length(),0);


        textWithLink.append("  " +getString(R.string.and)+"  ");
        textWithLink.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSecondary)),textWithLink.length()-3,textWithLink.length(),0);

        textWithLink.append(getString(R.string.privacy_policy_link));
        textWithLink.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToPrivacyPolicy();
            }
        }, textWithLink.length() - getString(R.string.privacy_policy_link).length(), textWithLink.length(),0);;

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(textWithLink, TextView.BufferType.SPANNABLE);
    }


    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        emailRegisterForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        emailRegisterForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                emailRegisterForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });
        registerProgressLayout.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        registerProgressLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                registerProgressLayout.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }


}
