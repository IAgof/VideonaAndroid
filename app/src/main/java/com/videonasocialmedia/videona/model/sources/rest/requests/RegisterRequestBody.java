package com.videonasocialmedia.videona.model.sources.rest.requests;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */

/**
 * Class that implements the body of the api request to register a new user
 */
public class RegisterRequestBody {
    /*
        {
            username: "foo",
                email: "foo@bar.com",
                pass: "asdjw0213"
        }
    */
    String username;
    String email;
    String password;

    public RegisterRequestBody(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
