package com.example.mooderation.backend;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.mooderation.MoodEvent;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MoodHistoryRepository {
    private final FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    private MutableLiveData<List<MoodEvent>> moodHistory = new MutableLiveData<>();

    public Task<Void> add(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).set(moodEvent);
    }

    public Task<Void> remove(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).delete();
    }

    public LiveData<List<MoodEvent>> getMoodHistory() {
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

        return moodHistory;
    }

    private CollectionReference getCollectionReference() {
        return firestore.collection("users")
                .document(user.getUid())
                .collection("mood_history");
    }
}