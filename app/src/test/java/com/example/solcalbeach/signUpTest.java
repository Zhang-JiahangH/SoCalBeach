package com.example.solcalbeach;


import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import android.widget.EditText;

import com.example.solcalbeach.util.userRegisterHelper;

public class signUpTest {
    String name;
    String email;
    String password;
    String rePassword;

    @Before
    public void init() {
        name = "Test User";
        email = "testUser@gmail.com";
        password = "12345678";
        rePassword = "12345678";
    }

    // if user entered a very short password
    // we'll not let them pass
    @Test
    public void testIsValidPwdShouldNotPass() {
        userRegisterHelper newUser = new userRegisterHelper(name, email, "123456");
        assertFalse(newUser.isValidPwd());
    }

    // if user entered nothing for email
    // we'll not let them pass
    @Test
    public void testisValidEmailShouldNotPass() {
        userRegisterHelper newUser = new userRegisterHelper(name, "", password);
        assertFalse(newUser.isValidEmail());
    }

    // if user entered not email format String for email
    // we'll not let them pass
    @Test
    public void testisValidNullEmailShouldNotPass() {
        userRegisterHelper newUser = new userRegisterHelper(name, "wrong", password);
        assertFalse(newUser.isValidEmail());
    }

    // if user entered passwords not consistent
    // we'll not let them pass
    @Test
    public void isValidConfirm() {
        userRegisterHelper newUser = new userRegisterHelper(name, email, password);
        assertTrue(newUser.isValidConfirm("123456"));
    }

    // otherwise
    // we'll let them pass
    @Test
    public void testSignUpShouldPass() {
        userRegisterHelper newUser = new userRegisterHelper(name, email, password);
        assertTrue(newUser.isValidPwd());
        assertFalse(newUser.isValidConfirm(rePassword));
    }
}
