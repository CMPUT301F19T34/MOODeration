package com.example.mooderation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AddMoodEventFragment.OnFragmentInteractionListener {
    private MoodEventHistory moodEventHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moodEventHistory = new MoodEventHistory();

        final FloatingActionButton addMoodEventButton = findViewById(R.id.add_mood_event_button);
        addMoodEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddMoodEventFragment fragment = new AddMoodEventFragment();
                fragment.show(getSupportFragmentManager(), "ADD_MOOD");
            }
        });
    }

    @Override
    public void onPositiveClick(MoodEvent moodEvent) {
        moodEventHistory.addMoodEvent(moodEvent);
    }
}
