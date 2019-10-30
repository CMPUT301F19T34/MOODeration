package com.example.mooderation;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.mooderation.activity.followrequest.DaggerFollowRequestsComponent;
import com.example.mooderation.activity.followrequest.FollowRequestsInjector;
import com.example.mooderation.activity.followrequest.FollowRequestsActivity;
import com.example.mooderation.activity.followrequest.FollowRequestsComponent;
import com.example.mooderation.backend.Database;
import com.example.mooderation.dependencyinjection.FirebaseModule;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class TestFollowRequests {
    private Solo solo;

    Database database = mock(Database.class);

    public class FirebaseTestModule extends FirebaseModule {
        @Override
        public Database provideDatabase() {
            return database;
        }
    }

    @Rule
    public ActivityTestRule<FollowRequestsActivity> rule = new ActivityTestRule<>(FollowRequestsActivity.class, true, true);

    @Before
    public void setUp() {
        FollowRequestsComponent component = DaggerFollowRequestsComponent.builder()
                .firebaseModule(new TestFollowRequests.FirebaseTestModule())
                .build();
        FollowRequestsInjector.set(component);
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
    }

    @After
    public void tearDown() {
        solo.finishOpenedActivities();
    }

    @Test
    public void testAcceptFollower() {
        solo.assertCurrentActivity("Wrong Activity", FollowRequestsActivity.class);
    }
}
