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
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
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
    @Bind(R.id.progress_bar_register)
    View ProgressBarRegister;
    @Bind(R.id.register_form_view)
    View loginFormView;
    @Bind(R.id.email_register_button)
    Button emailRegisterButton;
    @Bind(R.id.accept_term_service_text_view)
    TextView accepTermServiceTextView;

    @Bind(R.id.check_box_Accept_Term)
    CheckBox checkBoxAcceptTerm;
    @Bind(R.id.layout_progress_register)
    View layoutProgressBarRegister;
    @Bind(R.id.layout_register_form)
    View layoutRegisterForm;
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
        ProgressBarRegister.setVisibility(View.INVISIBLE);
        registerProgressTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        registerProgressTextView.setText(stringSuccesRegister);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                goToRecordActivity();
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

    public void goToRecordActivity(){
        Intent intent = new Intent(VideonaApplication.getAppContext(), RecordActivity.class);
        startActivity(intent);

    }

    public void addTextViewWithMultipleLink(TextView textView){

        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(getString(R.string.term_of_service_link));

        addStringClickable(stringBuilder, R.string.term_of_service_link, TermsOfServiceActivity.class );


        stringBuilder.append("  " +getString(R.string.and)+"  ");
        stringBuilder.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSecondary)),stringBuilder.length()-3,stringBuilder.length(),0);

        stringBuilder.append(getString(R.string.privacy_policy_link));

        addStringClickable(stringBuilder, R.string.privacy_policy_link, PrivacyPolicyActivity.class);


        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(stringBuilder, TextView.BufferType.SPANNABLE);
    }


    private SpannableStringBuilder addStringClickable(SpannableStringBuilder spannableStringBuilder, int stringClickable, final Class<?> activity) {
        spannableStringBuilder.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(VideonaApplication.getAppContext(), activity);
                startActivity(intent);
            }
        }, spannableStringBuilder.length() - getString(stringClickable).length(), spannableStringBuilder.length(), 0);

        return spannableStringBuilder;
    }


    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        layoutRegisterForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        layoutRegisterForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layoutRegisterForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });
        layoutProgressBarRegister.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        layoutProgressBarRegister.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                layoutProgressBarRegister.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }


}
