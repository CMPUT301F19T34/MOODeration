package com.example.mooderation.intent;

import android.widget.Button;
import android.widget.EditText;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.example.mooderation.auth.ui.LoginActivity;
import com.robotium.solo.Solo;

/**
 * Utilities for automatically logging into the application during intent tests.
 * Assumes a user with the information below always exists in the authentication
 * and has an entry in the users collection.
 */
class AuthUtils {
    private static String testEmail = "test@email.com";
    private static String testUsername  = "test-username";
    private static String testPassword = "password";

    public static String getTestEmail() {
        return testEmail;
    }

    public static String getTestUsername() {
        return testUsername;
    }

    public static String getTestPassword() {
        return testPassword;
    }

    public static void login(Solo solo) {
        login(solo, testEmail, testPassword);
    }

    public static void login(Solo solo, String email, String password) {
        solo.assertCurrentActivity("Cannot login from this activity", LoginActivity.class);

        // enter email
        final EditText emailText = (EditText)solo.getView(R.id.email);
        solo.clearEditText(emailText);
        solo.enterText(emailText, email);

        // enter password
        final EditText passwordText = (EditText)solo.getView(R.id.password);
        solo.clearEditText(passwordText);
        solo.enterText(passwordText, password);

        // press login button
        final Button loginButton = (Button)solo.getView(R.id.login);
        solo.clickOnButton(loginButton.getText().toString());

        // wait for home activity
        solo.waitForActivity(HomeActivity.class);
    }

    public static void logout(Solo solo) {
        // open navigation drawer and click logout button
        solo.clickOnImageButton(0); // TODO refactor into own method
        solo.clickOnText("Log out"); // TODO don't search by text

        // wait for logout activity
        solo.waitForActivity(LoginActivity.class);
    }
}
