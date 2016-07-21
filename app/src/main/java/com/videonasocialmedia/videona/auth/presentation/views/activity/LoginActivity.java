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
import android.widget.EditText;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.LoginPresenter;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends VideonaActivity implements LoginView {


    // UI references.
    @Bind(R.id.email_text_input)
    TextInputLayout emailTextInput;
    @Bind(R.id.password_text_input)
    TextInputLayout passwordTextInput;
    @Bind(R.id.email_edit_text)
    EditText emailEditText;
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;
    @Bind(R.id.login_progress_view)
    View progressView;
    @Bind(R.id.login_form_view)
    View loginFormView;
    @Bind(R.id.email_sign_in_button)
    Button emailSignInButton;
   @Bind(R.id.accept_term_service_text_view)
    TextView accepTermServiceTextView;
    private LoginPresenter loginPresenter;
    @Bind(R.id.login_progress_layout)
    View loginProgressLayout;
    @Bind(R.id.email_login_form)
    View emailLoginForm;
    @Bind(R.id.login_progress_text_view)
    TextView loginProgressTextView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupToolbar();
        loginPresenter = new LoginPresenter(this);
        addTextViewWithLink(accepTermServiceTextView);
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
        // Inflate the menu; this adds items to the action bar if it is present.
       // getMenuInflater().inflate(R.menu.menu_edit_activity, menu);
        return true;
    }

   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()) {
            case R.id.action_settings_edit_options:
                navigateTo(SettingsActivity.class);
                return true;
            case R.id.action_settings_edit_gallery:
                navigateTo(GalleryActivity.class);
                return true;
            case R.id.action_settings_edit_tutorial:
                //navigateTo(TutorialActivity.class);
                return true;
            default:

        }
        return super.onOptionsItemSelected(item);
    }*/

    @OnClick (R.id.email_sign_in_button)
    public void emailSignInButtonClickListener(){
        attemptLogin();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (loginPresenter.isEmailValidAndNotEmpty(email) &&
                loginPresenter.isPasswordValidAndNotEmpty(password)/*&& loginPresenter.isCheckedPrivacyTerm(checkBoxAcceptTerm)*/) {

            loginPresenter.tryToSignInOrLogIn(email, password);
        }

    }




    @OnEditorAction(R.id.password_edit_text)
    public boolean onEditorAction(int id, KeyEvent key) {

        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }
        return false;
    }

    @Override
    public void resetErrorFields() {
        emailTextInput.setError(null);
        passwordTextInput.setError(null);
    }

    @Override
    public void passwordFieldRequire() {
        passwordTextInput.setError(getString(R.string.error_invalid_password));
    }

    @Override
    public void emailFieldRequire() {
        emailTextInput.setError(getString(R.string.error_field_required));
    }

    @Override
    public void emailInvalid() {
        emailTextInput.setError(getString(R.string.error_invalid_email));
    }

    @Override
    public void passwordInvalid() {
        passwordTextInput.setError(getString(R.string.error_invalid_password));
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
    public void showErrorLogin(int stringErrorLogin) {
        showMessage(stringErrorLogin);
    }

    @Override
    public void showSuccessLogin(int stringSuccesLogin) {


        progressView.setVisibility(View.INVISIBLE);
        loginProgressTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
        loginProgressTextView.setText(stringSuccesLogin);

        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitLoginActivity();
            }
        },3000);
    }

    @Override
    public void goToRegisterActivity() {
        Intent intent = new Intent(VideonaApplication.getAppContext(), RegisterActivity.class);
        startActivity(intent);
    }

    private void showMessage(int stringResource) {
        Snackbar snackbar = Snackbar.make(emailSignInButton, stringResource, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    @Override
    public void exitLoginActivity() {
                finish();
    }

    public void addTextViewWithLink(TextView textView){
        SpannableStringBuilder newTextOfTextView = new SpannableStringBuilder(getString(R.string.first_string_link_for_create_account));

        newTextOfTextView.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSecondary)),0,newTextOfTextView.length(),0);

        newTextOfTextView.append(" ");

        newTextOfTextView.append(getString(R.string.second_string_link_for_create_account));
        newTextOfTextView.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                goToRegisterActivity();
            }
        }, newTextOfTextView.length() - getString(R.string.second_string_link_for_create_account).length(), newTextOfTextView.length(),0);;

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(newTextOfTextView, TextView.BufferType.SPANNABLE);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
        emailLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
        emailLoginForm.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                emailLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            }
        });
        loginProgressLayout.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
        loginProgressLayout.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                loginProgressLayout.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            }
        });
    }

}

