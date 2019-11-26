package com.example.mooderation.backend;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Database abstraction for accessing mood events of followers
 */
public class FollowedMoodEventRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseUser user;

    private MutableLiveData<HashMap<String, MoodEvent>> followedMoodEvents;

    private ArrayList<ListenerRegistration> listenerRegistrations;

    public FollowedMoodEventRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
        this.listenerRegistrations = new ArrayList<>();
    }

    public LiveData<HashMap<String, MoodEvent>> getFollowedMoodEvents() {
        if (followedMoodEvents == null) {
            followedMoodEvents = new MutableLiveData<>();

            following(user.getUid()).addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    List<Participant> followerList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        followerList.add(doc.toObject(Participant.class));
                    }
                    listenForFollowEvents(followerList);
                }
                else if (e != null) {
                    // TODO
                }
            });
        }

        return followedMoodEvents;
    }

    private void listenForFollowEvents(List<Participant> followerList) {
        for (ListenerRegistration listenerRegistration : listenerRegistrations)
            listenerRegistration.remove();
        listenerRegistrations.clear();

        for (Participant p : followerList) {
            listenerRegistrations.add(latestMoodEventOf(p.getUid())
                    .addSnapshotListener((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        HashMap<String, MoodEvent> m = followedMoodEvents.getValue();
                        if (m == null) m = new HashMap<>();
                        m.put(p.getUid(), doc.toObject(MoodEvent.class));
                        followedMoodEvents.setValue(m);
                    }
                }
                else if (e != null) {
                    // TODO handle error
                }
            }));
        }
    }

    /**
     * Get a reference to the users that a user is following
     * @param userId
     *      The user's ID.
     * @return
     *      A reference to the firestore collection for followers.
     */
    private CollectionReference following(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("following");
    }

    /**
     * Get a reference to the user's latest mood event
     * @return
     *      A reference to the user's latest mood event
     */
    private Query latestMoodEventOf(String userId) {
        return firestore.collection("users")
                .document(userId)
                .collection("mood_history")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(1);
    }
}
