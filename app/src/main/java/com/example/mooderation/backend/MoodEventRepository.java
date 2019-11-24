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

/**
 * Database abstraction for accessing a user's mood events.
 */
public class MoodEventRepository {
    private final FirebaseFirestore firestore;
    private final FirebaseUser user;

    private MutableLiveData<List<MoodEvent>> moodHistory;

    /**
     * Default constructor.
     * Creates dependencies internally.
     */
    public MoodEventRepository() {
        this.firestore = FirebaseFirestore.getInstance();
        this.user = FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Constructor used for testing.
     * Supports dependency injection.
     * @param user
     *      The user whose mood history will be tracked.
     * @param firestore
     *      The current database instance.
     */
    public MoodEventRepository(FirebaseUser user, FirebaseFirestore firestore) {
        this.firestore = firestore;
        this.user = user;
    }

    /**
     * Add a mood event to the user's mood history.
     * Will overwrite a mood event with the same Date.
     * @param moodEvent
     *      The mood event to be added.
     * @return
     *      Database operation Task, used for testing.
     */
    public Task<Void> add(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).set(moodEvent);
    }

    /**
     * Remove a mood event from the user's mood history
     * @param moodEvent
     *      The mood event to remove.
     * @return
     *      Database operation Task, used for testing.
     */
    public Task<Void> remove(MoodEvent moodEvent) {
        return getCollectionReference().document(moodEvent.getId()).delete();
    }

    /**
     * Get the current user's mood history.
     * Sorted in reverse chronological order.
     * @return
     *      LiveData tracking the user's mood history.
     */
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

    /**
     * Get a reference to the user's mood history.
     * @return
     *      A reference to the user's mood history collection in firestore.
     */
    private CollectionReference getCollectionReference() {
        return firestore.collection("users")
                .document(user.getUid())
                .collection("mood_history");
    }
}