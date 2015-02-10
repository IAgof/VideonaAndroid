package com.videonasocialmedia.videona.api;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by jca on 8/1/15.
 */
public interface ApiClient {

    @POST("/signup")
    void register(@Body RegisterRequestBody requestBody, Callback<Response> callback);

    @GET("/login")
    void login(@Header("Authorization") String authorization, Callback<Response> callback);

    @POST("/logout")
    void logout(@Header("Cookie") String cookie, Callback<Response> callback);


    @GET("/users/{id}/profile")
    void getUserProfile(@Path("id") int id, @Header("Cookie") String Cookie, Callback<Response> callback);

    //Puede que fuera mejor hacer la peticion con picasso
    @GET("/users/{id}/profile/Avatar")
    void getUserAvatar(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    //Tengo dudas sobre el cuerpo del put
    @PUT("/users/{id}/profile/Avatar")
    void updateUserAvatar(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    @GET("/users/{id}/profile/name")
    void getUserName(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);

    @GET("/users/{id}/videos")
    void getUserVideos(@Header("Authorization") String authorization, @Path("id") int id, Callback callback);
}
