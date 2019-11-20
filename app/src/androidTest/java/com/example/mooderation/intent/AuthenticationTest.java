package com.example.mooderation.intent;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.HomeActivity;
import com.example.mooderation.R;
import com.example.mooderation.SplashActivity;
import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.auth.ui.SignUpActivity;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.ExecutionException;

import static com.example.mooderation.intent.AuthUtils.getTestEmail;
import static com.example.mooderation.intent.AuthUtils.getTestUsername;
import static com.example.mooderation.intent.AuthUtils.login;
import static com.example.mooderation.intent.AuthUtils.logout;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class AuthenticationTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<SplashActivity> rule =
            new ActivityTestRule<>(SplashActivity.class, true, false);


    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        rule.launchActivity(new Intent());
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testLogin() {
        assertTrue(solo.waitForActivity(SplashActivity.class, 1000));
        assertTrue(solo.waitForActivity(LoginActivity.class, 1000));
        login(solo);

        assertTrue(solo.searchText("Logged in as " + getTestUsername()));
        assertNotNull(FirebaseAuth.getInstance().getCurrentUser());
        assertEquals(getTestEmail(), FirebaseAuth.getInstance().getCurrentUser().getEmail());

        logout(solo);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void testSignup() throws ExecutionException, InterruptedException {
        solo.waitForActivity(LoginActivity.class);
        solo.clickOnButton(1);
        solo.waitForActivity(SignUpActivity.class);

        try {
            signup("signup-username", "signup@email.com", "123456", "123456");
            solo.waitForActivity(HomeActivity.class);

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
                user.delete();
            }
        }

        logout(solo);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
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
}
