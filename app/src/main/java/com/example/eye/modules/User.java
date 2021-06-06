package com.example.eye.modules;

import java.io.Serializable;
                //Serializable  must be implemented by the class whose object you want to persist.
public class User implements Serializable {
    public String firstName, lastName, email, token;

    public User(){
        //default constructor
    }
}
