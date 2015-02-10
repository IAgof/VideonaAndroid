package com.videonasocialmedia.videona.api;

import android.text.TextUtils;

import java.util.regex.Pattern;

/**
 * Created by jca on 23/1/15.
 */
public class Validator {

    private static Pattern userNamePattern = Pattern.compile("^[áéíóúÁÉÍÓÚüÜñÑA-Za-z0-9_]{4,15}$");

    //TODO establecer tamaño pass
    private static Pattern passwordPattern = Pattern.compile("((?=.*[a-z])(?=.*d)(?=.*[@#$%&,!?¿.])(?=.*[A-Z]).{8,16})");


    public static boolean validateUserName(String username) {
        username = username.replaceAll("\\.", "");
        return userNamePattern.matcher(username).matches();
    }

    public static boolean isUserNameAvailable(String username) {
        //TODO check if user name is avaliable in backend
        return true;
    }

    public static boolean validatePassword(String password) {
        //TODO Verificar que Vero tiene la misma regex
        return passwordPattern.matcher(password).matches();
    }

    public static boolean validateEmail(CharSequence email) {
        return !TextUtils.isEmpty(email) &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isEmailAvailable(String email) {
        //TODO check if email is available in backend
        return true;
    }
}
