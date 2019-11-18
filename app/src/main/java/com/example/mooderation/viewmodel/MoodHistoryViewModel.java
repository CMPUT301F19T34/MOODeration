package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodHistoryRepository;
import com.google.android.gms.tasks.Task;

import java.util.List;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the MoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodHistoryRepository moodHistoryRepository = new MoodHistoryRepository();

    public MoodHistoryViewModel() {
        this.moodHistoryRepository = new MoodHistoryRepository();
    }

    // TODO implement real dependency injection
    public MoodHistoryViewModel(MoodHistoryRepository moodHistoryRepository) {
        this.moodHistoryRepository = moodHistoryRepository;
    }

    // TODO implement filter

    public LiveData<List<MoodEvent>> getMoodHistory() {
        return moodHistoryRepository.getMoodHistory();
    }

    public Task<Void> addMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.add(moodEvent);
    }

    public Task<Void> removeMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.remove(moodEvent);
    }
}