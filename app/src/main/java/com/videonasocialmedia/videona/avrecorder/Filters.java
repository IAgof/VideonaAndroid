package com.videonasocialmedia.videona.avrecorder;

import android.util.Log;

import com.videonasocialmedia.videona.avrecorder.gles.FullFrameRect;
import com.videonasocialmedia.videona.avrecorder.gles.Texture2dProgram;

import static com.google.gson.internal.$Gson$Preconditions.checkArgument;


/**
 * This class matches descriptive final int
 * variables to Texture2dProgram.ProgramType
 * @hide
 */
public class Filters {
    private static final String TAG = "FiltersFx";
    private static final boolean VERBOSE = true;

    // Camera filters; must match up with camera_filter_names in strings.xml

    // NEW ORDER, only effects filters
    static final int FILTER_NONE = 0;
    static final int FILTER_FISHEYE = 1;
    static final int FILTER_STRETCH = 2;
    static final int FILTER_DENT = 3;
    static final int FILTER_MIRROR = 4;
    static final int FILTER_SQUEEZE = 5;
    static final int FILTER_TUNNEL = 6;
    static final int FILTER_TWIRL = 7;
    static final int FILTER_BULGE = 8;
    // Filters color
    static final int FILTER_NONE_COLOR = 9;
    static final int FILTER_AQUA = 10;
    static final int FILTER_POSTERIZE_BW = 11;
    static final int FILTER_EMBOSS = 12;
    static final int FILTER_MONO = 13;
    static final int FILTER_NEGATIVE = 14;
    static final int FILTER_NIGHT = 15;
    static final int FILTER_POSTERIZE = 16;
    static final int FILTER_SEPIA = 17;


    /**
     * Ensure a filter int code is valid. Update this function as
     * more filters are defined
     * @param filter
     */
    public static void checkFilterArgument(int filter){
        checkArgument(filter >= 0 && filter <= 17);
    }

    /**
     * Updates the filter on the provided FullFrameRect
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
            case FILTER_NONE_COLOR:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT;
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
            /*case FILTER_NEON:
                programType = Texture2dProgram.ProgramType.TEXTURE_EXT_NEON;
                kernel = new float[] {
                        -1f, -1f, -1f,
                        -1f, 8f, -1f,
                        -1f, -1f, -1f };
                colorAdj = 0.5f;
                break;*/
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
