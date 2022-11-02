package com.example.solcalbeach;

import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class userLogIn extends AppCompatActivity {

    Button login_but;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_log_in);
        login_but = findViewById(R.id.btn_logIn);
        login_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(valid_login()){
                    // TODO: log in success, record the session and lead to main page
                }else{
                    // TODO: log in fail, back to current sign in page and show warning.
                }
            }
        });

    }

    private boolean valid_login(){
        String email = ((EditText)findViewById(R.id.login_email)).getText().toString();
        String pwd = ((EditText)findViewById(R.id.login_password)).getText().toString();
        //TODO: call real time database to check the validity of the input info

        return false;
    }
}