package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FindParticipantViewModel extends ViewModel {
    private ParticipantRepository participantRepository;
    private MutableLiveData<List<Participant>> allParticipants;
    private LiveData<List<Participant>> searchResults;
    private ListenerRegistration registration;
    private Participant currentParticipant;
    private String filter = "";

    public FindParticipantViewModel() {
        participantRepository = new ParticipantRepository();
        allParticipants = new MutableLiveData<>(new ArrayList<>());
        searchResults = new MutableLiveData<>(new ArrayList<>());

        registration = participantRepository.addListener(participants -> {
            Collections.sort(participants, (l, r) ->
                    l.getUsername().compareTo(r.getUsername()));
            allParticipants.setValue(participants);
        });
        searchResults = Transformations.map(allParticipants, (List<Participant> input) -> {
            List<Participant> results = new ArrayList<>();
            for (Participant participant : input) {
                if (participant == currentParticipant) {
                    continue;
                }
                if (participant.getUsername().toLowerCase().startsWith(filter.toLowerCase())) {
                    results.add(participant);
                }
            }
            return results;
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (registration != null) {
            registration.remove();
        }
    }

    public LiveData<List<Participant>> getSearchResults() {
        return searchResults;
    }

    public void setParticipant(Participant participant) {
        currentParticipant = participant;
    }

    public void filter(String s) {
        filter = s;
        allParticipants.setValue(allParticipants.getValue());
    }
}
