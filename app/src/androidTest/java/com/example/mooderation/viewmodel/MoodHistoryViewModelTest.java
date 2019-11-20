package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.verify;

@RunWith(JUnit4.class)
public class MoodHistoryViewModelTest {
    @Mock
    MoodEventRepository moodEventRepository;
    @Mock
    MoodEvent moodEvent;

    private MoodHistoryViewModel moodHistoryViewModel;

    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);
        moodHistoryViewModel = new MoodHistoryViewModel(moodEventRepository);
    }

    @Test
    public void testAddMoodEvent() {
        moodHistoryViewModel.addMoodEvent(moodEvent);
        verify(moodEventRepository).add(moodEvent);
    }

    @Test
    public void testDeleteMoodEvent() {
        moodHistoryViewModel.removeMoodEvent(moodEvent);
        verify(moodEventRepository).remove(moodEvent);
    }
}
