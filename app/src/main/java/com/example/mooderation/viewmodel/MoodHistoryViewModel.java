package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodRepository;

import java.util.List;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the MoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodRepository moodRepository = new MoodRepository();

    public MoodHistoryViewModel() {
        this.moodRepository = new MoodRepository();
    }

    // TODO implement real dependency injection
    public MoodHistoryViewModel(MoodRepository moodRepository) {
        this.moodRepository = moodRepository;
    }

    // TODO implement filter

    public LiveData<List<MoodEvent>> getMoodHistory() {
        return moodRepository.getMoodHistory();
    }

    public void addMoodEvent(MoodEvent moodEvent) {
        moodRepository.add(moodEvent);
    }

    public void removeMoodEvent(MoodEvent moodEvent) {
        moodRepository.remove(moodEvent);
    }
}