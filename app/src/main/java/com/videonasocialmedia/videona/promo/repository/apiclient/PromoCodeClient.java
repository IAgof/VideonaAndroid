/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.repository.apiclient;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 *
 */
public interface PromoCodeClient {
    @GET("promo_codes/{promoCode}")
    Call<PromoCodeResponse> validatePromoCode(@Path("promoCode") String promoCode);
}
