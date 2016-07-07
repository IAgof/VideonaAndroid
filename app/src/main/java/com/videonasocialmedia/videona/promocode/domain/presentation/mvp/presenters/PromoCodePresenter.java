/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promocode.domain.presentation.mvp.presenters;

import android.content.Intent;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.VideonaApplication;
import com.videonasocialmedia.videona.presentation.views.activity.RecordActivity;
import com.videonasocialmedia.videona.promocode.domain.presentation.mvp.presenters.callback.CheckPromoCodeListener;
import com.videonasocialmedia.videona.promocode.domain.presentation.mvp.views.PromoCodeView;

/**
 * Created by alvaro on 7/07/16.
 */
public class PromoCodePresenter implements CheckPromoCodeListener {

    private PromoCodeView promoCodeView;

    public PromoCodePresenter(PromoCodeView promoCodeView){
        this.promoCodeView = promoCodeView;
    }

    public void validateCode(String code){

    }

    @Override
    public void onSuccess() {
        showMessageValidateCode();
    }

    private void showMessageValidateCode() {
        promoCodeView.showMessageValidateCode(R.string.message_promocode_valid);
    }

    @Override
    public void onError(Causes cause) {
        promoCodeView.showInvalidCode(R.string.message_promocode_invalid);
    }


}
