package com.example.eye.utilities;

import java.util.HashMap;

public class Constants {


            //most of the values are key value pair to avoid mistakes we use constants
    public static final String KEY_COLLECTION_USERS = "users";
    public static final String KEY_FIRST_NAME = "first_name";
    public static final String KEY_LAST_NAME = "last_name";
    public static final String KEY_EMAIL= "email";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_PREFERENCE_NAME = "eyePreference";
    public static final String KEY_IS_SIGNED_IN = "isSignedIn";
    public static final String KEY_USER_ID = "user_id";
    public static  final  String KEY_FCM_TOKEN = "fcm_token";
    //define two constants or authorization
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";

          //to send custom data using remote message
    public static final String REMOTE_MSG_TYPE = "type";
    public static final String REMOTE_MSG_INVITATION = "invitation";
    public static final String REMOTE_MSG_MEETING_TYPE = "meetingType";
    public static final String REMOTE_MSG_INVITER_TOKEN = "inviterToken";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration_ids";


    public static HashMap<String, String> getRemoteMessageHeaders(){
        HashMap<String, String> headers = new HashMap<>();
        headers.put(
                Constants.REMOTE_MSG_AUTHORIZATION,
                "key = AAAA5Lr6XgE:APA91bEDHZTbVhSPBu6428iQN1cJYamUjmaS1bqoQbGNPq1pi88MhutUnjZd28lu9vtu53U1Xj4ZxadNM6T-c215iYbR1jRW-eO8KpxZN5Tx8XeGGDEfIwjrMnixn5wt3TLGqWq2e-KZ"
        );
        headers.put(
                Constants.REMOTE_MSG_CONTENT_TYPE,"application/json");
        return headers;
    }
}
