package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
            startActivity(intent);
        } else {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
        }
        // TODO finish splash activity?
    }
}
