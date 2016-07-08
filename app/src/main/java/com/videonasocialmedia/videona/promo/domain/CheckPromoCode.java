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
import com.videonasocialmedia.videona.promo.repository.apiclient.PromoCodeResponse;
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
        if (CachedToken.hasToken()) {
            client = generator.generateService(PromoCodeClient.class, CachedToken.getToken());
            client.validatePromoCode(code).enqueue(new Callback<PromoCodeResponse>() {
                @Override
                public void onResponse(Call<PromoCodeResponse> call, Response<PromoCodeResponse> response) {
                    PromoCodeResponse responseBody = response.body();
                    if (responseBody.isValidCode()) {
                        Promo promo = new Promo(responseBody.getCampaign()
                                , responseBody.isValidCode());
                        PromoRepository local = new PromosLocalSource();
                        local.setPromo(promo, null);
                        listener.onSuccess(promo.getCampaign());
                    } else {
                        listener.onError(CheckPromoCodeListener.Causes.INVALID);
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
