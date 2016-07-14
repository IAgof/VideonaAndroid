/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.presentation.views.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.presentation.views.activity.VideonaActivity;
import com.videonasocialmedia.videona.promo.presentation.mvp.presenters.PromoCodePresenter;
import com.videonasocialmedia.videona.promo.presentation.mvp.views.PromoCodeView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by alvaro on 7/07/16.
 */
public class PromoCodeActivity extends VideonaActivity implements PromoCodeView {

    @Bind(R.id.edit_promocode)
    EditText editText;

    @Bind(R.id.button_validate)
    Button buttonValidate;

    PromoCodePresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_promocode_text);
        ButterKnife.bind(this);
        setToolbar();
        presenter = new PromoCodePresenter(this);
    }

   @Override
    protected void onResume() {
        super.onResume();
        changeTypeFontRobotoThin();
    }


    @OnTextChanged(R.id.edit_promocode)
    public void editTextPromocodenTextChanged(){
        editText.setTypeface(Typeface.DEFAULT);
        String text= editText.getText().toString();

        if(text.length()==0) {
            changeTypeFontRobotoThin();
        }
    }


    private void changeTypeFontRobotoThin() {
        editText.setTypeface(Typeface.createFromAsset(getAssets(),"fonts/Roboto-Thin.ttf"));
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
        showSnackBar(stringResourceId);
    }

    private void showSnackBar(int stringResourceId) {
        Snackbar snackbar = Snackbar.make(buttonValidate, stringResourceId, Snackbar.LENGTH_LONG);
        snackbar.show();
    }
}
