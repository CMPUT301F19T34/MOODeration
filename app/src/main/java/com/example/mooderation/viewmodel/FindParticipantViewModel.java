package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.LoginRepository;
import com.example.mooderation.backend.ParticipantRepository;

import java.util.ArrayList;
import java.util.List;

public class FindParticipantViewModel extends ViewModel {
    private ParticipantRepository participantRepository;
    private Participant currentUser;

    private MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public FindParticipantViewModel() {
        this.participantRepository = new ParticipantRepository();
        this.currentUser = LoginRepository.getInstance().getParticipant();
    }

    // TODO implement real dependency injection
    public FindParticipantViewModel(ParticipantRepository participantRepository, Participant user) {
        this.participantRepository = participantRepository;
        this.currentUser = user;
    }

    public LiveData<List<Participant>> getSearchResults() {
        return Transformations.switchMap(searchQuery, query -> Transformations.map(participantRepository.getParticipants(), participants -> {
            List<Participant> results = new ArrayList<>();
            for (Participant participant : participants) {
                // don't show current participant in results
                if (participant.getUid().equals(currentUser.getUid()))
                    continue;
                // only get participant that match searchQuery that match searchQuery
                if (participant.getUsername().toLowerCase().startsWith(query.toLowerCase()))
                    results.add(participant);
            }
            return results;
        }));
    }

    public void searchFor(String query) {
        searchQuery.setValue(query);
    }
}
