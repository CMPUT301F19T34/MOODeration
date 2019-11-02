package com.example.mooderation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements AddMoodEventFragment.OnFragmentInteractionListener {
    public static int REQUEST_LOGIN = 0;

    private MoodEventHistory moodEventHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Log.d("MainActivity", "addAuthStateListener launching LoginActivity");
                launchLoginActivity();
            } else {
                Log.d("MainActivity", "addAuthStateListener launching notifyLogin");
                notifyLogin();
                // Populate data here
            }
        });

        final Button logoutButton = findViewById(R.id.logout);
        logoutButton.setOnClickListener(v -> FirebaseAuth.getInstance().signOut());

        moodEventHistory = new MoodEventHistory();

        final FloatingActionButton addMoodEventButton = findViewById(R.id.add_mood_event_button);
        addMoodEventButton.setOnClickListener(v -> {
            AddMoodEventFragment fragment = new AddMoodEventFragment();
            fragment.show(getSupportFragmentManager(), "ADD_MOOD");
        });
    }

    @Override
    public void onPositiveClick(MoodEvent moodEvent) {
        moodEventHistory.addMoodEvent(moodEvent);
    }

    private void launchLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
        startActivityForResult(intent, REQUEST_LOGIN);
    }

    private void notifyLogin() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Toast.makeText(getApplicationContext(), "Signed in as " + user.getEmail(), Toast.LENGTH_LONG).show();
    }
}
