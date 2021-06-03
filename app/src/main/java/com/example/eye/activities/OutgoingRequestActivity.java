package com.example.eye.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
//import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eye.R;
import com.example.eye.modules.User;
import com.example.eye.network.ApiClient;
import com.example.eye.network.ApiServices;
import com.example.eye.utilities.Constants;
import com.example.eye.utilities.PreferenceManager;
/*import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;*/
import com.google.firebase.installations.FirebaseInstallations;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OutgoingRequestActivity extends AppCompatActivity {


            private PreferenceManager preferenceManager;            //define
            private String inviterToken = null;                 //token of sender
            String preview = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outgoing_request);


        preferenceManager = new PreferenceManager(getApplicationContext());             //initialise


        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        TextView textUsername  = findViewById(R.id.textUsername);
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
        Button   buttonCancel = findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(view -> {
            if(user != null){
                cancelInvitation(user.token);
            }
        });


        FirebaseInstallations.getInstance().getToken(true).addOnCompleteListener(task -> {
            if(task.isSuccessful() && task.getResult() != null){
                inviterToken = task.getResult().getToken();
                if(meetingType != null && user != null){
                    initiateMeeting(meetingType, user.token);
                }


            }
        });

    }


    private void initiateMeeting(String meetingType, String receiverToken){
        try{
            JSONArray tokens =  new JSONArray();
            tokens.put(receiverToken);
                                                       //body for API request
            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();
                                                // with this pass the custom data
            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION);
            data.put(Constants.REMOTE_MSG_MEETING_TYPE, meetingType);
            data.put(Constants.KEY_FIRST_NAME, preferenceManager.getString(Constants.KEY_USER_ID));
            data.put(Constants.KEY_LAST_NAME, preferenceManager.getString(Constants.KEY_LAST_NAME));
            data.put(Constants.KEY_EMAIL, preferenceManager.getString(Constants.KEY_EMAIL));
            data.put(Constants.REMOTE_MSG_INVITER_TOKEN, inviterToken);     // to send response if receiver accept or reject


            preview =
                    preferenceManager.getString(Constants.KEY_USER_ID)  + "_" +
                            UUID.randomUUID().toString().substring(0, 5);
            data.put(Constants.REMOTE_MSG_PREVIEW, preview);
            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);
                                //call sendremotemessage method
            sendRemoteMessage(body.toString(), Constants.REMOTE_MSG_INVITATION);

        } catch(Exception exception){
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
    //method to send remote message
    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiServices.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {                      //to able the response of the api
            @Override
            public void onResponse(@NonNull Call<String> call,@NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION)) {
                        Toast.makeText(OutgoingRequestActivity.this, "request send successfully", Toast.LENGTH_SHORT).show();
                    }else if(type.equals(Constants.REMOTE_MSG_INVITATION_RESPONSE)){
                        Toast.makeText(OutgoingRequestActivity.this, "Request Cancelled", Toast.LENGTH_SHORT).show();
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


    private void cancelInvitation(String receiverToken){
        try {

            JSONArray tokens =new JSONArray();
            tokens.put(receiverToken);

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

    private BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                    try {
                        URL serverURL = new URL("https://meet.jit.si");
                        JitsiMeetConferenceOptions conferenceOptions =
                                new JitsiMeetConferenceOptions.Builder()
                                        .setServerURL(serverURL)
                                        .setWelcomePageEnabled(false)
                                        .setRoom(preview)
                                        .setVideoMuted(false)
                                        .setAudioMuted(true)
                                        .build();
                        JitsiMeetActivity.launch(OutgoingRequestActivity.this,conferenceOptions);
                        finish();
                    }catch(Exception exception){
                        Toast.makeText(OutgoingRequestActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    // Toast.makeText(context, "Request Accepted", Toast.LENGTH_SHORT).show();
                } else if(type.equals(Constants.REMOTE_MSG_INVITATION_REJECTED)){
                    Toast.makeText(context, "Request Rejected", Toast.LENGTH_SHORT).show();
                    finish();
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

