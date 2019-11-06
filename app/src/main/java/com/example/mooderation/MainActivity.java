package com.example.mooderation;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import com.google.firebase.auth.FirebaseAuth;

/**
 * The applications main activity.
 * Navigation components are being used keeping most of the UI
 * implementation in fragments.
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MoodHistoryViewModel model = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);
        model.setParticipant(new Participant(
                "kQ8j15T0habyG4Iq3Dx7mkRT5RM2",
                "username"
        ));
        Log.e("TAG", "Setting a viewmodel");
    }
}
