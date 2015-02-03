package com.videona.videona.api;

import android.util.Log;

import java.io.IOException;
import java.net.CookieManager;
import java.net.URI;
import java.util.List;
import java.util.Map;

/**
 * Created by jca on 23/1/15.
 */
public class CustomCookieManager extends CookieManager {

    // The cookie key we're interested in.
    private final String SESSION_KEY = "session-key";


    @Override
    public void put(URI uri, Map<String, List<String>> responseHeaders) throws IOException {

        super.put(uri, responseHeaders);

        if (responseHeaders != null && responseHeaders.get("Set-Cookie") != null)
            for (String string : responseHeaders.get("Set-Cookie")) {
                if (string.contains("PHPSESSID")) {
                    //Preference.getInstance().setSessionId(string);
                    Log.d("COOKIE", string);
                }
            }

        /*if (responseHeaders == null || responseHeaders.get(Constants.SET_COOKIE_KEY) == null) {
            // No cookies in this response, simply return from this method.
            return;
        }*/

        // Yes, we've found cookies, inspect them for the key we're looking for.
        /*for (String possibleSessionCookieValues : responseHeaders.get(Constants.SET_COOKIE_KEY)) {

            if (possibleSessionCookieValues != null) {

                for (String possibleSessionCookie : possibleSessionCookieValues.split(";")) {

                    if (possibleSessionCookie.startsWith(SESSION_KEY) && possibleSessionCookie.contains("=")) {

                        // We can safely get the index 1 of the array: we know it contains
                        // a '=' meaning it has at least 2 values after splitting.
                        String session = possibleSessionCookie.split("=")[1];

                        // store `session` somewhere

                        return;
                    }
                }
            }
        }*/
    }
}
