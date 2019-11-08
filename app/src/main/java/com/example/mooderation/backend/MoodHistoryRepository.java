package com.example.mooderation.backend;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.MoodHistoryViewModel;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;
import java.util.List;

public class MoodHistoryRepository implements OwnedRepository<Participant, MoodEvent> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public Task<Void> add(Participant participant, MoodEvent moodEvent) {
        return moodHistoryPath(participant).document().set(moodEvent);
    }


    public Task<Void> edit(Participant participant, MoodEvent moodEvent, MoodEvent newMoodEvent) {
        return moodHistoryPath(participant).document().set(newMoodEvent);
    }

    @Override
    public Task<Void> remove(Participant participant, MoodEvent moodEvent) {
        throw new UnsupportedOperationException("Deleting mood events is not yet supported.");
    }

    @Override
    public ListenerRegistration addListener(Participant participant, Listener<MoodEvent> listener) {
        return moodHistoryPath(participant).addSnapshotListener(((queryDocumentSnapshots, e) -> {
            List<MoodEvent> events = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                events.add(doc.toObject(MoodEvent.class));
            }
            listener.onDataChanged(events);
        }));
    }

    private CollectionReference moodHistoryPath(Participant participant) {
        return db.collection("users")
                .document(participant.getUid())
                .collection("mood_history");
    }
}
