package com.example.mooderation;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.backend.MoodHistoryRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the AddMoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodHistoryRepository moodHistoryRepository;
    private ListenerRegistration listenerRegistration;

    private MutableLiveData<List<MoodEvent>> moodHistory;
    private Participant participant;

    /**
     * Default constructor
     * Used by ViewModelProviders
     * TODO implement dependency injection in the future
     */
    public MoodHistoryViewModel() {
        moodHistoryRepository = new MoodHistoryRepository();
        moodHistory = new MutableLiveData<>(new ArrayList<>());
    }

    public void setParticipant(Participant participant) {
        this.participant = participant;
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
        listenerRegistration = moodHistoryRepository.addListener(participant, moodEvents -> {
            Collections.sort(moodEvents, (l, r) ->
                    -l.getDate().compareTo(r.getDate()));
            moodHistory.setValue(moodEvents);
        });
    }

    /**
     * Add a new MoodEvent to the MoodHistory
     * @param moodEvent The MoodEvent to add
     */
    public void addMoodEvent(MoodEvent moodEvent) {
        moodHistoryRepository.add(participant, moodEvent);
    }

    /**
     * Edit existing MoodEvent in the MoodHistory
     * @param moodEvent The MoodEvent to add
     */
    public void editMoodEvent(MoodEvent moodEvent, MoodEvent newMoodEvent) {
        moodHistoryRepository.edit(participant, moodEvent, newMoodEvent);
    }

    public LiveData<List<MoodEvent>> getMoodHistory() {
        return moodHistory;
    }
}