package com.videonasocialmedia.videona.api;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;
import retrofit.http.Query;

/**
 * Created by jca on 8/1/15.
 */
public interface ApiClient {

    @POST("/signup")
    void register(@Body RegisterRequestBody requestBody, Callback<Response> callback);

    @GET("/login")
    void login(@Header("Authorization") String authorization,
               @Query("_remember_me") int rememeberMe, Callback<Response> callback);

    @POST("/logout")
    void logout(@Header("Cookie") String cookie, Callback<Response> callback);


    @GET("/users/{id}/profile")
    void getUserProfile(@Path("id") int id, Callback<Response> callback);

    //Puede que fuera mejor hacer la peticion con picasso
    @GET("/users/{id}/profile/Avatar")
    void getUserAvatar(@Path("id") int id, Callback callback);

    //Tengo dudas sobre el cuerpo del put
    @PUT("/users/{id}/profile/Avatar")
    void updateUserAvatar(@Path("id") int id, Callback callback);

    @GET("/users/{id}/profile/name")
    void getUserName(@Path("id") int id, Callback callback);

    @GET("/users/{id}/videos")
    void getUserVideos(@Path("id") int id, Callback callback);
}
