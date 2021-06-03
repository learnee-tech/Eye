package com.example.eye.network;

import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class ApiClient {


    /*REST Client is a method or a tool to invoke a REST service API that is
        exposed for communication by any system or service provider.*/

    private static Retrofit retrofit = null;    //instance od retrofit
            public static Retrofit getClient(){
                if(retrofit == null){
                    retrofit = new Retrofit.Builder()
                            .baseUrl("https://fcm.googleapis.com/fcm/")
                            .addConverterFactory(ScalarsConverterFactory.create())
                            .build();
                }
                return retrofit;
            }


}
