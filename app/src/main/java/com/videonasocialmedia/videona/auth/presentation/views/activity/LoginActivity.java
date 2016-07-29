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
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.presentation.mvp.presenters.LoginPresenter;
import com.videonasocialmedia.videona.auth.presentation.mvp.views.LoginView;
import com.videonasocialmedia.videona.presentation.views.activity.PrivacyPolicyActivity;
import com.videonasocialmedia.videona.presentation.views.activity.TermsOfServiceActivity;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;
import com.videonasocialmedia.videona.presentation.views.dialog.VideonaDialog;
import com.videonasocialmedia.videona.presentation.views.listener.VideonaDialogListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends VideonaActivity implements LoginView, VideonaDialogListener {



    // UI references Login.

    @Bind(R.id.text_input_email)
    TextInputLayout textInputEmail;

    @Bind(R.id.password_text_input)
    TextInputLayout passwordTextInput;

    @Bind(R.id.email_edit_text)
    EditText emailEditText;

    @Nullable
    @Bind(R.id.password_edit_text)
    EditText passwordEditText;

    @Bind(R.id.progress_bar_login)
    View progressBarLogin;

    @Bind(R.id.login_form_view)
    View loginFormView;

    @Nullable
    @Bind(R.id.email_sign_in_button)
    Button emailSignInButton;

    @Nullable
   @Bind(R.id.create_account_text_view)
    TextView textViewCreateAccount;

    private LoginPresenter loginPresenter;

    @Bind(R.id.layoutProgressBarLogin)
    View layoutProgress;

    @Bind(R.id.layout_login_form)
    View layoutLoginForm;


    @Bind(R.id.progress_text_view)
    TextView textViewLoginProgress;

    @Nullable
    @Bind(R.id.coordinatorLayoutLogin)
    View coordinatorLayoutLogin;

    // UI references Register.

    protected final int REQUEST_CODE_EXIT_REGISTER_ACTIVITY = 1;

    @Nullable
    VideonaDialog dialog;

    @Nullable
    @Bind(R.id.toolbarRegister)
    Toolbar toolbarRegister;

    @Nullable
    @Bind(R.id.email_register_button)
    Button emailRegisterButton;
    @Nullable
    @Bind(R.id.term_of_service_text_view)
    TextView accepTermServiceTextView;
    @Nullable
    @Bind(R.id.check_box_Accept_Term)
    CheckBox checkBoxAcceptTerm;

    @Nullable
    @Bind(R.id.coordinatorLayoutRegister)
    View coordinatorLayoutRegister;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        setupToolbar(R.id.toolbar);
        loginPresenter = new LoginPresenter(this);
        addTextViewWithLinktoRegister(textViewCreateAccount);


    }

    private void setupToolbar(int toolbarSelect) {
        Toolbar toolbar = (Toolbar) findViewById(toolbarSelect);
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


    @Nullable
    @OnClick (R.id.email_sign_in_button)
    public void emailSignInButtonClickListener(){
        attemptLogin();

    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (loginPresenter.isEmailValidAndNotEmpty(email) &&
                loginPresenter.isPasswordValidAndNotEmpty(password)) {

            loginPresenter.tryToLogIn(email, password);
        }

    }
    @Nullable
    @OnClick(R.id.email_register_button)
    public void emailRegisterButtonClickListener(){
        attemptRegister();
    }

    private void attemptRegister() {

        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (loginPresenter.isEmailValidAndNotEmpty(email) &&
                loginPresenter.isPasswordValidAndNotEmpty(password)&& loginPresenter.isCheckedPrivacyTerm(checkBoxAcceptTerm)) {

            loginPresenter.tryToSignIn(email, password);
        }
    }


    @OnEditorAction(R.id.password_edit_text)
    public boolean onEditorAction(int id, KeyEvent key) {

        if (id == R.id.login || id == EditorInfo.IME_NULL) {
            attemptLogin();
            return true;
        }

        if (id == R.id.register || id == EditorInfo.IME_NULL) {
            attemptRegister();
            return true;
        }
        return false;
    }

    @Override
    public void resetErrorFields() {

        //if (getIdViewActivate() == coordinatorLayoutLogin.getId()) {
            textInputEmail.setError(null);
            passwordTextInput.setError(null);
           // return;
       // }
    }

    @Override
    public void passwordFieldRequire() {
            passwordTextInput.setError(getString(R.string.error_invalid_password));

    }

    @Override
    public void emailFieldRequire() {
            textInputEmail.setError(getString(R.string.error_field_required_register));
    }

    @Override
    public void emailInvalid() {
            textInputEmail.setError(getString(R.string.error_invalid_email_register));
    }

    @Override
    public void passwordInvalid() {
            passwordTextInput.setError(getString(R.string.error_invalid_password_register));
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
        showMessage(stringErrorLogin,emailSignInButton);
    }

    @Override
    public void showSuccess(int stringSuccesLogin) {


            progressBarLogin.setVisibility(View.INVISIBLE);
            textViewLoginProgress.setTextColor(getResources().getColor(R.color.colorPrimary));
            textViewLoginProgress.setText(stringSuccesLogin);


        Handler handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                exitLoginActivity();
            }
        },3000);
    }

    private void showMessage(int stringResource, Button button) {
            Snackbar snackbar = Snackbar.make(button, stringResource, Snackbar.LENGTH_LONG);
            snackbar.show();
        }

    @Override
    public void exitLoginActivity() {
                finish();
    }

    @Override
    public void showErrorRegister(int stringErrorRegister) {
        showMessage(stringErrorRegister,emailRegisterButton);
    }


    @Override
    public void showNoChekedPrivacyTerm(int stringNoChekedPrivacyTerm) {
        showMessage(stringNoChekedPrivacyTerm,emailRegisterButton);

    }



    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            setupExitRegisterActivity();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void setupExitRegisterActivity() {
        dialog = new VideonaDialog.Builder() .withTitle(getString(R.string.exit_registerActivity_title))
                .withImage(0)
                .withMessage(getString(R.string.exit_registerActivity_message))
                .withPositiveButton(getString(R.string.acceptExitRegisterActivity))
                .withNegativeButton(getString(R.string.cancelExitRegisterActivity))
                .withCode(REQUEST_CODE_EXIT_REGISTER_ACTIVITY)
                .withListener(LoginActivity.this)
                .create();
        dialog.show(getFragmentManager(), "exitRegisterActivityDialog");

    }

    @Override
    public void onClickPositiveButton(int id) {
        if(id == REQUEST_CODE_EXIT_REGISTER_ACTIVITY)
            dialog.dismiss();
            exitLoginActivity();

    }

    @Override
    public void onClickNegativeButton(int id) {
        //TODO cambiar email
        if(id == REQUEST_CODE_EXIT_REGISTER_ACTIVITY)
            dialog.dismiss();
    }

    public void addTextViewWithLinktoRegister(TextView textView){

        SpannableStringBuilder newTextOfTextView = new SpannableStringBuilder(getString(R.string.first_string_link_for_create_account));

        newTextOfTextView.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.colorSecondary)),0,newTextOfTextView.length(),0);

        newTextOfTextView.append(" ");

        newTextOfTextView.append(getString(R.string.second_string_link_for_create_account));
        newTextOfTextView.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                setContentView(R.layout.activity_register);
                ButterKnife.bind(LoginActivity.this);
                setupToolbar(R.id.toolbarRegister);
                addTextViewWithLinktoTermService(accepTermServiceTextView);

            }
        }, newTextOfTextView.length() - getString(R.string.second_string_link_for_create_account).length(), newTextOfTextView.length(),0);;

        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setText(newTextOfTextView, TextView.BufferType.SPANNABLE);
    }

    public void addTextViewWithLinktoTermService(TextView textView){

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


    /**
     * Shows the progress UI and hides the login form.
     */
    private void showProgress(final boolean show) {

            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
            layoutLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
            layoutLoginForm.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutLoginForm.setVisibility(show ? View.INVISIBLE : View.VISIBLE);
                }
            });
            layoutProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
            layoutProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutProgress.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
                }
            });

    }


    /*public int getIdViewActivate(){
        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        return viewGroup.getId();
    }*/

}

