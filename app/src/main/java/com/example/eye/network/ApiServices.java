package com.example.eye.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;


            //An interface where we will make a Post Request to our server
public interface ApiServices {

            @POST("send")               // endpoint is send the complete url is basr url+ send
    Call<String > sendRemoteMessage(
                    @HeaderMap HashMap<String, String> headers,
                    @Body String remoteBody
                    );
}

