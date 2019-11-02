package com.example.mooderation;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.auth.ui.LoginActivity;
import com.robotium.solo.Condition;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.*;

public class LoginActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, false);

    @Before
    public void setUp() {
        Intent intent = new Intent();
        intent.putExtra(LoginActivity.AUTHENTICATOR, new MockAuthenticator());
        rule.launchActivity(intent);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void start() {
        solo.assertCurrentActivity("Wrong activity", LoginActivity.class);
    }

    @Test
    public void testLogin() {
        final Button loginButton = (Button)solo.getView(R.id.login);
        final EditText emailText = (EditText)solo.getView(R.id.email);
        final EditText passwordText = (EditText)solo.getView(R.id.password);
        final ProgressBar pbar = (ProgressBar)solo.getView(R.id.loading);

        assertEquals(View.GONE, pbar.getVisibility());

        solo.enterText(emailText, "registered-email@mail.com");
        solo.enterText(passwordText, "registered-password");

        solo.clickOnButton(loginButton.getText().toString());

        solo.waitForEmptyActivityStack(1);
        assertEquals(Activity.RESULT_OK, rule.getActivityResult().getResultCode());
    }

    @Test
    public void testEnableStatus() {
        final Button loginButton = (Button)solo.getView(R.id.login);
        final Button signupButton = (Button)solo.getView(R.id.signup);
        final EditText emailText = (EditText)solo.getView(R.id.email);
        final EditText passwordText = (EditText)solo.getView(R.id.password);

        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());

        solo.enterText(emailText, "invalid-email-format");
        solo.enterText(passwordText, "registered-password");
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());

        solo.enterText(emailText, "valid-email-format@email.com");
        solo.enterText(passwordText, "123");
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());

        solo.enterText(emailText, "valid-email-format@email.com");
        solo.enterText(passwordText, "valid-password");
        assertTrue(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
    }
}
