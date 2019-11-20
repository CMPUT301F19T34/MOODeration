package com.example.mooderation.auth;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.R;
import com.example.mooderation.auth.ui.SignUpActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class SignUpActivityTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SignUpActivity> rule =
            new ActivityTestRule<>(SignUpActivity.class, true, false);

    @Before
    public void setUp(){
        Intent intent = new Intent();
        intent.putExtra(SignUpActivity.AUTHENTICATOR, new MockAuthenticator());
        rule.launchActivity(intent);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testSignUp() {
        signup(MockAuthenticator.NEW_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD);

        solo.waitForEmptyActivityStack(1000);
        assertEquals(Activity.RESULT_OK, rule.getActivityResult().getResultCode());
    }

    // TODO sometimes fails
    @Test
    public void testProgressBar() {
        final ProgressBar pbar = (ProgressBar)solo.getView(R.id.loading);

        assertEquals(View.GONE, pbar.getVisibility());

        signup(MockAuthenticator.NEW_USERNAME, MockAuthenticator.DELAY_EMAIL, MockAuthenticator.NEW_PASSWORD);

        assertTrue(solo.waitForCondition(() -> pbar.getVisibility() == View.VISIBLE, 5000));
        assertTrue(solo.waitForCondition(() -> pbar.getVisibility() == View.GONE, 5000));

        signup(MockAuthenticator.NEW_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD);

        solo.waitForEmptyActivityStack(1000);
        assertEquals(Activity.RESULT_OK, rule.getActivityResult().getResultCode());
    }

    @Test
    public void testEnableStatus() {
        final Button signUpButton = (Button)solo.getView(R.id.signup);
        final EditText usernameText = (EditText)solo.getView(R.id.username);
        final EditText emailText = (EditText)solo.getView(R.id.email);
        final EditText passwordText = (EditText)solo.getView(R.id.password);
        final EditText password2Text = (EditText)solo.getView(R.id.password2);

        assertFalse(signUpButton.isEnabled());

        form_fill(MockAuthenticator.INVALID_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD, MockAuthenticator.NEW_PASSWORD);
        assertFalse(signUpButton.isEnabled());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_username), usernameText.getError());
        assertNull(emailText.getError()); assertNull(passwordText.getError()); assertNull(password2Text.getError());

        form_fill(MockAuthenticator.NEW_USERNAME, MockAuthenticator.INVALID_EMAIL, MockAuthenticator.NEW_PASSWORD, MockAuthenticator.NEW_PASSWORD);
        assertFalse(signUpButton.isEnabled());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_email), emailText.getError());
        assertNull(usernameText.getError()); assertNull(passwordText.getError()); assertNull(password2Text.getError());

        form_fill(MockAuthenticator.NEW_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.INVALID_PASSWORD, MockAuthenticator.INVALID_PASSWORD);
        assertFalse(signUpButton.isEnabled());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_password), passwordText.getError());
        assertNull(usernameText.getError()); assertNull(emailText.getError()); assertNull(password2Text.getError());

        form_fill(MockAuthenticator.NEW_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD, MockAuthenticator.INVALID_PASSWORD);
        assertFalse(signUpButton.isEnabled());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_password2), password2Text.getError());
        assertNull(usernameText.getError()); assertNull(emailText.getError()); assertNull(passwordText.getError());

        form_fill(MockAuthenticator.NEW_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD, MockAuthenticator.NEW_PASSWORD);
        assertTrue(signUpButton.isEnabled());
        assertNull(usernameText.getError()); assertNull(emailText.getError()); assertNull(passwordText.getError()); assertNull(password2Text.getError());
    }

    @Test
    public void testUsernameCollision() {
        signup(MockAuthenticator.REGISTERED_USERNAME, MockAuthenticator.NEW_EMAIL, MockAuthenticator.NEW_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_signup_username_collision), 1, 1000));
    }

    @Test
    public void testEmailCollision() {
        signup(MockAuthenticator.NEW_USERNAME, MockAuthenticator.REGISTERED_EMAIL, MockAuthenticator.NEW_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_signup_email_collision), 1, 1000));
    }

    @Test
    public void testGenericError() {
        signup(MockAuthenticator.NEW_USERNAME, MockAuthenticator.GENERIC_ERROR_EMAIL, MockAuthenticator.NEW_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_signup_generic), 1, 1000));
    }

    private void form_fill(String username, String email, String password, String password2) {
        final EditText usernameText = (EditText)solo.getView(R.id.username);
        final EditText emailText = (EditText)solo.getView(R.id.email);
        final EditText passwordText = (EditText)solo.getView(R.id.password);
        final EditText password2Text = (EditText)solo.getView(R.id.password2);

        solo.clearEditText(usernameText);
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        solo.clearEditText(password2Text);

        solo.enterText(usernameText, username);
        solo.enterText(emailText, email);
        solo.enterText(passwordText, password);
        solo.enterText(password2Text, password2);
    }

    private void signup(String username, String email, String password) {
        form_fill(username, email, password, password);

        final Button signUpButton = (Button)solo.getView(R.id.signup);
        solo.clickOnButton(signUpButton.getText().toString());
    }
}
