/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.apiclient;

import com.videonasocialmedia.videona.promo.model.PromoCode;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

/**
 *
 */
public interface PromoCodeClient {
    @POST("promo")
    @Headers("Content-Type: application/json")
    Call<PromoCodeResponse> validatePromoCode(@Body PromoCode body);
}
