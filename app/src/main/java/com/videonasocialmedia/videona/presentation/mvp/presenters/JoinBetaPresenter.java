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

import com.videonasocialmedia.videona.eventbus.events.survey.JoinBetaEvent;
import com.videonasocialmedia.videona.presentation.mvp.views.JoinBetaView;

import de.greenrobot.event.EventBus;

/**
 * This class is used to show the setting menu.
 */
public class JoinBetaPresenter {

    private JoinBetaView joinBetaView;

    /**
     * Constructor
     *
     * @param joinBetaView
     */
    public JoinBetaPresenter(JoinBetaView joinBetaView) {
        this.joinBetaView = joinBetaView;
    }

    public void validateEmail(CharSequence email) {
        if(isValidEmail(email))
            EventBus.getDefault().post(new JoinBetaEvent(email.toString()));
        else
            joinBetaView.showMessage();
    }

    private boolean isValidEmail(CharSequence email) {
        if (email == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

}
