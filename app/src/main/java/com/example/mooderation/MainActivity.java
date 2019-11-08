package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * The applications main activity.
 * Navigation components are being used keeping most of the UI
 * implementation in fragments.
 */
public class MainActivity extends AppCompatActivity {
    public final int REQUEST_AUTHENTICATE = 0;

    MoodHistoryViewModel moodHistoryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moodHistoryViewModel = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
            } else {
                String welcome = "Logged in as " + firebaseAuth.getCurrentUser().getDisplayName();
                Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

                moodHistoryViewModel.setParticipant(new Participant(
                        firebaseAuth.getCurrentUser().getUid(),
                        firebaseAuth.getCurrentUser().getDisplayName()
                ));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.log_out) {
            FirebaseAuth.getInstance().signOut();
        }
        return super.onOptionsItemSelected(item);
    }
}
