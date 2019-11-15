package com.example.mooderation.backend;

import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the main "users" table, and allows adding to and deleting from it.
 */
public class ParticipantRepository implements Repository<Participant> {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    public Task<Void> add(Participant participant) {
        return db.collection("users").document(participant.getUid()).set(participant);
    }

    @Override
    public Task<Void> remove(Participant participant) {
        return participantPath(participant).collection("followers").get()
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> participantPath(participant).collection("follow_requests").get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> db.collectionGroup("followers").whereEqualTo("uid", participant.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> db.collectionGroup("follow_requests").whereEqualTo("uid", participant.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> participantPath(participant).collection("mood_history").get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> participantPath(participant).delete());
    }

    @Override
    public ListenerRegistration addListener(Listener<Participant> listener) {
        return participantsPath().addSnapshotListener((queryDocumentSnapshots, e) -> {
            List<Participant> participants = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                participants.add(doc.toObject(Participant.class));
            }
            listener.onDataChanged(participants);
        });
    }

    private Task<Void> deleteAllImmediateDocuments(QuerySnapshot ref) {
        List<Task<Void>> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : ref) {
            tasks.add(doc.getReference().delete());
        }
        return Tasks.whenAll(tasks);
    }

    private CollectionReference participantsPath() {
        return db.collection("users");
    }

    private DocumentReference participantPath(Participant participant) {
        return participantsPath().document(participant.getUid());
    }
}
