package com.example.mooderation.backend;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class FollowRequestRepository implements OwnedRepository<Participant, FollowRequest> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public Task<Void> add(Participant participant, FollowRequest request) {
        return followRequestsPath(participant).document(request.getUid()).set(request);
    }

    @Override
    public Task<Void> remove(Participant participant, FollowRequest request) {
        return followRequestsPath(participant).document(request.getUid()).delete();
    }

    @Override
    public ListenerRegistration addListener(Participant participant, Listener<FollowRequest> listener) {
        return followRequestsPath(participant).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            List<FollowRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                requests.add(doc.toObject(FollowRequest.class));
            }
            listener.onDataChanged(requests);
        }));
    }

    private CollectionReference followRequestsPath(Participant participant) {
        return db.collection("users")
                .document(participant.getUid())
                .collection("follow_requests");
    }
}
