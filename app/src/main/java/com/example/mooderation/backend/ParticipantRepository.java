package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the main "users" table, and allows adding to and deleting from it.
 */
@Singleton
public class ParticipantRepository {
    private final FirebaseFirestore firestore;// = FirebaseFirestore.getInstance();
    MutableLiveData<List<Participant>> participants;

    @Inject
    public ParticipantRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
    }

    public Task<Void> register(Participant participant) {
        return firestore.collection("users").document(participant.getUid()).set(participant);
    }

    public LiveData<List<Participant>> getParticipants() {
        if (participants == null) {
            participants = new MutableLiveData<>();

            participantsPath().addSnapshotListener(((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    List<Participant> participantList = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        participantList.add(doc.toObject(Participant.class));
                    }
                    Collections.sort(participantList, (l, r) -> l.getUsername().compareTo(r.getUsername()));
                    participants.setValue(participantList);
                }
                else if (e != null) {
                    // TODO handle errors
                }
            }));
        }

        return participants;
    }

    // TODO this is only used for testing, is it really needed?
    public Task<Void> remove(Participant participant) {
        return participantsPath().document(participant.getUid()).get()
                // delete from other user's followers
                .continueWithTask(task -> firestore.collectionGroup("followers").whereEqualTo("uid", participant.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                // delete from other user's follow requests
                .continueWithTask(task -> firestore.collectionGroup("follow_requests").whereEqualTo("uid", participant.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                // delete the user's document
                .continueWithTask(task -> participantsPath().document(participant.getUid()).delete());
    }

    private Task<Void> deleteAllImmediateDocuments(QuerySnapshot ref) {
        List<Task<Void>> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : ref) {
            tasks.add(doc.getReference().delete());
        }
        return Tasks.whenAll(tasks);
    }

    private CollectionReference participantsPath() {
        return firestore.collection("users");
    }
}
