package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.MoodEvent;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoodHistoryRepository {
    private final FirebaseFirestore firestore;
    private final Participant currentUser;

    private MutableLiveData<List<MoodEvent>> moodHistory;

    public MoodHistoryRepository() {
        this.firestore = FirebaseFirestore.getInstance();

        // TODO temporary fix -- the user will not be retrieved from the database by the time
        // this repository is initialized. A proper solution would be to get the user in the
        // login activity. The problem occurs here since mood history is the default fragment.
        this.currentUser = new Participant(FirebaseAuth.getInstance().getUid(), "username");
        //this.currentUser = LoginRepository.getInstance().getParticipant();
    }

    // TODO implement real dependency injection
    public MoodHistoryRepository(Participant user, FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.currentUser = user;
    }

    public Task<Void> add(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).set(moodEvent);
    }

    public Task<Void> remove(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).delete();
    }

    public LiveData<List<MoodEvent>> getMoodHistory() {
        if (moodHistory == null) {
            moodHistory = new MutableLiveData<>();

            getCollectionReference().addSnapshotListener(((queryDocumentSnapshots, e) -> {
                if (queryDocumentSnapshots != null) {
                    List<MoodEvent> moodEvents = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        moodEvents.add(doc.toObject(MoodEvent.class));
                    }
                    Collections.sort(moodEvents, (l, r) -> -l.getDate().compareTo(r.getDate()));
                    moodHistory.setValue(moodEvents);
                }
                else if (e != null) {
                    // TODO exception handle
                }
            }));
        }

        return moodHistory;
    }

    private CollectionReference getCollectionReference() {
        return firestore.collection("users")
                .document(currentUser.getUid())
                .collection("mood_history");
    }
}