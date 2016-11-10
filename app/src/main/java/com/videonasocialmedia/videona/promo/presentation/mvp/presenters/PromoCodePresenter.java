/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.presentation.mvp.presenters;


import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.promo.domain.CheckPromoCode;
import com.videonasocialmedia.videona.promo.domain.CheckPromoCodeListener;
import com.videonasocialmedia.videona.promo.presentation.mvp.views.PromoCodeView;

/**
 * Created by alvaro on 7/07/16.
 */
public class PromoCodePresenter implements CheckPromoCodeListener {

    private PromoCodeView promoCodeView;
    private CheckPromoCode checkPromoCodeUseCase;


    public PromoCodePresenter(PromoCodeView promoCodeView) {
        this.promoCodeView = promoCodeView;
        checkPromoCodeUseCase = new CheckPromoCode();
    }

    public void validateCode(String code) {
        promoCodeView.showProgressDialog();
        checkPromoCodeUseCase.checkPromoCode(code, this);
    }

    @Override
    public void onSuccess(String campaing) {
        showMessageValidateCode();
    }

    private void showMessageValidateCode() {
        promoCodeView.showMessageValidateCode(R.string.message_promocode_valid);
    }

    @Override
    public void onError(Causes cause) {

        promoCodeView.hideProgressDialog();

        if (cause == Causes.UNAUTHORIZED)
            promoCodeView.showInvalidCode(R.string.message_promocode_unauthorized);
        else
            promoCodeView.showInvalidCode(R.string.message_promocode_invalid);
    }
}
