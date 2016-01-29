package com.videonasocialmedia.videona.model.entities.social;

import android.graphics.drawable.Drawable;

/**
 * Created by jca on 9/12/15.
 */
public class SocialNetwork {
    private final String name;
    private final String androidPackageName;
    private final String androidActivityName;
    private final Drawable icon;
    private final String defaultMessage;

    public SocialNetwork(String name, String androidPackageName, String androidActivityName,
                         Drawable icon, String defaultMessage) {
        this.name = name;
        this.androidPackageName = androidPackageName;
        this.androidActivityName = androidActivityName;
        this.icon=icon;
        this.defaultMessage = defaultMessage;
    }

    public String getName() {
        return name;
    }

    public String getAndroidPackageName() {
        return androidPackageName;
    }

    public String getAndroidActivityName() {
        return androidActivityName;
    }

    public Drawable getIcon() {
        return icon;
    }
}
