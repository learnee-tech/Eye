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
import com.example.eye.network.ApiClient;
import com.example.eye.network.ApiServices;
import com.example.eye.utilities.Constants;

import org.jitsi.meet.sdk.JitsiMeetActivity;
import org.jitsi.meet.sdk.JitsiMeetConferenceOptions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URL;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IncomingRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // setContentView(R.layout.activity_incoming_request_activity);//Whenever you want to change the current look of an// Activity or when you move from one Activity to another, the new Activity must have a design to show. We call setContentView in onCreate with the desired design as argument
        //here we get data from intent declared in messaging service.
       // ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
       // String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

      /*  if(meetingType != null){
            if(meetingType.equals("video")){
                imageMeetingType.setImageResource(R.drawable.ic_video);
            }
        }
        TextView textUsername = findViewById(R.id.textUsername);

        String firstname = getIntent().getStringExtra(Constants.KEY_FIRST_NAME);
        textUsername.setText(String.format(
                "%s %s",
                firstname,
                getIntent().getStringExtra(Constants.KEY_LAST_NAME)
        ));*/

        //Button buttonAccept = findViewById(R.id.buttonAccept);
       /* buttonAccept.setOnClickListener((View view) -> {
            sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
            );
        });

        Button buttonReject = findViewById(R.id.buttonReject);
        buttonReject.setOnClickListener(view -> sendInvitationResponse(
                Constants.REMOTE_MSG_INVITATION_REJECTED,
                getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
        ));*/



        sendInvitationResponse(
                    Constants.REMOTE_MSG_INVITATION_ACCEPTED,
                    getIntent().getStringExtra(Constants.REMOTE_MSG_INVITER_TOKEN)
            );




    }
    private final BroadcastReceiver invitationResponseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String type = intent.getStringExtra(Constants.REMOTE_MSG_INVITATION_RESPONSE);
            if(type != null){
                if(type.equals(Constants.REMOTE_MSG_INVITATION_CANCELLED)){
                    Toast.makeText(context, "Request Cancelled", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));

                }
            }
        }
    };
            private void sendInvitationResponse(String type, String receiverToken){
        try {

            JSONArray tokens =new JSONArray();
            tokens.put(receiverToken);

            JSONObject body = new JSONObject();
            JSONObject data = new JSONObject();

            data.put(Constants.REMOTE_MSG_TYPE, Constants.REMOTE_MSG_INVITATION_RESPONSE);
            data.put(Constants.REMOTE_MSG_INVITATION_RESPONSE, type);

            body.put(Constants.REMOTE_MSG_DATA, data);
            body.put(Constants.REMOTE_MSG_REGISTRATION_IDS, tokens);

            sendRemoteMessage(body.toString(), type);
        } catch(Exception exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
            }




    private void sendRemoteMessage(String remoteMessageBody, String type){
        ApiClient.getClient().create(ApiServices.class).sendRemoteMessage(
                Constants.getRemoteMessageHeaders(), remoteMessageBody
        ).enqueue(new Callback<String>() {                      //to able the response of the api
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.isSuccessful()){
                    if(type.equals(Constants.REMOTE_MSG_INVITATION_ACCEPTED)){
                        try {
                            URL serverURL = new URL("https://meet.jit.si");
                            JitsiMeetConferenceOptions conferenceOptions =
                                    new JitsiMeetConferenceOptions.Builder()
                                    .setServerURL(serverURL)
                                    .setWelcomePageEnabled(false)
                                            .setVideoMuted(false)
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
                                            .setRoom(getIntent().getStringExtra(Constants.REMOTE_MSG_PREVIEW))
                                    .build();
                            JitsiMeetActivity.launch(IncomingRequestActivity.this,conferenceOptions);
                            finish();
                        }catch(Exception exception){
                            Toast.makeText(IncomingRequestActivity.this, exception.getMessage(), Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        // Toast.makeText(IncomingRequestActivity.this, "Requested Accepted", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(IncomingRequestActivity.this, "Request Rejected", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{

                    Toast.makeText(IncomingRequestActivity.this, response.message(), Toast.LENGTH_SHORT).show();
                    finish();
                }

            }

            @Override
            public void onFailure(@NonNull Call<String> call,@NonNull Throwable t) {
                Toast.makeText(IncomingRequestActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }


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