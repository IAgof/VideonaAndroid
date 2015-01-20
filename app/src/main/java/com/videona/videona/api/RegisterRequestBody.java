package com.videona.videona.api;

/**
 * Created by jca on 8/1/15.
 */
public class RegisterRequestBody {
/*
    {
        user_name: "foo",
            email: "foo@bar.com",
            pass: "asdjw0213"
    }
*/
    String user_name;
    String email;
    String password;

    public RegisterRequestBody(String user_name, String email, String password) {
        this.user_name = user_name;
        this.email = email;
        this.password = password;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
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
