package com.example.eye.firebase;

import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.eye.activities.IncomingRequestActivity;
import com.example.eye.utilities.Constants;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);            //onNewToken will give you the token onNewToken() will be called only when there is a new token generated or existing token updated
        // testing purpose (can be removed also)  Log.d("FCM", "Token: " + token);

    }
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String type = remoteMessage.getData().get(Constants.REMOTE_MSG_TYPE);

        if(type != null) {
            //check if invitation type and then start incomingrequestactivity using intent
            if (type.equals(Constants.REMOTE_MSG_INVITATION)) {
                Intent intent = new Intent(getApplicationContext(), IncomingRequestActivity.class);
                /*intent.putExtra(
                        Constants.REMOTE_MSG_MEETING_TYPE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_MEETING_TYPE)
                );
                intent.putExtra(
                        Constants.KEY_FIRST_NAME,
                        remoteMessage.getData().get(Constants.KEY_FIRST_NAME)
                );
                intent.putExtra(
                        Constants.KEY_LAST_NAME,
                        remoteMessage.getData().get(Constants.KEY_LAST_NAME)
                );*/
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITER_TOKEN,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITER_TOKEN)
                );
                intent.putExtra(
                        Constants.REMOTE_MSG_PREVIEW,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_PREVIEW)
                );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            } /*else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                Intent intent = new Intent(Constants.REMOTE_MSG_INVITATION_RESPONSE);
                intent.putExtra(
                        Constants.REMOTE_MSG_INVITATION_RESPONSE,
                        remoteMessage.getData().get(Constants.REMOTE_MSG_INVITATION_RESPONSE)
                );
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
            }*/
            }
        }

        //when we call API for sending meeting request we get remote message inside this onmessagereceived method.

        //method to handle incoming messages.
       /*// testing purpose (can be removed also)// if (remoteMessage.getNotification() != null) {
            Log.d(
                    "FCM",
                    "Remote message received: " + remoteMessage.getNotification().getBody()
            );
        }*/

}
