/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.domain;

import com.videonasocialmedia.videona.auth.repository.localsource.CachedToken;
import com.videonasocialmedia.videona.promo.model.PromoCode;
import com.videonasocialmedia.videona.promo.repository.apiclient.PromoCodeClient;
import com.videonasocialmedia.videona.promo.repository.apiclient.PromoCodeResponse;
import com.videonasocialmedia.videona.repository.rest.ServiceGenerator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 *
 */
public class CheckPromoCode {

    public void checkPromoCode(String code, final CheckPromoCodeListener listener) {
        PromoCode promoCode = new PromoCode(code);
        PromoCodeClient client;
        ServiceGenerator generator = new ServiceGenerator();
        if (CachedToken.hasToken()) {
            client = generator.generateService(PromoCodeClient.class, CachedToken.getToken());
            client.validatePromoCode(promoCode).enqueue(new Callback<PromoCodeResponse>() {
                @Override
                public void onResponse(Call<PromoCodeResponse> call, Response<PromoCodeResponse> response) {
                    if (response.body().isValidCode()) {
                        listener.onSuccess();
                    } else {
                        listener.onError(CheckPromoCodeListener.Causes.UNKNOWN);
                    }
                }

                @Override
                public void onFailure(Call<PromoCodeResponse> call, Throwable t) {
                    listener.onError(CheckPromoCodeListener.Causes.UNKNOWN);
                }
            });
        } else {
            listener.onError(CheckPromoCodeListener.Causes.UNAUTHORIZED);
        }
    }

}
