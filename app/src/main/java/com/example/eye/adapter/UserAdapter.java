package com.example.eye.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.eye.R;
//import com.google.firebase.firestore.auth.User;
import com.example.eye.activities.MainActivity;
import com.example.eye.listener.UsersListener;
import com.example.eye.modules.User;
import java.util.List;



                    /*Adapter is a bridge between UI component and data source that helps us to fill data in UI component.
                     It holds the data and send the data to an Adapter view then view can takes the data from the adapter view and shows the data on different views like as ListView, GridView, Spinner etc. F
                     */
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
        private final List<User> users;
        private UsersListener usersListener;
        public UserAdapter(List<User> users,UsersListener usersListener) {
                this.users = users;
                this.usersListener = usersListener;
        }

        @NonNull
        @Override
        public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new UserViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(
                                R.layout.item_container_client,
                                parent,
                                false
                        )
                );
        }

        @Override
        public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
                holder.setUserData(users.get(position));
        }

        @Override
        public int getItemCount() {
                return users.size();
        }


         class UserViewHolder extends RecyclerView.ViewHolder{
                        TextView textUsername, textEmail;
                        ImageView imageVideoMeeting;
                public UserViewHolder(@NonNull View itemView) {         //create view for input
                        super(itemView);
                       textUsername  = itemView.findViewById(R.id.textUsername);
                       textEmail = itemView.findViewById(R.id.textEmail);
                       imageVideoMeeting= itemView.findViewById(R.id.imageVideoMeeting);
                }
                void setUserData(com.example.eye.modules.User user)                 //put data into view
                {
                        textUsername.setText(String.format("%s %s", user.firstName, user.lastName));
                        textEmail.setText(user.email);
                        imageVideoMeeting.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                usersListener.initiateVideo(user);
                            }

                        });
                }
        }

}
