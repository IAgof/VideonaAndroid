/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.apiclient;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Path;

/**
 *
 */
public interface PromoCodeClient {
    @GET("promo_code/{promoCode}")
    @Headers("Content-Type: application/json")
    Call<PromoCodeResponse> validatePromoCode(@Path("promoCode") String promoCode);
}
