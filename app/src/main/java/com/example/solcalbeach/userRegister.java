package com.example.solcalbeach;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class userRegister extends AppCompatActivity {
    Button reg_but;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_sign_up);
        reg_but = (Button) findViewById(R.id.btn_register);
        reg_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                record_new_user();
                finishRegister();
            }
        });
    }

    public void finishRegister(){
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }

    private void record_new_user(){
        String name = ((EditText)findViewById(R.id.et_name)).getText().toString();
        String email = ((EditText)findViewById(R.id.et_email)).getText().toString();
        String password = ((EditText)findViewById(R.id.et_password)).getText().toString();
        userRegisterHelper newUser = new userRegisterHelper(name,email,password);
        final Map<String, Object> users = new HashMap<String, Object>();
        users.put(newUser.name,newUser);

        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference().child("users");
        usersRef.updateChildren(users);

    }
}
