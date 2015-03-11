/*
 * Copyright (c) 2015. Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.model.sources;



import com.videonasocialmedia.videona.model.entities.social.Session;
import com.videonasocialmedia.videona.model.entities.social.User;
import com.videonasocialmedia.videona.model.entities.social.Video;

/**
 *
 * @author Juan Javier Cabanas
 */
public interface DataSource {

    public User getUser(int id);

    public User getUser(String name);

    public void createUser(String userName, String email, String password);

    public Video getVideo();

    public Session getSession();
}
