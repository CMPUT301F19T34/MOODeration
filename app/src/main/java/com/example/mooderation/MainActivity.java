package com.example.mooderation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

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
    }
}
