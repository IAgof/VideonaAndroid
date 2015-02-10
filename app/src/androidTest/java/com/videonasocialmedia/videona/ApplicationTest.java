package com.videonasocialmedia.videona;

import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<VideonaApplication> {
    public ApplicationTest() {
        super(VideonaApplication.class);
        //createApplication();
    }

    public void testApiClientIsNotNull() throws Exception {
        //assertNotNull((VideonaApplication)this.getApplication().getApiClient());
    }
}