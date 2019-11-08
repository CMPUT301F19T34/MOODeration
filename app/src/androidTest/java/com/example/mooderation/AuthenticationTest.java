package com.example.mooderation;

import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;

import androidx.lifecycle.ViewModelProviders;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.auth.ui.SignUpActivity;
import com.example.mooderation.viewmodel.MoodHistoryViewModel;
import com.example.mooderation.viewmodel.ParticipantViewModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.robotium.solo.Solo;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;

public class AuthenticationTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<MainActivity> rule =
            new ActivityTestRule<>(MainActivity.class, true, false);


    @Before
    public void setUp() {
        FirebaseAuth.getInstance().signOut();
        rule.launchActivity(new Intent());
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @Test
    public void testLogin() {
        solo.waitForActivity(LoginActivity.class);
        login("login@email.com", "123456");
        solo.waitForActivity(MainActivity.class);
        assertTrue(solo.searchText("Logged in as login-username"));
        assertEquals("login@email.com", FirebaseAuth.getInstance().getCurrentUser().getEmail());

        MainActivity mainActivity = (MainActivity)solo.getCurrentActivity();
        ParticipantViewModel participantViewModel = ViewModelProviders.of(mainActivity).get(ParticipantViewModel.class);
        MoodHistoryViewModel moodHistoryViewModel = ViewModelProviders.of(mainActivity).get(MoodHistoryViewModel.class);

        assertEquals(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                     participantViewModel.getParticipant().getUid());
        assertEquals("login-username",
                     participantViewModel.getParticipant().getUsername());

        solo.clickOnImageButton(0);
        solo.clickOnText("Log out");
        solo.waitForActivity(LoginActivity.class);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
    }

    @Test
    public void testSignup() {
        solo.waitForActivity(LoginActivity.class);
        solo.clickOnButton(1);
        solo.waitForActivity(SignUpActivity.class);

        try {
            signup("signup-username", "signup@email.com", "123456", "123456");
            solo.waitForActivity(MainActivity.class);

            assertTrue(solo.searchText("Logged in as signup-username"));
            assertEquals("signup@email.com", FirebaseAuth.getInstance().getCurrentUser().getEmail());

            solo.clickOnImageButton(0);
            solo.clickOnText("Log out");
            solo.waitForActivity(LoginActivity.class);

            login("signup@email.com", "123456");
            solo.waitForActivity(MainActivity.class);
            assertTrue(solo.searchText("Logged in as signup-username"));
            assertEquals("signup@email.com", FirebaseAuth.getInstance().getCurrentUser().getEmail());

            MainActivity mainActivity = (MainActivity) solo.getCurrentActivity();
            ParticipantViewModel participantViewModel = ViewModelProviders.of(mainActivity).get(ParticipantViewModel.class);
            MoodHistoryViewModel moodHistoryViewModel = ViewModelProviders.of(mainActivity).get(MoodHistoryViewModel.class);

            assertEquals(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    participantViewModel.getParticipant().getUid());
            assertEquals("signup-username",
                    participantViewModel.getParticipant().getUsername());
        } finally {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                try {
                    Tasks.await(FirebaseFirestore.getInstance()
                            .collection("users")
                            .document(user.getUid())
                            .delete());
                } catch (ExecutionException ex) {
                    ex.printStackTrace();
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                user.delete();
            }
        }
        solo.waitForActivity(LoginActivity.class);
        assertNull(FirebaseAuth.getInstance().getCurrentUser());
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
