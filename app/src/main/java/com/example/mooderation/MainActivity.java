package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.backend.LoginRepository;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashSet;
import java.util.Set;

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

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // these fragments will have the navigation drawer
        Set<Integer> topLevelFragments = new HashSet<>();
        topLevelFragments.add(R.id.moodHistoryFragment);
        topLevelFragments.add(R.id.followRequestsFragment);
        topLevelFragments.add(R.id.findParticipantFragment);
        // TODO register other top level fragments here

        // configures the top app bar
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(topLevelFragments)
                        .setDrawerLayout(drawerLayout)
                        .build();

        // configure the navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // navigation listener
        navView.setNavigationItemSelectedListener(menuItem -> {
            drawerLayout.closeDrawers();

            switch (menuItem.getItemId()) {
                // navigate to mood history
                case R.id.mood_history_drawer_item:
                    menuItem.setChecked(true);
                    navController.navigate(R.id.moodHistoryFragment);
                    break;

                // navigate to follow requests
                case R.id.follow_request_drawer_item:
                    menuItem.setChecked(true);
                    navController.navigate(R.id.followRequestsFragment);
                    break;

                // navigate to participant search
                case R.id.find_participant_drawer_item:
                    navController.navigate((R.id.findParticipantFragment));
                    break;

                // log out of the app
                case R.id.log_out_drawer_item:
                    FirebaseAuth.getInstance().signOut();
                    signOut();
                    break;
            }

            return true;
        });

        // TODO is this listener necessary?
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                signOut();
            }
        });


        FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(documentSnapshot -> {
            Participant participant = new Participant(
                    FirebaseAuth.getInstance().getUid(),
                    (String) documentSnapshot.get("username"));

            // TODO participant should be passed as an arg from LoginActivity
            LoginRepository.getInstance().setParticipant(participant);

            // successfully logged in
            String welcome = "Logged in as " + participant.getUsername();
            Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            signOut();
        }
    }

    // TODO find a better way to do this
    private void signOut() {
        // go to login screen
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());

        // restarts the main activity
        // refresh view models, etc
        finish();
        startActivity(intent);
    }
}