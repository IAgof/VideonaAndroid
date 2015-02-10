package com.videonasocialmedia.videona.api;

import retrofit.RequestInterceptor;

/**
 * Created by jca on 9/2/15.
 */
public class ApiHeaders implements RequestInterceptor {
    private String sessionCookieValue;
    private String rememberMeCookieValue;


    public String getSessionCookieValue() {
        return sessionCookieValue;
    }


    public void setSessionCookieValue(String sessionCookieValue) {
        this.sessionCookieValue = sessionCookieValue;
    }

    public String getRememberMeCookieValue() {
        return rememberMeCookieValue;
    }

    public void setRememberMeCookieValue(String rememberMeCookieValue) {
        this.rememberMeCookieValue = rememberMeCookieValue;
    }

    /**
     * Called for every request. Add data using methods on the supplied {@link retrofit.RequestInterceptor.RequestFacade}.
     *
     * @param request api call request message
     */
    @Override
    public void intercept(RequestFacade request) {
        request.addHeader("User-Agent", "Videona-App");
        if (sessionCookieValue != null) {
            request.addHeader("Cookie", sessionCookieValue);
            request.addHeader("Cookie", rememberMeCookieValue);
        }
    }
}
