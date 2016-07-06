/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.network;

import com.videonasocialmedia.videona.model.entities.editor.media.Video;
import com.videonasocialmedia.videona.network.repository.apiclient.VideoInfoApi;
import com.videonasocialmedia.videona.network.repository.model.VideoMetadataRequest;
import com.videonasocialmedia.videona.network.repository.model.VideoResponse;
import com.videonasocialmedia.videona.network.repository.rest.ServiceGenerator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import okhttp3.HttpUrl;

import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import retrofit2.Response;

import static org.junit.Assert.assertEquals;

/**
 * Created by alvaro on 29/06/16.
 */
public class VideoInfoTest {

    MockWebServer server;
    VideoInfoApi videoInfoApi;

    @Before
    public void setUp() throws IOException {

        server = new MockWebServer();
        server.start();
        HttpUrl baseUrl = server.url("/");
        videoInfoApi = new ServiceGenerator(baseUrl.toString()).generateService(VideoInfoApi.class);

    }

    @After
    public void tearDown() throws IOException {
        server.shutdown();
    }

    private VideoResponse performVideoResponse() throws IOException {

        Video video = new Video("somePath");
        video.setTitle("VID");
        video.setLocationLatitude(44.5);
        video.setLocationLongitude(-3.45);
        video.setHeight(720);
        video.setWidth(1280);
        video.setBitRate(1000000);
        video.setDate("date");
        video.setSize(123456789);
        video.setFileDuration(125000);


        VideoMetadataRequest videoMetadata = new VideoMetadataRequest(video.getLocationLatitude(),
                video.getLocationLongitude(),video.getHeight(), video.getWidth(),
                video.getRotation(), video.getFileDuration(), video.getSize(),
                video.getDate(), video.getBitRate(),video.getTitle(), VideoMetadataRequest.VIDEO_TYPE.Recorded);

        Response<VideoResponse> response = videoInfoApi.sendInfoVideo(videoMetadata).execute();

        return response.body();
    }

    @Test
    public void ShouldReceiveSuccessIfVideoInfoHasBeenReceivedCorrectly() throws Exception {

        VideoResponse videoResponse = performVideoResponse();
        RecordedRequest videoRequest = server.takeRequest();
        assertEquals("/user/new_video", videoRequest.getPath());
        assertEquals(videoResponse.getBackendResponse(), "ok");
    }

}
