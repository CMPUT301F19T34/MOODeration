package com.example.mooderation;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.mooderation.auth.firebase.FirebaseAuthentication;
import com.example.mooderation.auth.firebase.FirebaseAuthenticator;
import com.example.mooderation.auth.ui.LoginActivity;
import com.example.mooderation.viewmodel.FindParticipantViewModel;
import com.example.mooderation.viewmodel.FollowRequestsViewModel;
import com.example.mooderation.viewmodel.ParticipantViewModel;
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
    public final int REQUEST_AUTHENTICATE = 0;

    private ParticipantViewModel participantViewModel;
    //private MoodHistoryViewModel moodHistoryViewModel;
    private FollowRequestsViewModel followRequestsViewModel;
    private FindParticipantViewModel findParticipantViewModel;
    private ParticipantProfileViewModel participantProfileViewModel;

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private NavigationView navigationView;

    private boolean paused = false;

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
        topLevelFragments.add(R.id.findParticipantFragment);
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
                    break;
            }

            return true;
        });

        // initialize view models
        participantViewModel = ViewModelProviders.of(this).get(ParticipantViewModel.class);
        //moodHistoryViewModel = ViewModelProviders.of(this).get(MoodHistoryViewModel.class);
        followRequestsViewModel = ViewModelProviders.of(this).get(FollowRequestsViewModel.class);
        findParticipantViewModel = ViewModelProviders.of(this).get(FindParticipantViewModel.class);
        participantProfileViewModel = ViewModelProviders.of(this).get(ParticipantProfileViewModel.class);

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            if (firebaseAuth.getCurrentUser() == null) {
                // go to login screen
                paused = true;
                Intent intent = new Intent(this, LoginActivity.class);
                intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
                startActivityForResult(intent, REQUEST_AUTHENTICATE);
            } else {
                if (!paused) {
                    FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getUid()).get().addOnSuccessListener(documentSnapshot -> {
                        Participant participant = new Participant(
                                FirebaseAuth.getInstance().getUid(),
                                (String) documentSnapshot.get("username"));
                        participantViewModel.setParticipant(participant);
                        //moodHistoryViewModel.setParticipant(participant);
                        followRequestsViewModel.setParticipant(participant);
                        findParticipantViewModel.setParticipant(participant);
                        participantProfileViewModel.setParticipant(participant);

                        // successfully logged in
                        String welcome = "Logged in as " + participant.getUsername();
                        Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_AUTHENTICATE) {
            paused = false;
            if (resultCode == RESULT_OK) {
                FirebaseAuthentication auth = (FirebaseAuthentication)data.getParcelableExtra(LoginActivity.AUTHENTICATION);
                // initialize the user object and store in ViewModel
                Participant participant = new Participant(
                        auth.getUser().getUid(),
                        auth.getUsername());
                participantViewModel.setParticipant(participant);
                //moodHistoryViewModel.setParticipant(participant);
                followRequestsViewModel.setParticipant(participant);
                findParticipantViewModel.setParticipant(participant);
                participantProfileViewModel.setParticipant(participant);

                // successfully logged in
                String welcome = "Logged in as " + participant.getUsername();
                Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}