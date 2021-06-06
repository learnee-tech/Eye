package com.example.eye.network;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

            //interface to declare endpoints.
            //An interface where we will make a Post Request to our FIREBASE
public interface ApiServices {

            @POST("send")               //post when, to send data from app to serevr.
                // endpoint is send the complete url is basr url+ send
    Call<String > sendRemoteMessage(                                                //Create a new, identical call to this one which can be enqueued or executed even if this call has already been.
                    @HeaderMap HashMap<String, String> headers,
                    @Body String remoteBody
                    );
}

