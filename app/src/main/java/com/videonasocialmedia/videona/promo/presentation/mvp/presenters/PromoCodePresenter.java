/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.promo.domain.usecase.CheckPromoCode;
import com.videonasocialmedia.videona.promo.presentation.mvp.presenters.callback.CheckPromoCodeListener;
import com.videonasocialmedia.videona.promo.presentation.mvp.views.PromoCodeView;

/**
 * Created by alvaro on 7/07/16.
 */
public class PromoCodePresenter implements CheckPromoCodeListener {

    private PromoCodeView promoCodeView;
    private CheckPromoCode checkPromoCodeUseCase;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor preferencesEditor;

    public PromoCodePresenter(PromoCodeView promoCodeView){
        this.promoCodeView = promoCodeView;
        checkPromoCodeUseCase = new CheckPromoCode();
    }

    public void validateCode(String code){

        checkPromoCodeUseCase.checkPromoCode(code, this);
    }

    @Override
    public void onSuccess() {
        showMessageValidateCode();
        // TODO:(alvaro.martinez) 8/07/16 save validate code, i am a wolder user, use realm
        // preferencesEditor.putBoolean(ConfigPreferences.IS_WOLDER_USER, true);
        // preferencesEditor.commit();
    }

    private void showMessageValidateCode() {
        promoCodeView.showMessageValidateCode(R.string.message_promocode_valid);
    }

    @Override
    public void onError(Causes cause) {
        if(cause == Causes.UNAUTHORIZED){
           promoCodeView.showInvalidCode(R.string.message_promocode_unauthorized);
           return;
        }
        promoCodeView.showInvalidCode(R.string.message_promocode_invalid);
    }


}
