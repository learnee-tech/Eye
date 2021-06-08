package com.example.eye.listener;

import com.example.eye.modules.User;

public interface UsersListener {
    void initiateVideo(User user);


    void onMultipleUsersAction(Boolean isMultipleUsersSelected);
}
