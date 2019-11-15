package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.backend.MoodHistoryRepository;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ViewModel for sharing the MoodHistory data between the MoodHistoryFragment
 * and the AddMoodEventFragment
 */
public class MoodHistoryViewModel extends ViewModel {
    private MoodHistoryRepository moodHistoryRepository = new MoodHistoryRepository();
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private ListenerRegistration listenerRegistration;
    private MutableLiveData<List<MoodEvent>> moodHistory = new MutableLiveData<>(new ArrayList<>());


    public MoodHistoryViewModel() {
        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            updateListener();
        });
    }

    private void updateListener() {
        if (listenerRegistration != null) listenerRegistration.remove();
        if (user == null) return;

        listenerRegistration = moodHistoryRepository.addListener(user, moodEvents -> {
            Collections.sort(moodEvents, (l, r) -> -l.getDate().compareTo(r.getDate()));
            moodHistory.setValue(moodEvents);
        });
    }

    public Task<Void> addMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.add(user, moodEvent);
    }

    public Task<Void> removeMoodEvent(MoodEvent moodEvent) {
        return moodHistoryRepository.remove(user, moodEvent);
    }

    public LiveData<List<MoodEvent>> getMoodHistory() {
        updateListener();
        return moodHistory;
    }

}