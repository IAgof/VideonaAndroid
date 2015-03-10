package com.videonasocialmedia.videona.model.sources;

import com.videonasocialmedia.videona.model.entities.social.Session;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.model.entities.social.Video;

/**
 * Created by jca on 5/3/15.
 */
public interface DataSource {

    public User getUser(int id);

    public User getUser(String name);

    public void createUser(String userName, String email, String password);

    public Video getVideo();

    public Session getSession();
}
