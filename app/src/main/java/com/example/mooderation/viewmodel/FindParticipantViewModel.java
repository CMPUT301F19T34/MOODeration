package com.example.mooderation.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.Participant;
import com.example.mooderation.backend.ParticipantRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * ViewModel for FindParticipantFragment
 */
public class FindParticipantViewModel extends ViewModel {
    private ParticipantRepository participantRepository;
    private FirebaseUser user;

    private LiveData<List<Participant>> searchResults;
    private MutableLiveData<String> searchQuery = new MutableLiveData<>("");

    /**
     * Default constructor. Creates dependencies internally.
     */
    public FindParticipantViewModel() {
        this.participantRepository = new ParticipantRepository();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Constructor used for testing. Supports dependency injection.
     * @param participantRepository
     *      The repository to retrieve data from.
     * @param user
     *      The firebase user currently signed in.
     */
    public FindParticipantViewModel(ParticipantRepository participantRepository, FirebaseUser user) {
        this.participantRepository = participantRepository;
        this.user = user;
    }

    /**
     * Get the search results. Filters out participants that don't match the search query.
     * @return
     *      LiveData tracking participants that match the search result.
     */
    public LiveData<List<Participant>> getSearchResults() {
        if (searchResults == null) {
            /**
             * Transformation.switchMap updates the live data when the data or the searchQuery
             * changes. Each update of searchQuery will call Transformation.map again.
             * Transformation.map tracks the participants LiveData and applies the filter function.
             */
            searchResults = Transformations.switchMap(searchQuery, query -> Transformations.map(participantRepository.getParticipants(), participants -> {
                List<Participant> results = new ArrayList<>();
                for (Participant participant : participants) {
                    // don't show current participant in results
                    if (participant.getUid().equals(user.getUid()))
                        continue;
                    // only get participant that match searchQuery that match searchQuery
                    if (participant.getUsername().toLowerCase().startsWith(query.toLowerCase()))
                        results.add(participant);
                }
                return results;
            }));
        }

        return searchResults;
    }

    /**
     * Update the search query
     * @param query
     *      The new search query.
     */
    public void searchFor(String query) {
        searchQuery.setValue(query);
    }
}
