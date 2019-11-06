package com.example.mooderation.backend;

import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the followers of each participant.
 */
public class FollowerRepository implements OwnedRepository<Participant, Follower> {
    private FirebaseFirestore db;

    public FollowerRepository() {
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public Task<Void> add(Participant participant, Follower follower) {
        return followersPath(participant).document(follower.getUid()).set(follower);
    }

    @Override
    public Task<Void> remove(Participant participant, Follower follower) {
        return followersPath(participant).document(follower.getUid()).delete();
    }

    @Override
    public ListenerRegistration addListener(Participant participant, Listener<Follower> listener) {
        return followersPath(participant).addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<Follower> followers = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                followers.add(doc.toObject(Follower.class));
            }
            listener.onDataChanged(followers);
        });
    }

    private CollectionReference followersPath(Participant participant) {
        return db.collection("users").document(participant.getUid()).collection("followers");
    }
}
