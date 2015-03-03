package com.videonasocialmedia.videona.model.rest.callbacks;

/**
 * Created by jca on 2/3/15.
 */

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

/**
 * Sub-class implementing the callback of the login api call
 */
public class BasicLoginCallback implements Callback<Response> {
    /**
     * Receive the successful response from the server and stores the cookie with the session
     * data in VideonaApplication.apiHeaders and, if rememberUser is true, the cookie is also
     * stored in sharedPreferences
     *
     * @param o
     * @param response successful response message from the server
     */
    @Override
    public void success(Response o, Response response) {
        //Toast.makeText(getApplicationContext(), "Logeado correctamente",
        //        Toast.LENGTH_LONG).show();

        //TODO adaptarse a OAUTH

//        String cookie = "";
//        String sessionIdCookie = "";
//        String rememberMeCookie = "";
//        List<Header> h = response.getHeaders();
//        for (Header header : h) {
//            if (header.getName().equalsIgnoreCase("set-cookie")) {
//                cookie = header.getValue();
//                if (cookie.contains("PHPSESSSID")) {
//                    sessionIdCookie = cookie;
//                } else if (cookie.contains("REMEMBERME")) {
//                    rememberMeCookie = cookie;
//                }
//            }
//        }
//
//
//        app.getApiHeaders().setSessionCookieValue(sessionIdCookie);
//
//
//        if (rememberMe.isChecked()) {
//            app.getApiHeaders().setRememberMeCookieValue(rememberMeCookie);
//            config.edit().putString("sessionCookie", sessionIdCookie).apply();
//            config.edit().putString("rememberMeCookie", rememberMeCookie).apply();
//        }

        //startActivity(new Intent(getApplicationContext(), RecordActivity.class));

    }

    /**
     * Show a message to the user let him know it was not possible to login
     *
     * @param error error captured by retrofit
     */
    @Override
    public void failure(RetrofitError error) {
        //Toast.makeText(getApplicationContext(), "Error durante login",
        //        Toast.LENGTH_SHORT).show();
    }
}