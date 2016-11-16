/*
 * Copyright (C) 2016 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 */

package com.videonasocialmedia.videona.effects.repository.model;

import io.realm.RealmList;

/**
 * Created by alvaro on 16/11/16.
 */

public interface Mapper<From, To> {
  To map(From from);
}
