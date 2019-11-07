package com.example.mooderation;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.auth.base.IAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.auth.ui.SignUpActivity;
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
    public void testLogin() {
        login(MockAuthenticator.REGISTERED_EMAIL, MockAuthenticator.REGISTERED_PASSWORD);

        solo.waitForEmptyActivityStack(1000);
        assertEquals(Activity.RESULT_OK, rule.getActivityResult().getResultCode());
    }

    @Test
    public void testSignUp() {
        final Button signUpButton = (Button)solo.getView(R.id.signup);

        solo.clickOnButton(signUpButton.getText().toString());

        assertTrue(solo.waitForActivity(SignUpActivity.class));
        solo.sleep(500);
        SignUpActivity activity = (SignUpActivity) solo.getCurrentActivity();
        IAuthenticator auth = activity.getIntent().getParcelableExtra(SignUpActivity.AUTHENTICATOR);
        assertTrue(auth instanceof MockAuthenticator);

        solo.goBack();
        assertTrue(solo.waitForActivity(LoginActivity.class));
    }

    @Test
    public void testProgressBar() {
        final ProgressBar pbar = (ProgressBar)solo.getView(R.id.loading);

        assertEquals(View.GONE, pbar.getVisibility());

        login(MockAuthenticator.DELAY_EMAIL, MockAuthenticator.REGISTERED_PASSWORD);

        assertTrue(solo.waitForCondition(() -> pbar.getVisibility() == View.VISIBLE, 5000));
        assertTrue(solo.waitForCondition(() -> pbar.getVisibility() == View.GONE, 5000));

        login(MockAuthenticator.REGISTERED_EMAIL, MockAuthenticator.REGISTERED_PASSWORD);

        solo.waitForEmptyActivityStack(1000);
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

        solo.enterText(emailText, MockAuthenticator.INVALID_EMAIL);
        solo.enterText(passwordText, MockAuthenticator.REGISTERED_PASSWORD);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_email), emailText.getError());
        assertNull(passwordText.getError());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());

        solo.enterText(emailText, MockAuthenticator.REGISTERED_EMAIL);
        solo.enterText(passwordText, MockAuthenticator.INVALID_PASSWORD);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        assertNull(emailText.getError());
        assertEquals(solo.getString(R.string.auth_prompt_invalid_password), passwordText.getError());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());

        solo.enterText(emailText, MockAuthenticator.REGISTERED_EMAIL);
        solo.enterText(passwordText, MockAuthenticator.REGISTERED_PASSWORD);
        assertTrue(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
        assertNull(emailText.getError());
        assertNull(passwordText.getError());
        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);
        assertFalse(loginButton.isEnabled());
        assertTrue(signupButton.isEnabled());
    }

    @Test
    public void testWrongEmail() {
        login(MockAuthenticator.WRONG_EMAIL, MockAuthenticator.REGISTERED_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_login_invalid_email), 1, 1000));
    }

    @Test
    public void testWrongPassword() {
        login(MockAuthenticator.REGISTERED_EMAIL, MockAuthenticator.WRONG_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_login_invalid_password), 1, 1000));
    }

    @Test
    public void testGenericError() {
        login(MockAuthenticator.GENERIC_ERROR_EMAIL, MockAuthenticator.REGISTERED_PASSWORD);
        assertTrue(solo.waitForText(solo.getString(R.string.auth_error_login_generic), 1, 1000));
    }

    private void login(String email, String password) {
        final Button loginButton = (Button)solo.getView(R.id.login);
        final EditText emailText = (EditText)solo.getView(R.id.email);
        final EditText passwordText = (EditText)solo.getView(R.id.password);

        solo.clearEditText(emailText);
        solo.clearEditText(passwordText);

        solo.enterText(emailText, email);
        solo.enterText(passwordText, password);
        solo.clickOnButton(loginButton.getText().toString());
    }
}
