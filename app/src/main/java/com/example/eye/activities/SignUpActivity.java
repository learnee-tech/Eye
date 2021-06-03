package com.example.eye.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eye.R;
import com.example.eye.utilities.Constants;
import com.example.eye.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;

public class SignUpActivity extends AppCompatActivity {
        private EditText inputFirstName, inputLastName, inputEmail, inputPassword, inputConfirmPassword;
        private MaterialButton buttonSignUP;
        private ProgressBar signUpProgressBar;
        private PreferenceManager preferenceManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        preferenceManager =  new PreferenceManager((getApplicationContext()));

        findViewById(R.id.imageBack).setOnClickListener(v -> onBackPressed());

        inputFirstName = findViewById(R.id.inputFirstName);
        inputLastName = findViewById(R.id.inputLastName);
        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        buttonSignUP = findViewById(R.id.buttonSignUp);
        signUpProgressBar = findViewById(R.id.signUpProgressBar);


        buttonSignUP.setOnClickListener(view -> {
            if (inputFirstName.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
            } else if (inputLastName.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
            } else if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignUpActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else if (inputConfirmPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Confirm Your Password", Toast.LENGTH_SHORT).show();
            } else if (!inputPassword.getText().toString().equals(inputConfirmPassword.getText().toString())) {
                Toast.makeText(SignUpActivity.this, "Password and Confirm Password must be same", Toast.LENGTH_SHORT).show();
            } else {
                signUp();
            }
        });

    }
             private void signUp() {
                 buttonSignUP.setVisibility(View.INVISIBLE);
                 signUpProgressBar.setVisibility(View.VISIBLE);
                 FirebaseFirestore database = FirebaseFirestore.getInstance();
                 HashMap<String, Object> user = new HashMap<>();                  //  A HashMap basically designates unique keys to corresponding values that can be retrieved at any given point.
                 user.put(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                 user.put(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                 user.put(Constants.KEY_EMAIL, inputEmail.getText().toString());
                 user.put(Constants.KEY_PASSWORD, inputPassword.getText().toString());

                 database.collection(Constants.KEY_COLLECTION_USERS)
                         .add(user)
                         .addOnSuccessListener(documentReference -> {
                             preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                             preferenceManager.putString(Constants.KEY_USER_ID, documentReference.getId());
                             preferenceManager.putString(Constants.KEY_FIRST_NAME, inputFirstName.getText().toString());
                             preferenceManager.putString(Constants.KEY_LAST_NAME, inputLastName.getText().toString());
                             preferenceManager.putString(Constants.KEY_EMAIL, inputEmail.getText().toString());
                             Intent intent = new Intent(getApplicationContext(), MainActivity.class);                   //f we know class name then we can navigate the app from One Activity to another activity using Intent.
                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                             startActivity(intent);
                         })
                         .addOnFailureListener(e -> {
                             signUpProgressBar.setVisibility(View.INVISIBLE);
                             buttonSignUP.setVisibility(View.VISIBLE);
                             Toast.makeText(SignUpActivity.this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                         });

             }
}