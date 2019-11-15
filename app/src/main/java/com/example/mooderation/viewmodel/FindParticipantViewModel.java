package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindParticipantViewModel extends ViewModel {
    private ParticipantRepository participantRepository;

    private ListenerRegistration listenerRegistration;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private Participant dummyParticipant; // TODO remove

    private MutableLiveData<List<Participant>> allParticipants;
    private LiveData<List<Participant>> searchResults;

    private String filter = "";

    public FindParticipantViewModel() {
        participantRepository = new ParticipantRepository();
        allParticipants = new MutableLiveData<>(new ArrayList<>());
        searchResults = new MutableLiveData<>(new ArrayList<>());

        FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            updateListener();
        });
//
        searchResults = Transformations.map(allParticipants, (List<Participant> input) -> {
            List<Participant> results = new ArrayList<>();
            for (Participant participant : input) {
                if (participant.getUid().equals(dummyParticipant.getUid())) continue;
                if (participant.getUsername().toLowerCase().startsWith(filter.toLowerCase())) {
                    results.add(participant);
                }
            }
            return results;
        });
    }

    private void updateListener() {
        if (listenerRegistration != null) listenerRegistration.remove();
        if (user == null) return;

        // TODO remove
        dummyParticipant = new Participant(user.getUid(), "dummy");

        listenerRegistration = participantRepository.addListener(participants -> {
            Collections.sort(participants, (l, r) -> l.getUsername().compareTo(r.getUsername()));
            allParticipants.setValue(participants);
        });
    }

    // TODO is this necessary?
    @Override
    protected void onCleared() {
        super.onCleared();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    public LiveData<List<Participant>> getSearchResults() {
        updateListener();
        return searchResults;
    }

    public void setFilter(String s) {
        filter = s;
        allParticipants.setValue(allParticipants.getValue());
    }
}
