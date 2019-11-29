package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodLatLng;
import com.example.mooderation.Participant;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.backend.FollowedMoodEventRepository;
import com.example.mooderation.backend.MoodEventRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FollowedMoodHistoryMapViewModelTest {
    @Mock
    FollowedMoodEventRepository followedMoodEventRepository;
    @Mock
    Observer<HashMap<Participant, MoodEvent>> observer;

    private MoodLatLng location = new MoodLatLng();
    private Participant participant1 = new Participant("uid1", "username1");
    private Participant participant2 = new Participant("uid2", "username2");

    private MutableLiveData<HashMap<Participant, MoodEvent>> moodEvents = new MutableLiveData<>();
    private FollowedMoodHistoryMapViewModel followedMoodHistoryMapViewModel;

    private MoodEvent moodEventWithLocation;
    private MoodEvent moodEventWithoutLocation;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(followedMoodEventRepository.getFollowedMoodEvents()).thenReturn(moodEvents);

        moodEventWithLocation = new MoodEvent(new Date(), EmotionalState.HAPPY, SocialSituation.ALONE, "reason", location);
        moodEventWithoutLocation = new MoodEvent(new Date(), EmotionalState.SAD, SocialSituation.ALONE, "reason");

        HashMap<Participant, MoodEvent> events = new HashMap<>();
        events.put(participant1, moodEventWithLocation);
        events.put(participant2, moodEventWithoutLocation);
        moodEvents.setValue(events);

        followedMoodHistoryMapViewModel = new FollowedMoodHistoryMapViewModel(followedMoodEventRepository);
        followedMoodHistoryMapViewModel.getFollowedMoodEventsWithLocation().observeForever(observer);
    }

    @Test
    public void testGetMoodHistoryWithLocation() {
        HashMap<Participant, MoodEvent> filtered = followedMoodHistoryMapViewModel.getFollowedMoodEventsWithLocation().getValue();
        assertTrue(filtered.containsKey(participant1));
        assertFalse(filtered.containsKey(participant2));
    }
}
