package com.videonasocialmedia.avrecorder;

import android.util.Log;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;

/**
 * This class matches descriptive final int
 * variables to Texture2dProgram.ProgramType
 *
 * @hide
 */
public class Filters {
    // NEW ORDER, only effects filters
    public static final int FILTER_NONE = 0;
    public static final int FILTER_FISHEYE = 1;
    public static final int FILTER_STRETCH = 2;
    public static final int FILTER_DENT = 3;
    public static final int FILTER_MIRROR = 4;
    public static final int FILTER_SQUEEZE = 5;
    public static final int FILTER_TUNNEL = 6;
    public static final int FILTER_TWIRL = 7;
    public static final int FILTER_BULGE = 8;
    // Filters color
    public static final int FILTER_AQUA = 9;
    public static final int FILTER_POSTERIZE_BW = 10;
    public static final int FILTER_EMBOSS = 11;
    public static final int FILTER_MONO = 12;
    public static final int FILTER_NEGATIVE = 13;
    public static final int FILTER_NIGHT = 14;
    public static final int FILTER_POSTERIZE = 15;
    public static final int FILTER_SEPIA = 16;
    private static final String TAG = "Filters";
    private static final boolean VERBOSE = false;

    /**
     * Ensure a filter int code is valid. Update this function as
     * more filters are defined
     *
     * @param filter
     */
    public static void checkFilterArgument(int filter) {
        checkArgument(filter >= 0 && filter <= 16);
    }

    /**
     * Updates the filter on the provided FullFrameRect
     *
     * @return the int code of the new filter
     */
    public static void updateFilter(FullFrameRect rect, int newFilter) {
        Texture2dProgram.ProgramType programType;
        float[] kernel = null;
        float colorAdj = 0.0f;

        if (VERBOSE) Log.d(TAG, "Updating filter to " + newFilter);
        switch (newFilter) {
            case FILTER_NONE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT;
                break;
            case FILTER_SQUEEZE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_SQUEEZE;
                break;
            case FILTER_TWIRL:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_TWIRL;
                break;
            case FILTER_TUNNEL:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_TUNNEL;
                break;
            case FILTER_BULGE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_BULGE;
                break;
            case FILTER_DENT:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_DENT;
                break;
            case FILTER_FISHEYE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_FISHEYE;
                break;
            case FILTER_STRETCH:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_STRETCH;
                break;
            case FILTER_MIRROR:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_MIRROR;
                break;
            case FILTER_MONO:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_MONO;
                break;
            case FILTER_NEGATIVE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_NEGATIVE;
                break;
            case FILTER_SEPIA:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_SEPIA;
                break;
            case FILTER_POSTERIZE:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_POSTERIZE;
                break;
            case FILTER_AQUA:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_AQUA;
                break;
            case FILTER_EMBOSS:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_EMBOSS;
                break;
            case FILTER_POSTERIZE_BW:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_POSTERIZE_BW;
                break;
            case FILTER_NIGHT:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_NIGHT;
                break;
            default:
                throw new RuntimeException("Unknown filter mode " + newFilter);
        }

        // Do we need a whole new program?  (We want to avoid doing this if we don't have
        // too -- compiling a program could be expensive.)
        if (programType != rect.getProgram().getProgramType()) {
            rect.changeProgram(new Texture2dProgram(programType));
        }

        // Update the filter kernel (if any).
        if (kernel != null) {
            rect.getProgram().setKernel(kernel, colorAdj);
        }
    }
}
