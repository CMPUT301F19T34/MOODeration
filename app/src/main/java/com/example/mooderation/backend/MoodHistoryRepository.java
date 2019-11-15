package com.example.mooderation.backend;

import com.example.mooderation.MoodEvent;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryRepository implements OwnedRepository<FirebaseUser, MoodEvent> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public Task<Void> add(FirebaseUser user, MoodEvent moodEvent) {
        String moodEventId = String.valueOf(moodEvent.getDate().getTime());
        return moodHistoryPath(user).document(moodEventId).set(moodEvent);
    }

    public Task<Void> remove(FirebaseUser user, MoodEvent moodEvent) {
        String moodEventId = String.valueOf(moodEvent.getDate().getTime());
        return moodHistoryPath(user).document(moodEventId).delete();
    }

    public ListenerRegistration addListener(FirebaseUser user, Listener<MoodEvent> listener) {
        return moodHistoryPath(user).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            List<MoodEvent> events = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                events.add(doc.toObject(MoodEvent.class));
            }
            listener.onDataChanged(events);
        }));
    }

    private CollectionReference moodHistoryPath(FirebaseUser user) {
        return db.collection("users")
                .document(user.getUid())
                .collection("mood_history");
    }
}