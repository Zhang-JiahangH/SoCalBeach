package com.example.solcalbeach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.solcalbeach.util.userRegisterHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class userSignUp extends AppCompatActivity {
    private Button reg_but;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up);
        // Initialize the auth instance
        mAuth = FirebaseAuth.getInstance();
        // Put the error message if have one
        TextView error = (TextView) findViewById(R.id.tv_signup_error);
        if(getIntent().getStringExtra("error")!=null){
            error.setText(getIntent().getStringExtra("error"));
        }

        reg_but = (Button) findViewById(R.id.btn_register);
        reg_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: check if all input fields are legal.

                //TODO: check if the using email is already in the database
                record_new_user();
            }
        });
    }

    public void finishRegister(){
        // When register is success, send intent to main page.
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    public void failedRegister(String error){
        Intent intent = new Intent(this, userSignUp.class);
        //add corresponding error message for failing register
        intent.putExtra("error",error);
        startActivity(intent);
    }


    private void record_new_user(){
        String name = ((EditText)findViewById(R.id.et_name)).getText().toString();
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_password)).getText().toString();
        userRegisterHelper newUser = new userRegisterHelper(name,email,password);
        // Checking if the using email has been used
        mAuth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
            @Override
            public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                if(!task.getResult().getSignInMethods().isEmpty()){
                    failedRegister("Email has been used, try sign in?");
                }else{
                    // Get reference of user maps in database
                    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
                    // checking if the email is already stored in the database
                    // TODO: doing so by trying to store the user in databse by UID linking with auth system.
                    DatabaseReference tempRef = usersRef.child(newUser.getName());
                    tempRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                // The registered email exist in the email
                                String error = "Email already exist, try to sign in";
                                failedRegister(error);
                            }else{
                                // Creating a new mAuth user instance
                                mAuth.createUserWithEmailAndPassword(newUser.getEmail(), newUser.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Put user information into database after the async task is done.
                                        final Map<String, Object> users = new HashMap<String, Object>();
                                        users.put(mAuth.getCurrentUser().getUid(),newUser);
                                        usersRef.updateChildren(users);
                                    }
                                });
                                finishRegister();
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }
            }
        });
    }
}
