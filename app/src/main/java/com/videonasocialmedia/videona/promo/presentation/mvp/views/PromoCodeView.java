/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.promo.presentation.mvp.views;

/**
 * Created by alvaro on 7/07/16.
 */
public interface PromoCodeView {

    void showMessageValidateCode(int stringResourceId);

    void showInvalidCode(int stringResourceId);

    void showProgressDialog();

    void hideProgressDialog();
}
