/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 * Álvaro Martínez Marco
 * Danny R. Fonseca Arboleda
 */
package com.videonasocialmedia.videona.model.entities.licensing;

/**
 * First implementation of licensing for media used in videona. This need to be revised and probably
 * replaced by supported classes of android development community like DRM or something.
 * <p/>
 * Created by dfa on 8/4/15.
 */
public class License {

    /**
     * The license text.
     */
    private String text;

    /**
     * The license reference name.
     */
    private String name;

    public License(String text, String name) {
        this.text = text;
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public final static String CC40_NAME = "CC BY-NC-SA 4.0";
    public final static String CC40_TEXT = "Summary\n" +
            "Creative Commons Attribution-NoDeratives license in International version 4, that " +
            "allows to do what they want except modifying.\n Attribution\n You must give credit to " +
            "the original author of the work, including a URI or hyperlink to the work, this Public" +
            " license and a copyright notice.\n\nAttribution information revoke\n\nAuthor can " +
            "request to remove any attribution given information.\n\nTivoization\nYou may not apply " +
            "legal terms or technological measures that legally restrict others from doing anything " +
            "the license permits.\n\nDisclaimer of warranties\nDisclaimer of warranties is optional." +
            "\n\nIf separately undertaken, shared material must retain a notice to Disclaimer of " +
            "warranties.\n\nOtherwise, Disclaimer of warranties, is taken by default, providing the " +
            "work as-is and as-available.\n\nLiable\nLiable follows the same rules as Disclaimer of " +
            "warranties, providing, by default, protection from defamation for the creator.\n\n" +
            "Revoke\n The licensor cannot revoke these freedoms as long as you follow the license " +
            "terms.";
}
