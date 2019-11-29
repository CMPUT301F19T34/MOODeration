package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.FollowedMoodEventRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class FollowedMoodsViewModelTest {
    @Mock
    FollowedMoodEventRepository followedMoodEventRepository;

    private FollowedMoodsViewModel followedMoodsViewModel;

    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        followedMoodsViewModel = new FollowedMoodsViewModel(followedMoodEventRepository);
    }

    @Test
    public void testUsesRepositoryMoodEvents() {
        MutableLiveData<HashMap<Participant, MoodEvent>> moodsLiveData = new MutableLiveData<>();
        when(followedMoodEventRepository.getFollowedMoodEvents()).thenReturn(moodsLiveData);
        assertSame(moodsLiveData, followedMoodsViewModel.getMoodEvents());
        verify(followedMoodEventRepository).getFollowedMoodEvents();
    }
}
