package com.example.solcalbeach;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class userSignIn extends AppCompatActivity {

    private Button login_but;
    private Button reg_but;
    private TextView error_msg;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_in);
        mAuth = FirebaseAuth.getInstance();
        error_msg = (TextView)findViewById(R.id.tv_signin_error);
        if(getIntent().getStringExtra("error")!=null){
            error_msg.setText(getIntent().getStringExtra("error"));
        }

        login_but = findViewById(R.id.btn_logIn);
        reg_but = findViewById(R.id.btn_to_sign_up);
        reg_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                to_register();
            }
        });

        login_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
                String pwd = ((EditText)findViewById(R.id.login_password)).getText().toString();
                // TODO: check if all the fields are typed in properly
                signInHelper(email, pwd);
            }
        });
    }


    @Override
    public void onStart(){
        super.onStart();
        FirebaseUser curUser = mAuth.getCurrentUser();
        if(curUser != null){
            // TODO: if is signed in, DO SOMETHING
        }
    }

    private void to_register(){
        Intent intent = new Intent(this, userSignUp.class);
        startActivity(intent);
    }

    private void to_main(){
        Intent intent = new Intent(this, homeActivity.class);
        startActivity(intent);
    }

    private void signInHelper(String email, String pwd){
        mAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    // if log in successfully, turn to main page.
                    FirebaseUser curUser = mAuth.getCurrentUser();
                    to_main();
                }else{
                    // if log in failed, reload this page and display the error message
                    getIntent().putExtra("error","Invalid email or password. Try again");
                    finish();
                    startActivity(getIntent());
                }
            }
        });
    }
}