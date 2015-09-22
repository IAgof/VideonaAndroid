/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.videonasocialmedia.avrecorder;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.util.Log;
import android.view.MotionEvent;

import java.nio.FloatBuffer;

/**
 * GL program and supporting functions for textured 2D shapes.
 * @hide
 */
public class Texture2dProgram {
    private static final String TAG ="Texture2dProgram";

    public enum ProgramType {
        TEXTURE_2D, TEXTURE_EXT,TEXTURE_EXT_CHROMA_KEY,
        TEXTURE_EXT_SQUEEZE, TEXTURE_EXT_TWIRL, TEXTURE_EXT_TUNNEL, TEXTURE_EXT_BULGE,
        TEXTURE_EXT_DENT, TEXTURE_EXT_FISHEYE, TEXTURE_EXT_STRETCH, TEXTURE_EXT_MIRROR,
        TEXTURE_EXT_FILT,TEXTURE_EXT_MONO,TEXTURE_EXT_NEGATIVE,TEXTURE_EXT_SEPIA,TEXTURE_EXT_POSTERIZE,
        TEXTURE_EXT_AQUA,TEXTURE_EXT_EMBOSS,TEXTURE_EXT_POSTERIZE_BW,TEXTURE_EXT_NIGHT,
        TEXTURE_EXT_NEON
    }

