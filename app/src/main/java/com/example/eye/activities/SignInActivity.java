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
import com.example.eye.activities.MainActivity;
import com.example.eye.activities.SignUpActivity;
import com.example.eye.utilities.Constants;
import com.example.eye.utilities.PreferenceManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

/*
            required imports for testing cloud database
import androidx.annotation.NonNull;
import android.widget.Toast;
import com.example.eye.activities.SignUpActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;*/

public class SignInActivity extends AppCompatActivity {


    private EditText  inputEmail, inputPassword;
    private MaterialButton buttonSignIn;
    private ProgressBar signInProgressBar;
    private PreferenceManager preferenceManager;

    @Override
    protected void
    onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        preferenceManager = new PreferenceManager(getApplicationContext());


        // to remain signed in after signed in one time.
        if(preferenceManager.getBoolean(Constants.KEY_IS_SIGNED_IN)) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }


        findViewById(R.id.textSignUp).setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), SignUpActivity.class)));

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        signInProgressBar = findViewById(R.id.signInProgressBar);


        buttonSignIn.setOnClickListener(view -> {
            if (inputEmail.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            } else if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail.getText().toString()).matches()) {
                Toast.makeText(SignInActivity.this, "Enter Valid Email", Toast.LENGTH_SHORT).show();
            } else if (inputPassword.getText().toString().trim().isEmpty()) {
                Toast.makeText(SignInActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                signIn();
            }
        });

    }
    private  void signIn(){
        buttonSignIn.setVisibility(View.INVISIBLE);
        signInProgressBar.setVisibility(View.VISIBLE);
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(Constants.KEY_COLLECTION_USERS)
                .whereEqualTo(Constants.KEY_EMAIL,inputEmail.getText().toString())      //Creates and returns a new Query with the additional filter that documents must contain the specified field and the value should be equal to the specified value.
                .whereEqualTo(Constants.KEY_PASSWORD, inputPassword.getText().toString())
                .get()
                .addOnCompleteListener((Task<QuerySnapshot> task) -> {
                    if(task.isSuccessful() && task.getResult() != null && task.getResult().getDocuments().size() > 0){          //A DocumentSnapshot contains data read from a document in your Cloud Firestore database. The data can be extracted with the getData() or get(String) methods. If the DocumentSnapshot points to a non-existing document, getData() and its corresponding methods will return null .
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putBoolean(Constants.KEY_IS_SIGNED_IN, true);
                        preferenceManager.putString(Constants.KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(Constants.KEY_FIRST_NAME, documentSnapshot.getString(Constants.KEY_FIRST_NAME));
                        preferenceManager.putString(Constants.KEY_LAST_NAME, documentSnapshot.getString(Constants.KEY_LAST_NAME));
                        preferenceManager.putString(Constants.KEY_EMAIL, documentSnapshot.getString(Constants.KEY_EMAIL));
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }else {
                        signInProgressBar.setVisibility(View.INVISIBLE);
                        buttonSignIn.setVisibility(View.VISIBLE);
                        Toast.makeText(SignInActivity.this, "Unable to sign in", Toast.LENGTH_SHORT).show();

                    }
                });
    }










        /*

        For testing the cloud database

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        HashMap<String, Object> user = new HashMap<>();
        user.put("first_name", "John");
        user.put("last_name", "Smith");
        user.put("email", "johnsmith@gmail.com");
        database.collection("users")
            .add(user)
                .addOnSuccessListener(documentReference -> Toast.makeText(SignInActivity.this, " User Inserted", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(SignInActivity.this, "Error adding User", Toast.LENGTH_SHORT).show());*/

}