/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.presentation.views.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.auth.presentation.views.activity.LoginActivity;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;
import com.videonasocialmedia.videona.promo.presentation.mvp.presenters.PromoCodePresenter;
import com.videonasocialmedia.videona.promo.presentation.mvp.views.PromoCodeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by alvaro on 7/07/16.
 */
public class PromoCodeActivity extends VideonaActivity implements PromoCodeView {

    @Bind(R.id.edit_promocode)
    EditText editText;

    @Bind(R.id.button_validate)
    Button buttonValidate;

    @Bind(R.id.promocode_progressbar)
    ProgressBar progressBar;

    PromoCodePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_promocode_text);
        ButterKnife.bind(this);
        setToolbar();
        presenter = new PromoCodePresenter(this);
    }

    private void setToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }

    @OnClick(R.id.button_validate)
    public void buttonValidateOnClickListener(){
        presenter.validateCode(editText.getText().toString());
    }


    @Override
    public void showMessageValidateCode(int stringResourceId) {
        showSnackBar(stringResourceId);
        navigateToRecordActivity();
    }

    private void navigateToRecordActivity() {
        Intent intent = new Intent(VideonaApplication.getAppContext(), RecordActivity.class);
        startActivity(intent);
    }


    @Override
    public void showInvalidCode(int stringResourceId) {
        if(R.string.message_promocode_unauthorized == stringResourceId){
            showLoginSnackBar(stringResourceId);
            return;
        }
        showSnackBar(stringResourceId);
    }

    @Override
    public void showProgressDialog() {
        progressBar.setVisibility(View.VISIBLE);
        editText.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressDialog() {
        progressBar.setVisibility(View.GONE);
        editText.setVisibility(View.VISIBLE);
    }

    private void showSnackBar(int stringResourceId) {
        Snackbar snackbar = Snackbar.make(buttonValidate, stringResourceId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    private void showLoginSnackBar(int stringResourceId){

        Snackbar snackbar = Snackbar.make(buttonValidate, stringResourceId, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.title_activity_login,  new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VideonaApplication.getAppContext(), LoginActivity.class);
                startActivity(intent);
            }
        });
        snackbar.show();

    }
}
