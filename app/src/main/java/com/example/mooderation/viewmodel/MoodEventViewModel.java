package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodEventRepository;

public class MoodEventViewModel extends ViewModel {
    private MoodEventRepository moodEventRepository;
    private MutableLiveData<MoodEvent> moodEventLiveData;

    public MoodEventViewModel() {
        moodEventRepository = new MoodEventRepository();
    }

    public MoodEventViewModel(MoodEventRepository moodEventRepository) {
        this.moodEventRepository = moodEventRepository;
    }

    public void setMoodEvent(MoodEvent moodEvent) {
        if (moodEventLiveData == null) {
            moodEventLiveData = new MutableLiveData<>();
        }
        moodEventLiveData.setValue(moodEvent);
    }

    public LiveData<MoodEvent> getMoodEvent() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("MoodEvent must be set!");
        }
        return moodEventLiveData;
    }

    public void saveChanges() {
        if (moodEventLiveData == null) {
            throw new IllegalStateException("MoodEvent must be set!");
        }
        moodEventRepository.add(moodEventLiveData.getValue());
    }
}
