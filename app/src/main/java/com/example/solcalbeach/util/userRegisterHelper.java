package com.example.solcalbeach.util;

import android.text.TextUtils;
import android.util.Patterns;

import androidx.core.util.PatternsCompat;

import java.io.Serializable;

public class userRegisterHelper implements Serializable {
    public String name,email,displayName,password;

    public userRegisterHelper(){}

    public userRegisterHelper(String n, String e, String p){
        this.email=e;
        this.name=n;
        this.password = p;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}

    /*
        input is user's new pwd
        check if the pwd is strong enough.
     */
    public boolean isValidPwd(){
        if(this.password.length() >= 8) {
            return true;
        }
        else return false;
    }

    /*
     *  input is the user's input email address
     *  return whether the user's input is a valid email address.
     */
    public boolean isValidEmail(){
        return (!TextUtils.isEmpty(email) && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches());
    }

    /*
    * check re-typed password
    */
    public boolean isValidConfirm(String rePassword) {
        return !rePassword.equals(password);
    }
}
