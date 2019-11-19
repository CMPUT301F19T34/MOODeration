package com.example.mooderation.viewmodel;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnit4.class)
public class MoodHistoryViewModelTest {
    @Mock
    MoodRepository moodRepository;
    @Mock
    MoodEvent moodEvent;
    @Mock
    Observer<List<MoodEvent>> moodHistoryObserver;

    private MoodHistoryViewModel moodHistoryViewModel;
    private MutableLiveData<List<MoodEvent>> moodHistory = new MutableLiveData<>();

    // forces tasks to execute synchronously
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setUp() throws ExecutionException, InterruptedException {
        MockitoAnnotations.initMocks(this);

        moodHistoryViewModel = new MoodHistoryViewModel(moodRepository);
        when(moodRepository.getMoodHistory()).thenReturn(moodHistory);

        moodHistoryViewModel.getMoodHistory().observeForever(moodHistoryObserver);
    }

    // TODO replace with better tests

    @Test
    public void testAddMoodEvent() throws ExecutionException, InterruptedException {
        when(moodRepository.add(any())).then(invocation -> {
            moodHistory.setValue(new ArrayList<>());
            return null;
        });

        moodHistoryViewModel.addMoodEvent(moodEvent);
        verify(moodHistoryObserver).onChanged(new ArrayList<>());
    }

    @Test
    public void testDeleteMoodEvent() throws ExecutionException, InterruptedException {
        when(moodRepository.remove(any())).then(invocation -> {
            moodHistory.setValue(new ArrayList<>());
            return null;
        });

        moodHistoryViewModel.removeMoodEvent(moodEvent);
        verify(moodHistoryObserver).onChanged(new ArrayList<>());
    }
}
