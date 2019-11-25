package com.example.mooderation.intent;

import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.R;
import com.example.mooderation.SplashActivity;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class AuthenticationTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SplashActivity> rule = new ActivityTestRule<>(SplashActivity.class);

    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testLogin() {
        //assertTrue(solo.waitForActivity("LoginActivity", 10000));
        login("test@email.com", "password");

        assertTrue(solo.searchText("Logged in as test-username"));
        assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
        assertEquals("test@email.com", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        logout();
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void testSignup() throws ExecutionException, InterruptedException {
        //solo.waitForActivity(LoginActivity.class, 1000);
        solo.clickOnButton(1);
        //solo.waitForActivity(SignUpActivity.class, 1000);

        try {
            signup("signup-username", "signup@email.com", "123456", "123456");
            //solo.waitForActivity(HomeActivity.class, 1000);

            assertTrue(solo.searchText("Logged in as signup-username"));
            assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
            assertEquals("signup@email.com", FirebaseAuth.getInstance().getCurrentUser().getEmail());
        }
        finally {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                Tasks.await(FirebaseFirestore.getInstance()
                        .collection("users")
                        .document(user.getUid())
                        .delete());
                logout();
                user.delete();
            }
        }

        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    private void login(String email, String password) {
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
    }

    private void signup(String username, String email, String password, String password2) {
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

        final Button signUpButton = (Button)solo.getView(R.id.signup);
        solo.clickOnButton(signUpButton.getText().toString());
    }

    private void logout() {
        // open navigation drawer and click logout button
        solo.clickOnImageButton(0);
        solo.clickOnText("Log out"); // TODO don't search by text

        // wait for logout activity
        solo.waitForActivity(LoginActivity.class);
    }
}
