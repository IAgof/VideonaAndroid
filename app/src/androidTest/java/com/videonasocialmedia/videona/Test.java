package com.videonasocialmedia.videona;

/**
 *
 * https://www.youtube.com/watch?v=z47B1nhC3K0
 *
 * Created by jca on 19/1/15.
 */

import android.test.AndroidTestCase;

import junit.framework.TestCase;



public class Test extends AndroidTestCase {

    public void testFail() throws Exception {
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }


    public void testCorrect() throws Exception{
        final int expected = 1;
        final int reality = 5;
        assertEquals(expected, reality);
    }

}
