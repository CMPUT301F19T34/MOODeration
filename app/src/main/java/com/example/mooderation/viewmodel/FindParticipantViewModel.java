package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.LoginRepository;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class FindParticipantViewModel extends ViewModel {
    private ParticipantRepository participantRepository = new ParticipantRepository(FirebaseFirestore.getInstance()); // TODO temp
    private Participant currentParticipant = LoginRepository.getInstance().getParticipant();

    private MutableLiveData<String> searchQuery = new MutableLiveData<>();

    public LiveData<List<Participant>> getSearchResults() {
        return Transformations.switchMap(searchQuery, query -> Transformations.map(participantRepository.getParticipants(), participants -> {
            List<Participant> results = new ArrayList<>();
            for (Participant participant : participants) {
                // don't show current participant in results
                if (participant.getUid().equals(currentParticipant.getUid()))
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
