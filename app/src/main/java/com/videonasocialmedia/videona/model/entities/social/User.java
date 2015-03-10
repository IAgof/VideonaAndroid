package com.videonasocialmedia.videona.model.entities.social;

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
 * Class that represents the User in the model layer,
 */
public class User {
    private String name;
    private String email;
    private int id;
    private String avatarPath;

    public User (String name){
        this.name=name;
    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public User (String name, String email, String avatarPath, int id){
        this.name = name;
        this.email = email;
        this.avatarPath = avatarPath;
        this.id= id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }


}
