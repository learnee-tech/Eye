package com.example.eye.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.eye.R;
import com.example.eye.modules.User;
import com.example.eye.network.ApiClient;
import com.example.eye.network.ApiServices;
import com.example.eye.utilities.Constants;
import com.example.eye.utilities.PreferenceManager;
//import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.installations.FirebaseInstallations;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//import android.view.View;
/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;*/

public class OutgoingRequestActivity extends AppCompatActivity {


            private PreferenceManager preferenceManager;            //define
            private String inviterToken = null;                 //token of sender
            String preview = null;
            private TextView textUsername;

            private int rejectionCount = 0;
            private int totalReceivers = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_request);


        preferenceManager = new PreferenceManager(getApplicationContext());             //initialise


        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        textUsername  = findViewById(R.id.textUsername);
        String meetingType = getIntent().getStringExtra("type");


        if(meetingType != null){
            if(meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }
        User user = (User) getIntent().getSerializableExtra("user");
        if(user != null){
            textUsername.setText(String.format("%s %s",user.firstName,user.lastName));
        }
        Button  buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> {
            if(getIntent().getBooleanExtra("isMultiple",false)) {
                Type type = new TypeToken<ArrayList<User>>(){
                }.getType();
                ArrayList<User> receivers = new Gson().fromJson(getIntent().getStringExtra("selectUsers"), type);
                cancelInvitation(null, receivers);
            }else{
                if (user != null) {
                    cancelInvitation(user.token, null);
                }
            }

            });


      /*  FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                inviterToken = task.getResult().getToken();
                if(meetingType != null && user != null){
                    initiateMeeting(meetingType, user.token);
                }


            }
        });*/ FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                inviterToken= (task.getResult());

                if(meetingType != null){
                    if(getIntent().getBooleanExtra("isMultiple",true)){
                        Type type = new TypeToken<ArrayList<User>>(){}.getType();
                        ArrayList<User> receivers;
                        receivers = new Gson().fromJson(getIntent().getStringExtra("selectedUsers"),
                                type);
                        if (receivers != null){
                           totalReceivers = receivers.size();
                       }

                        initiateMeeting(meetingType,null, receivers);
                    }else{
                        if( user != null){
                            totalReceivers = 1;
                            initiateMeeting(meetingType, user.token, null);
                        }
                    }
                }

            }
        });

    }


    private void initiateMeeting(String meetingType, String receiverToken, ArrayList<User> receivers){
        try{                                                                    //body for API request
            JSONArray tokens =  new JSONArray();

            if(receiverToken != null ){
                tokens.put(receiverToken);
            }


            if(receivers != null && receivers.size() > 0){
                StringBuilder userNames =  new StringBuilder();
                for (int i = 0; i<receivers.size(); i++){
                    tokens.put(receivers.get(i).token);
                    userNames.append(receivers.get(i).firstName).append(" ").append(receivers.get(i).lastName).append("\n");
                }

                textUsername.setText(userNames.toString());
            }
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
                                                // with this pass the custom data
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_FIRST_NAME));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);     // to send response if receiver accept or reject


            preview = preferenceManager.getString(Constants.KEY_USER_ID)  + "_" +
                            UUID.randomUUID().toString().substring(0, 5);

            data.put(Constants.REMOTE_MSG_PREVIEW, preview);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                                //call sendremotemessage method

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch(Exception exception){
            Toast.makeText(this,exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //method to send remote message
    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiServices.class).sendRemoteMessage(      //lets call this method from apiservice interface
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {                      //to able the response of the api//Asynchronously send the request and notify callback of its response or if an error occurred talking to the server, creating the request, or processing the response.
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions =
                                    new JitsiMeetConferenceOptions.Builder()
                                            .setServerURL(serverURL)
                                            .setWelcomePageEnabled(false)
                                            .setRoom(preview)
                                            .setVideoMuted(true)
                                            .setAudioMuted(true)
                                            .setFeatureFlag("add-people.enabled", false)
                                            .setFeatureFlag("chat.enabled", false)
                                            .setFeatureFlag("live-streaming.enabled", false)
                                            .setFeatureFlag("meeting-name.enabled", false)
                                            .setFeatureFlag("meeting-password.enabled", false)
                                            // .setFeatureFlag("recording.enabled", false)
                                            .setFeatureFlag("invite.enabled", false)
                                            .setFeatureFlag("notification.enabled",false)
                                            .setFeatureFlag("raise-hand.enabled",false)
                                            .setFeatureFlag("overflow-menu.enabled",false)
                                            .setFeatureFlag("filmstrip.enabled",false)
                                            .setFeatureFlag("video-share.enabled",false)
                                            .setFeatureFlag("close-captions.enabled",false)
                                            .setFeatureFlag("conference-timer.enabled",false)
                                            .setFeatureFlag("calendar.enabled",false)
                                            .setFeatureFlag("call-integration.enabled",false)
                                            .setFeatureFlag("audio-mute.enabled",false)
                                            .build();
                            JitsiMeetActivity.launch(OutgoingRequestActivity.this,conferenceOptions);
                            finish();
                        }catch(Exception exception){
                            Toast.makeText(OutgoingRequestActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        Toast.makeText(OutgoingRequestActivity.this, "request send successfully", Toast.LENGTH_SHORT).show();
                    }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(OutgoingRequestActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
                        //startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                }else{
                    Toast.makeText(OutgoingRequestActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(OutgoingRequestActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    finish();
            }
        });
    }


    private void cancelInvitation(String receiverToken, ArrayList<User> receivers){
        try {

            JSONArray tokens =new JSONArray();

            if(receiverToken != null){
                tokens.put(receiverToken);
            }

            tokens.put(receiverToken);

            if(receivers != null && receivers.size() > 0){
                for (User user : receivers){
                    tokens.put(user.token);
                }
            }

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, Constants.REMOTE_MSG_INVITATION_CANCELLED);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION_RESPONSE);
        } catch(Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){

                    Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                } else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    rejectionCount += 1;
                    if (rejectionCount == totalReceivers){
                        Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }

                }
            }
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                invitationResponseReceiver,
                new IntentFilter(Constants.REMOTE_MSG_INVITATION_RESPONSE)
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(
                invitationResponseReceiver
        );
    }
}

