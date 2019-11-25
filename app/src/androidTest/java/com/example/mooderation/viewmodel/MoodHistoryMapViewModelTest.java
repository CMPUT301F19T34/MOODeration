package com.example.mooderation.viewmodel;

import android.location.Location;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodLatLng;
import com.example.mooderation.SocialSituation;
import com.example.mooderation.backend.MoodEventRepository;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MoodHistoryMapViewModelTest {
    @Mock
    MoodEventRepository moodEventRepository;
    @Mock
    MoodEvent moodEvent;
    @Mock
    Observer<List<MoodEvent>> observer;

    private MoodLatLng location = new MoodLatLng();

    private MutableLiveData<List<MoodEvent>> moodHistory = new MutableLiveData<>();
    private MoodHistoryMapViewModel moodHistoryMapViewModel;

    private MoodEvent moodEventWithLocation;
    private MoodEvent moodEventWithoutLocation;


    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        when(moodEventRepository.getMoodHistory()).thenReturn(moodHistory);

        moodEventWithLocation = new MoodEvent(new Date(), EmotionalState.HAPPY, SocialSituation.ALONE, "reason", location);
        moodEventWithoutLocation = new MoodEvent(new Date(), EmotionalState.SAD, SocialSituation.ALONE, "reason");

        List<MoodEvent> events = new ArrayList<>();
        events.add(moodEventWithLocation);
        events.add(moodEventWithoutLocation);
        moodHistory.setValue(events);

        moodHistoryMapViewModel = new MoodHistoryMapViewModel(moodEventRepository);
        moodHistoryMapViewModel.getMoodHistoryWithLocation().observeForever(observer);
    }

    @Test
    public void testGetMoodHistoryWithLocation() {
        List<MoodEvent> filtered = moodHistoryMapViewModel.getMoodHistoryWithLocation().getValue();
        assertTrue(filtered.contains(moodEventWithLocation));
        assertFalse(filtered.contains(moodEventWithoutLocation));
    }
}
