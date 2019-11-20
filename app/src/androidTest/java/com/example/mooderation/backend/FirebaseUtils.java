package com.example.mooderation.backend;

import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.ExecutionException;

class FirebaseUtils {
    /**
     * Adds this user to the "users" collection in the database.
     * Blocks until complete.
     * @param firestore
     *      A firestore instance.
     * @param participant
     *      The participant to add.
     * @throws ExecutionException
     * @throws InterruptedException
     */
    static void createUser(FirebaseFirestore firestore, Participant participant) throws ExecutionException, InterruptedException {
        Tasks.await(firestore.collection("users").document(participant.getUid()).set(participant));
    }
}
