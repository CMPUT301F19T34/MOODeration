package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.EmotionalState;
import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the MoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository = new MoodEventRepository();

    private MutableLiveData<EmotionalState> moodFilter = new MutableLiveData<>(null);
    private LiveData<List<MoodEvent>> moodHistory;

    public MoodHistoryViewModel() {
        this.moodEventRepository = new MoodEventRepository();
    }

    // TODO implement real dependency injection
    public MoodHistoryViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    // TODO implement filter

    public LiveData<List<MoodEvent>> getMoodHistory() {
        if (moodHistory == null) {
            moodHistory = Transformations.switchMap(moodFilter, filter ->
                Transformations.map(moodEventRepository.getMoodHistory(), moodEvents -> {
                    if (filter == null) {
                        return moodEvents;
                    }
                    List<MoodEvent> filteredMoodEvents = new ArrayList<>();
                    for (MoodEvent moodEvent : moodEvents) {
                        if (moodEvent.getEmotionalState() == filter) {
                            filteredMoodEvents.add(moodEvent);
                        }
                    }
                    return filteredMoodEvents;
                }));
        }

        return moodHistory;
    }

    public void setFilter(EmotionalState emotionalState) {
        moodFilter.setValue(emotionalState);
    }

    public void addMoodEvent(MoodEvent moodEvent) {
        moodEventRepository.add(moodEvent);
    }

    public void removeMoodEvent(MoodEvent moodEvent) {
        moodEventRepository.remove(moodEvent);
    }
}