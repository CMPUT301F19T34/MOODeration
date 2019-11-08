package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashSet;
import java.util.Set;

/**
 * The applications main activity.
 * Navigation components are being used keeping most of the UI
 * implementation in fragments.
 */
public class MainActivity extends AppCompatActivity {
    public final int REQUEST_AUTHENTICATE = 0;

    private ParticipantViewModel participantViewModel;
    private MoodHistoryViewModel moodHistoryViewModel;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // these are the top level fragments of the application
        // the top app bar will open the navigation drawer for these fragments
        // other fragments will show a back button
        Set<Integer> topLevelFragments = new HashSet<>();
        topLevelFragments.add(R.id.moodHistoryFragment);
        topLevelFragments.add(R.id.followRequestsFragment);
        // TODO add other top level fragments here

        // configures the top app bar
        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(topLevelFragments)
                        .setDrawerLayout(drawerLayout)
                        .build();

        // configure the navigation drawer
        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        // navigation listener
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    // navigate to mood history
                    case R.id.mood_history_drawer_item:
                        navController.navigate(R.id.moodHistoryFragment);
                        break;

                    // navigate to follow requests
                    case R.id.follow_request_drawer_item:
                        navController.navigate(R.id.followRequestsFragment);
                        break;

                    // log out of the app
                    case R.id.log_out_drawer_item:
                        FirebaseAuth.getInstance().signOut();
                        break;
                }

                return true;
            }
        });

        // initialize view models
        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);
        moodHistoryViewModel = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                // go to login screen
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
            } else {
                // initialize the user object and store in ViewModel
                Participant participant = new Participant(FirebaseAuth.getInstance().getUid(), "user");
                participantViewModel.setParticipant(participant);
                moodHistoryViewModel.setParticipant(participant);

                // successfully logged in
                // TODO fetch username for message for toast here?
                String welcome = "Logged in as " + firebaseAuth.getCurrentUser().getEmail();
                Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
            }
        });
    }
}