    // Simple vertex shader, used for all programs.
    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;\n" +
                    "uniform mat4 uTexMatrix;\n" +
                    "attribute vec4 aPosition;\n" +
                    "attribute vec4 aTextureCoord;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "void main() {\n" +
                    "    gl_Position = uMVPMatrix * aPosition;\n" +
                    "    vTextureCoord = (uTexMatrix * aTextureCoord).xy;\n" +
                    "}\n";

    // Simple fragment shader for use with "normal" 2D textures.
    private static final String FRAGMENT_SHADER_2D =
            "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform sampler2D sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    // Simple fragment shader for use with external 2D textures (e.g. what we get from
    // SurfaceTexture).
    private static final String FRAGMENT_SHADER_EXT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "}\n";

    // Fragment shader that converts color to black & white with a simple transformation.
    private static final String FRAGMENT_SHADER_EXT_BW =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    vec4 tc = texture2D(sTexture, vTextureCoord);\n" +
                    "    float color = tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11;\n" +
                    "    gl_FragColor = vec4(color, color, color, 1.0);\n" +
                    "}\n";

    // Fragment shader that attempts to produce a high contrast image
    private static final String FRAGMENT_SHADER_EXT_NIGHT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    vec4 tc = texture2D(sTexture, vTextureCoord);\n" +
                    "    float color = ((tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11) - 0.5 * 1.5) + 0.8;\n" +
                    "    gl_FragColor = vec4(color, color + 0.15, color, 1.0);\n" +
                    "}\n";

    // Fragment shader that applies a Chroma Key effect, making green pixels transparent
    private static final String FRAGMENT_SHADER_EXT_CHROMA_KEY =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "    vec4 tc = texture2D(sTexture, vTextureCoord);\n" +
                    "    float color = ((tc.r * 0.3 + tc.g * 0.59 + tc.b * 0.11) - 0.5 * 1.5) + 0.8;\n" +
                    "    if(tc.g > 0.6 && tc.b < 0.6 && tc.r < 0.6){ \n" +
                    "        gl_FragColor = vec4(0, 0, 0, 0.0);\n" +
                    "    }else{ \n" +
                    "        gl_FragColor = texture2D(sTexture, vTextureCoord);\n" +
                    "    }\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_SQUEEZE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    r = pow(r, 1.0/1.8) * 0.8;\n" +  // Squeeze it
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_TWIRL =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    phi = phi + (1.0 - smoothstep(-0.5, 0.5, r)) * 4.0;\n" + // Twirl it
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_TUNNEL =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    if (r > 0.5) r = 0.5;\n" + // Tunnel
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_BULGE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    r = r * smoothstep(-0.1, 0.5, r);\n" + // Bulge
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_DENT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    r = 2.0 * r - r * smoothstep(0.0, 0.7, r);\n" + // Dent
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_FISHEYE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    float r = length(normCoord); // to polar coords \n" +
                    "    float phi = atan(normCoord.y + uPosition.y, normCoord.x + uPosition.x); // to polar coords \n" +
                    "    r = r * r / sqrt(2.0);\n" + // Fisheye
                    "    normCoord.x = r * cos(phi); \n" +
                    "    normCoord.y = r * sin(phi); \n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_STRETCH =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n" +
                    "    vec2 s = sign(normCoord + uPosition);\n" +
                    "    normCoord = abs(normCoord);\n" +
                    "    normCoord = 0.5 * normCoord + 0.5 * smoothstep(0.25, 0.5, normCoord) * normCoord;\n" +
                    "    normCoord = s * normCoord;\n" +
                    "    texCoord = normCoord / 2.0 + 0.5;\n" +
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n" +
                    "}\n";

    private static final String FRAGMENT_SHADER_MIRROR =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform vec2 uPosition;\n" +
                    "void main() {\n" +
                    "    vec2 texCoord = vTextureCoord.xy;\n" +
                    "    vec2 normCoord = 2.0 * texCoord - 1.0;\n"+
                    "    normCoord.x = normCoord.x * sign(normCoord.x + uPosition.x);\n"+
                    "    texCoord = normCoord / 2.0 + 0.5;\n"+
                    "    gl_FragColor = texture2D(sTexture, texCoord);\n"+
                    "}\n";

    //https://github.com/yulu/GLtext/blob/master/res/raw/negative_fragment_shader.glsl
    private static final String FRAGMENT_SHADER_NEGATIVE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec3 irgb = texture2D(sTexture, vTextureCoord).rgb;\n" +
                    "vec3 neg = vec3(1., 1., 1.)-irgb;\n" +
                    "float T = 1.0;\n"+
                    "gl_FragColor = vec4(mix(irgb,neg, T), 1.);\n"+
                    "}\n";

    //https://github.com/yulu/GLtext/blob/master/res/raw/emboss_fragment_shader.glsl
    private static final String FRAGMENT_SHADER_EMBOSS =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec3 irgb = texture2D(sTexture, vTextureCoord).rgb;\n" +
                    "float ResS = 720.;\n" +
                    "float ResT = 720.;\n" +
                    "vec2 stp0 = vec2(1./ResS, 0.);\n" +
                    "vec2 stpp = vec2(1./ResS, 1./ResT);\n" +
                    "vec3 c00 = texture2D(sTexture, vTextureCoord).rgb;\n" +
                    "vec3 cp1p1 = texture2D(sTexture, vTextureCoord + stpp).rgb;\n" +
                    "vec3 diffs = c00 - cp1p1;\n" +
                    "float max = diffs.r;\n" +
                    "if(abs(diffs.g)>abs(max)) max = diffs.g;\n" +
                    "if(abs(diffs.b)>abs(max)) max = diffs.b;\n" +
                    "float gray = clamp(max + .5, 0., 1.);\n" +
                    "vec3 color = vec3(gray, gray, gray);\n" +
                    "gl_FragColor = vec4(mix(color,c00, 0.1), 1.);\n" +
                    "}\n";

    //http://www.glbasic.com/forum/index.php?topic=8025.0
    private static final String FRAGMENT_SHADER_SEPIA =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec3 texel = texture2D(sTexture, vTextureCoord.xy).rgb;\n"+
                    "gl_FragColor = vec4(texel.x,texel.y,texel.z, 1.0);\n"+
                    "gl_FragColor.r = dot(texel, vec3(.393, .769, .189));\n"+
                    "gl_FragColor.g = dot(texel, vec3(.349, .686, .168));\n"+
                    "gl_FragColor.b = dot(texel, vec3(.272, .534, .131));\n"+
                    "}\n";

    //https://github.com/BradLarson/GPUImage/blob/master/framework/Source/GPUImageSketchFilter.m
    //http://stackoverflow.com/questions/5830139/where-can-i-find-sample-opengl-es-2-0-shaders-that-perform-image-processing-task
    //private static final String FRAGMENT_SHADER_SKETCH =

    // deepskyblue 	#00BFFF 	rgb(0,191,255)
    private static final String FRAGMENT_SHADER_AQUA =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec3 irgb = texture2D(sTexture, vTextureCoord).rgb;\n"+
                    "float gray = dot(irgb, vec3(0.299, 0.587, 0.114));\n" +
                    "gl_FragColor = vec4(gray * vec3(0, 0.749, 1.0), 1.0);\n" +
                    "}\n";

    //https://github.com/technicolorenvy/Processing-Libraries/blob/master/GLGraphics/examples/Integration/MovieFilters/data/Posterize.glsl
    private static final String FRAGMENT_SHADER_POSTERIZE_BW =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main() {\n" +
                    "vec4 color = texture2D(sTexture, vTextureCoord.st);\n" +
                    "float luminance = 0.299 * color.r + 0.587 * color.g + 0.114 * color.b;\n" +
                    "if (luminance < 0.5) gl_FragColor = vec4(0.0, 0.0, 0.0, 1.0);\n" +
                    "else gl_FragColor = vec4(1.0, 1.0, 1.0, 1.0);\n" +
                    "}\n";

    //http://www.filewatcher.com/p/gluon-0.70.0.tar.gz.8234641/gluon-gluon/graphics/shaders/GLSL/posterize.frag.html
    private static final String FRAGMENT_SHADER_POSTERIZE =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "precision mediump float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "void main()\n" +
                    "{\n" +
                    "vec4 outColor = texture2D(sTexture, vTextureCoord.st);\n" +
                    "float level = 5.0;\n" +
                    "outColor.r = floor(outColor.r * level) / level;\n" +
                    "outColor.g = floor(outColor.g * level) / level;\n" +
                    "outColor.b = floor(outColor.b * level) / level;\n" +
                    "gl_FragColor = outColor;\n" +
                    "}";

    public static final int KERNEL_SIZE = 9;
    private static final String FRAGMENT_SHADER_EXT_NEON =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "#define KERNEL_SIZE " + KERNEL_SIZE + "\n" +
                    "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform float uKernel[KERNEL_SIZE];\n" +
                    "uniform vec2 uTexOffset[KERNEL_SIZE];\n" +
                    "uniform float uColorAdjust;\n" +
                    "void main() {\n" +
                    "    int i = 0;\n" +
                    "    vec4 sum = vec4(0.0);\n" +
                    "    for (i = 0; i < KERNEL_SIZE; i++) {\n"+
                    "            vec4 texc = texture2D(sTexture, vTextureCoord + uTexOffset[i]);\n" +
                    "            sum += texc * uKernel[i];\n" +
                    "    }\n" +
                    "    sum += uColorAdjust;\n" +
                    "    gl_FragColor = sum;\n" +
                    "}\n";


    // Fragment shader with a convolution filter.  The upper-left half will be drawn normally,
    // the lower-right half will have the filter applied, and a thin red line will be drawn
    // at the border.
    //
    // This is not optimized for performance.  Some things that might make this faster:
    // - Remove the conditionals.  They're used to present a half & half view with a red
    //   stripe across the middle, but that's only useful for a demo.
    // - Unroll the loop.  Ideally the compiler does this for you when it's beneficial.
    // - Bake the filter kernel into the shader, instead of passing it through a uniform
    //   array.  That, combined with loop unrolling, should reduce memory accesses.
    //public static final int KERNEL_SIZE = 9;
    private static final String FRAGMENT_SHADER_EXT_FILT =
            "#extension GL_OES_EGL_image_external : require\n" +
                    "#define KERNEL_SIZE " + KERNEL_SIZE + "\n" +
                    "precision highp float;\n" +
                    "varying vec2 vTextureCoord;\n" +
                    "uniform samplerExternalOES sTexture;\n" +
                    "uniform float uKernel[KERNEL_SIZE];\n" +
                    "uniform vec2 uTexOffset[KERNEL_SIZE];\n" +
                    "uniform float uColorAdjust;\n" +
                    "void main() {\n" +
                    "    int i = 0;\n" +
                    "    vec4 sum = vec4(0.0);\n" +
                    "    for (i = 0; i < KERNEL_SIZE; i++) {\n"+
                    "            vec4 texc = texture2D(sTexture, vTextureCoord + uTexOffset[i]);\n" +
                    "            sum += texc * uKernel[i];\n" +
                    "    }\n" +
                    "    sum += uColorAdjust;\n" +
                    "    gl_FragColor = sum;\n" +
                    "}\n";

    private ProgramType mProgramType;

    private float mTexWidth;
    private float mTexHeight;

    // Handles to the GL program and various components of it.
    private int mProgramHandle;
    private int muMVPMatrixLoc;
    private int muTexMatrixLoc;
    private int muKernelLoc;
    private int muTexOffsetLoc;
    private int muColorAdjustLoc;
    private int maPositionLoc;
    private int maTextureCoordLoc;
    private int muTouchPositionLoc;

    private int mTextureTarget;

    private float[] mKernel = new float[KERNEL_SIZE];       // Inputs for convolution filter based shaders
    private float[] mSummedTouchPosition = new float[2];    // Summed touch event delta
    private float[] mLastTouchPosition = new float[2];      // Raw location of last touch event
    private float[] mTexOffset;
    private float mColorAdjust;


    /**
     * Prepares the program in the current EGL context.
     */
    public Texture2dProgram(ProgramType programType) {
        mProgramType = programType;

        switch (programType) {
            case TEXTURE_2D:
                mTextureTarget = GLES20.GL_TEXTURE_2D;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_2D);
                break;
            case TEXTURE_EXT:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT);
                break;
            case TEXTURE_EXT_CHROMA_KEY:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT_CHROMA_KEY);
                break;
            case TEXTURE_EXT_SQUEEZE:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_SQUEEZE);
                break;
            case TEXTURE_EXT_TWIRL:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_TWIRL);
                break;
            case TEXTURE_EXT_TUNNEL:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_TUNNEL);

                break;
            case TEXTURE_EXT_BULGE:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_BULGE);
                break;
            case TEXTURE_EXT_FISHEYE:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_FISHEYE);
                break;
            case TEXTURE_EXT_DENT:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_DENT);
                break;
            case TEXTURE_EXT_MIRROR:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_MIRROR);
                break;
            case TEXTURE_EXT_STRETCH:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_STRETCH);
                break;
            case TEXTURE_EXT_FILT:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT_FILT);
                break;

            //COLOR Filters
            case TEXTURE_EXT_MONO:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT_BW);
                break;
            case TEXTURE_EXT_NEGATIVE:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_NEGATIVE);
                break;
            case TEXTURE_EXT_SEPIA:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_SEPIA);
                break;
            case TEXTURE_EXT_POSTERIZE:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_POSTERIZE);
                break;
            case TEXTURE_EXT_AQUA:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_AQUA);
                break;
            case TEXTURE_EXT_EMBOSS:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EMBOSS);
                break;
            case TEXTURE_EXT_POSTERIZE_BW:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_POSTERIZE_BW);
                break;
            case TEXTURE_EXT_NIGHT:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT_NIGHT);
                break;
            case TEXTURE_EXT_NEON:
                mTextureTarget = GLES11Ext.GL_TEXTURE_EXTERNAL_OES;
                mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER_EXT_FILT);
                break;
            default:
                throw new RuntimeException("Unhandled type " + programType);
        }
        if (mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }
        Log.d(TAG, "Created program " + mProgramHandle + " (" + programType + ")");

        // get locations of attributes and uniforms

        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        maTextureCoordLoc = GLES20.glGetAttribLocation(mProgramHandle, "aTextureCoord");
        GlUtil.checkLocation(maTextureCoordLoc, "aTextureCoord");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muTexMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexMatrix");
        GlUtil.checkLocation(muTexMatrixLoc, "uTexMatrix");
        muKernelLoc = GLES20.glGetUniformLocation(mProgramHandle, "uKernel");
        if (muKernelLoc < 0) {
            // no kernel in this one
            muKernelLoc = -1;
            muTexOffsetLoc = -1;
            muColorAdjustLoc = -1;
        } else {
            // has kernel, must also have tex offset and color adj
            muTexOffsetLoc = GLES20.glGetUniformLocation(mProgramHandle, "uTexOffset");
            GlUtil.checkLocation(muTexOffsetLoc, "uTexOffset");
            muColorAdjustLoc = GLES20.glGetUniformLocation(mProgramHandle, "uColorAdjust");
            GlUtil.checkLocation(muColorAdjustLoc, "uColorAdjust");

            // initialize default values
            setKernel(new float[] {0f, 0f, 0f,  0f, 1f, 0f,  0f, 0f, 0f}, 0f);
            setTexSize(256, 256);
        }

        muTouchPositionLoc = GLES20.glGetUniformLocation(mProgramHandle, "uPosition");
        if(muTouchPositionLoc < 0){
            // Shader doesn't use position
            muTouchPositionLoc = -1;
        }else{
            // initialize default values
            //handleTouchEvent(new float[]{0f, 0f});
        }
    }

    /**
     * Releases the program.
     */
    public void release() {
        Log.d(TAG, "deleting program " + mProgramHandle);
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
    }

    /**
     * Returns the program type.
     */
    public ProgramType getProgramType() {
        return mProgramType;
    }

    /**
     * Creates a texture object suitable for use with this program.
     * <p>
     * On exit, the texture will be bound.
     */
    public int createTextureObject() {
        int[] textures = new int[1];
        GLES20.glGenTextures(1, textures, 0);
        GlUtil.checkGlError("glGenTextures");

        int texId = textures[0];
        GLES20.glBindTexture(mTextureTarget, texId);
        GlUtil.checkGlError("glBindTexture " + texId);

        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_NEAREST);
        GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE);
        GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE);
        GlUtil.checkGlError("glTexParameter");

        return texId;
    }

    /**
     * Configures the effect offset
     *
     * This only has an effect for programs that
     * use positional effects like SQUEEZE and MIRROR
     */
    public void handleTouchEvent(MotionEvent ev){
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            // A finger is dragging about
            if(mTexHeight != 0 && mTexWidth != 0){
                mSummedTouchPosition[0] += (2 * (ev.getX() - mLastTouchPosition[0])) / mTexWidth;
                mSummedTouchPosition[1] += (2 * (ev.getY() - mLastTouchPosition[1])) / -mTexHeight;
                mLastTouchPosition[0] = ev.getX();
                mLastTouchPosition[1] = ev.getY();
            }
        }else if(ev.getAction() == MotionEvent.ACTION_DOWN){
            // The primary finger has landed
            mLastTouchPosition[0] = ev.getX();
            mLastTouchPosition[1] = ev.getY();
        }
    }

    /**
     * Configures the convolution filter values.
     * This only has an effect for programs that use the
     * FRAGMENT_SHADER_EXT_FILT Fragment shader.
     *
     * @param values Normalized filter values; must be KERNEL_SIZE elements.
     */
    public void setKernel(float[] values, float colorAdj) {
        if (values.length != KERNEL_SIZE) {
            throw new IllegalArgumentException("Kernel size is " + values.length +
                    " vs. " + KERNEL_SIZE);
        }
        System.arraycopy(values, 0, mKernel, 0, KERNEL_SIZE);
        mColorAdjust = colorAdj;
        //Log.d(TAG, "filt kernel: " + Arrays.toString(mKernel) + ", adj=" + colorAdj);
    }

    /**
     * Sets the size of the texture.  This is used to find adjacent texels when filtering.
     */
    public void setTexSize(int width, int height) {
        mTexHeight = height;
        mTexWidth = width;
        float rw = 1.0f / width;
        float rh = 1.0f / height;

        // Don't need to create a new array here, but it's syntactically convenient.
        mTexOffset = new float[] {
                -rw, -rh,   0f, -rh,    rw, -rh,
                -rw, 0f,    0f, 0f,     rw, 0f,
                -rw, rh,    0f, rh,     rw, rh
        };
        //Log.d(TAG, "filt size: " + width + "x" + height + ": " + Arrays.toString(mTexOffset));
    }

    /**
     * Issues the draw call.  Does the full setup on every call.
     *
     * @param mvpMatrix The 4x4 projection matrix.
     * @param vertexBuffer Buffer with vertex position data.
     * @param firstVertex Index of first vertex to use in vertexBuffer.
     * @param vertexCount Number of vertices in vertexBuffer.
     * @param coordsPerVertex The number of coordinates per vertex (e.g. x,y is 2).
     * @param vertexStride Width, in bytes, of the position data for each vertex (often
     *        vertexCount * sizeof(float)).
     * @param texMatrix A 4x4 transformation matrix for texture coords.
     * @param texBuffer Buffer with vertex texture data.
     * @param texStride Width, in bytes, of the texture data for each vertex.
     */
    public void draw(float[] mvpMatrix, FloatBuffer vertexBuffer, int firstVertex,
                     int vertexCount, int coordsPerVertex, int vertexStride,
                     float[] texMatrix, FloatBuffer texBuffer, int textureId, int texStride) {
        GlUtil.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Set the texture.
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(mTextureTarget, textureId);

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        // Copy the texture transformation matrix over.
        GLES20.glUniformMatrix4fv(muTexMatrixLoc, 1, false, texMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
                GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Enable the "aTextureCoord" vertex attribute.
        GLES20.glEnableVertexAttribArray(maTextureCoordLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect texBuffer to "aTextureCoord".
        GLES20.glVertexAttribPointer(maTextureCoordLoc, 2,
                GLES20.GL_FLOAT, false, texStride, texBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Populate the convolution kernel, if present.
        if (muKernelLoc >= 0) {
            GLES20.glUniform1fv(muKernelLoc, KERNEL_SIZE, mKernel, 0);
            GLES20.glUniform2fv(muTexOffsetLoc, KERNEL_SIZE, mTexOffset, 0);
            GLES20.glUniform1f(muColorAdjustLoc, mColorAdjust);
        }

        // Populate touch position data, if present
        if (muTouchPositionLoc >= 0){
            GLES20.glUniform2fv(muTouchPositionLoc, 1, mSummedTouchPosition, 0);
        }

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array, texture, and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glDisableVertexAttribArray(maTextureCoordLoc);
        GLES20.glBindTexture(mTextureTarget, 0);
        GLES20.glUseProgram(0);
    }
}