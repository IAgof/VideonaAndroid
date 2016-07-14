/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain;

import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.promo.domain.model.Promo;
import com.videonasocialmedia.videona.promo.repository.PromoRepository;
import com.videonasocialmedia.videona.promo.repository.apiclient.PromoCodeClient;
import com.videonasocialmedia.videona.promo.repository.model.PromoCodeResponse;
import com.videonasocialmedia.videona.promo.repository.local.PromosLocalSource;
import com.videonasocialmedia.videona.repository.rest.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
public class CheckPromoCode {

    public void checkPromoCode(String code, final CheckPromoCodeListener listener) {
        PromoCodeClient client;
        ServiceGenerator generator = new ServiceGenerator();
        client = generator.generateService(PromoCodeClient.class, CachedToken.getToken());
        if (CachedToken.hasToken()) {
            client.validatePromoCode(code).enqueue(new InternalPromoCodeCallback(listener));
        } else {
            listener.onError(CheckPromoCodeListener.Causes.UNAUTHORIZED);
        }
    }

    private class InternalPromoCodeCallback implements Callback<PromoCodeResponse> {

        private CheckPromoCodeListener listener;

        public InternalPromoCodeCallback(CheckPromoCodeListener listener) {
            this.listener = listener;
        }

        @Override
        public void onResponse(Call<PromoCodeResponse> call, Response<PromoCodeResponse> response) {
            PromoCodeResponse responseBody = response.body();
            if(responseBody == null){
                listener.onError(CheckPromoCodeListener.Causes.UNKNOWN);
                return;
            }
            if (responseBody.isValidCode()) {
                Promo promo = new Promo(responseBody.getCampaign()
                        , responseBody.isValidCode());
                PromoRepository local = new PromosLocalSource();
                local.setPromo(promo, null);
                listener.onSuccess(promo.getCampaign());
            } else
                sendError(response);
        }

        private void sendError(Response<PromoCodeResponse> response) {
            PromoCodeResponse responseBody = response.body();
            if (response.code() == 401 || response.code() == 400)
                listener.onError(CheckPromoCodeListener.Causes.UNAUTHORIZED);
            else if (responseBody.getError()
                    .compareToIgnoreCase("Code has been already redeemed") == 0)
                listener.onError(CheckPromoCodeListener.Causes.REDEEMED);
            else if (responseBody.getError()
                    .compareToIgnoreCase("Code not found") == 0)
                listener.onError(CheckPromoCodeListener.Causes.INVALID);
            else if (responseBody.getError()
                    .compareToIgnoreCase("Code has already expired") == 0)
                listener.onError(CheckPromoCodeListener.Causes.EXPIRED);
        }

        @Override
        public void onFailure(Call<PromoCodeResponse> call, Throwable t) {
            listener.onError(CheckPromoCodeListener.Causes.UNKNOWN);
        }


    }
}
