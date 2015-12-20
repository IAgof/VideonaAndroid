package com.videonasocialmedia.videona.model.entities.editor.effects;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;

/**
 * Created by Veronica Lago Fominaya on 25/11/2015.
 */
public class OverlayEffect extends Effect {

    private final String resourcePath;
    private final int resourceId;

    public OverlayEffect(String identifier, String name, String iconPath, String resourcePath) {
        super(identifier, name, iconPath);
        this.resourcePath = resourcePath;
        this.resourceId = -1;
    }

    public OverlayEffect(String identifier, String name, String iconPath, int resourceId) {
        super(identifier, name, iconPath);
        this.resourcePath = null;
        this.resourceId = resourceId;
    }

    public OverlayEffect(String identifier, String name, int iconId, int resourceId) {
        super(identifier, name, iconId);
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
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);

        return bitmap;
    }
}
