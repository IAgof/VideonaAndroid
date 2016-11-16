/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.domain.model;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.videonasocialmedia.videona.auth.domain.model.PermissionType;

import java.io.File;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class OverlayEffect extends Effect {

    private final String resourcePath;
    private final int resourceId;

    public OverlayEffect(String identifier, String name, String iconPath, String resourcePath,
                         String type, boolean activated) {
        super(identifier, name, iconPath, type, activated);
        this.resourcePath = resourcePath;
        this.resourceId = -1;
    }

    public OverlayEffect(String identifier, String name, String iconPath, int resourceId,
                         String type, boolean activated) {
        super(identifier, name, iconPath, type, activated);
        this.resourcePath = null;
        this.resourceId = resourceId;
    }

    public OverlayEffect(String identifier, String name,  int coverIconId, int iconId,int resourceId,
                         String type, boolean activated) {
        super(identifier, name, coverIconId, iconId,type, activated);
        this.resourcePath = null;
        this.resourceId = resourceId;
    }

    public OverlayEffect(String identifier, String name, int coverIconId, int iconId, int resourceId,
                         String type, String permissionType,  boolean activated ) {
        super(identifier, name, coverIconId, iconId,type, activated, permissionType);
        this.resourcePath = null;
        this.resourceId = resourceId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public int getResourceId() {
        return resourceId;
    }

    public Bitmap getImage() {
        File image = new File(resourcePath);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

        return bitmap;
    }
}
