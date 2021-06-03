package com.example.eye.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.media.Image;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.eye.R;
import com.example.eye.utilities.Constants;

public class IncomingRequestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incoming_request_activity);//Whenever you want to change the current look of an// Activity or when you move from one Activity to another, the new Activity must have a design to show. We call setContentView in onCreate with the desired design as argument
        //here we get data from intent declared in messaging service.
        ImageView imageMeetingType = findViewById(R.id.imageMeetingType);
        String meetingType = getIntent().getStringExtra(Constants.REMOTE_MSG_MEETING_TYPE);

        if(meetingType != null){
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
        ));



    }


}