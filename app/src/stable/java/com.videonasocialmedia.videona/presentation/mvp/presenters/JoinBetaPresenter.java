/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Veronica Lago Fominaya
 */

package com.videonasocialmedia.videona.presentation.mvp.presenters;

import android.content.SharedPreferences;

import com.videonasocialmedia.videona.R;
import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;
import com.videonasocialmedia.videona.utils.ConfigPreferences;

import de.greenrobot.event.EventBus;

/**
 * This class is used to show the setting menu.
 */
public class JoinBetaPresenter {

    private JoinBetaView joinBetaView;
    private SharedPreferences sharedPreferences;

    /**
     * Constructor
     *
     * @param joinBetaView
     */
    public JoinBetaPresenter(JoinBetaView joinBetaView, SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        this.joinBetaView = joinBetaView;
    }

    public void validateEmail(CharSequence email) {
        if(isValidEmail(email)) {
            EventBus.getDefault().post(new JoinBetaEvent(email.toString()));
            joinBetaView.hideDialog();
            joinBetaView.showMessage(R.string.valid_email);
        } else
            joinBetaView.showMessage(R.string.invalid_email);
    }

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public void checkIfPreviousEmailExists() {
        String previousEmail = sharedPreferences.getString(ConfigPreferences.EMAIL, null);
        if(previousEmail != null && !previousEmail.isEmpty())
            joinBetaView.setEmail(previousEmail);
    }

}
