package com.videona.videona.api;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by jca on 8/1/15.
 */
public interface Api {
    @POST("/users/signup")
    void register(@Body RegisterRequestBody requestBody, Callback callback);

    @POST("users/login")
    void login(@Body LoginRequestBody requestBody, Callback callback);

    @POST("users/logout")
    void logout(@Header("Authorization") String authorization, Callback callback);

    @GET("users/{id}/profile")
    void getUserProfile(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    //Puede que fuera mejor hacer la peticion con picasso
    @GET("users/{id}/profile/Avatar")
    void getUserAvatar(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    //Tengo dudas sobre el cuerpo del put
    @PUT("users/{id}/profile/Avatar")
    void updateUserAvatar(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    @GET("users/{id}/profile/name")
    void getUserName(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    @GET("users/{id}/videos")
    void getUserVideos(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);
}
