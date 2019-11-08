package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.example.mooderation.backend.MoodHistoryRepository;
import com.google.android.gms.tasks.Task;
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
     * Default Constructor
     * TODO remove this and implement ViewModelFactory for this class
     */
    public MoodHistoryViewModel() {
        this.moodHistoryRepository = new MoodHistoryRepository();
        moodHistory = new MutableLiveData<>(new ArrayList<>());
    }

    /**
     * Constructor
     * @param moodHistoryRepository Database access object to associate with this view model
     */
    public MoodHistoryViewModel(MoodHistoryRepository moodHistoryRepository) {
        this.moodHistoryRepository = moodHistoryRepository;
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
    public Task<Void> addMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.add(participant, moodEvent);
    }

    public Task<Void> removeMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.remove(participant, moodEvent);
    }

    public LiveData<List<MoodEvent>> getMoodHistory() {
        return moodHistory;
    }
}