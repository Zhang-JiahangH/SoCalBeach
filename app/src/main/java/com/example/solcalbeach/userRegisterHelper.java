package com.example.solcalbeach;

import java.io.Serializable;

public class userRegisterHelper implements Serializable {
    public String name,email,password;

    public userRegisterHelper(){}

    public userRegisterHelper(String n, String e, String p){
        this.name=n; this.email=e; this.password = p;
    }

    public String getName() {return name;}

    public void setName(String name) {this.name = name;}

    public String getEmail() {return email;}

    public void setEmail(String email) {this.email = email;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}
}
