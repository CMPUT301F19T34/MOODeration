package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Menu;
import android.view.MenuInflater;
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

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;

/**
 * The applications main activity.
 * Navigation components are being used keeping most of the UI
 * implementation in fragments.
 */
public class MainActivity extends AppCompatActivity {
    public final int REQUEST_AUTHENTICATE = 0;

    MoodHistoryViewModel moodHistoryViewModel;

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

        AppBarConfiguration appBarConfiguration =
                new AppBarConfiguration.Builder(navController.getGraph())
                        .setDrawerLayout(drawerLayout)
                        .build();

        NavigationView navView = findViewById(R.id.nav_view);
        NavigationUI.setupWithNavController(toolbar, navController, appBarConfiguration);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();

                switch (menuItem.getItemId()) {
                    case R.id.mood_history_drawer_item:
                        navController.navigate(R.id.moodHistoryFragment);
                        break;

                    // currently crashes doesn't pass argument to fragment
//                    case R.id.follow_request_drawer_item:
//                        navController.navigate(R.id.followRequestsFragment);
//                        break;

                    case R.id.log_out_drawer_item:
                        FirebaseAuth.getInstance().signOut();
                        break;
                }

                return true;
            }
        });

        moodHistoryViewModel = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
            } else {
                String welcome = "Logged in as " + firebaseAuth.getCurrentUser().getEmail();
                Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();

                moodHistoryViewModel.setParticipant(new Participant(
                        FirebaseAuth.getInstance().getUid(),
                        "user"
                ));
            }
        });
    }
}
