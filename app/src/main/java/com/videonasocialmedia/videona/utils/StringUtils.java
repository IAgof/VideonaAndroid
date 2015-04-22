/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 *
 */

package com.videonasocialmedia.videona.utils;

import java.util.regex.Pattern;

public class StringUtils {

    private static final Pattern USER_NAME_PATTERN = Pattern.compile("^[áéíóúÁÉÍÓÚüÜñÑA-Za-z0-9_]{4,15}$");

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("((?=.*[a-z])(?=.*d)(?=.*[@#$%&,!?¿.])(?=.*[A-Z]).{8,16})");


    public static boolean validateUserName(String username) {
        username = username.replaceAll("\\.", "");
        return USER_NAME_PATTERN.matcher(username).matches();
    }

    public static boolean isUserNameAvailable(String username) {
        //TODO check if user name is avaliable in backend
        return true;
    }

    public static boolean validatePassword(String password) {
        return PASSWORD_PATTERN.matcher(password).matches();
    }

    public static boolean validateEmail(CharSequence email) {
        //return !TextUtils.isEmpty(email) &&
        //        android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        return true;
    }

    public static boolean isEmailAvailable(String email) {
        //TODO check if email is available in backend
        return true;
    }
}
