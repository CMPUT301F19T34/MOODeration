package com.example.mooderation.backend;

import android.util.Log;

import com.example.mooderation.FollowRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;


/**
 * A database that uses firebase as its backend
 */
public class FirebaseDatabase implements Database {
    private FirebaseFirestore db;
    private FirebaseUser user;

    private static String TAG = "FirebaseDatabase";

    /**
     * Initialize the database by connecting to the firebase and firebase auth singletons
     */
    public FirebaseDatabase() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public boolean authenticated() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    @Override
    public void acceptFollowRequest(FollowRequest request) {
        WriteBatch batch = db.batch();
        batch.delete(followRequestsPath().document(request.getUid()));
        batch.set(followersPath().document(request.getUid()), request);
        batch.commit().addOnFailureListener(e -> Log.e(TAG, "Failed to accept request"));
    }

    @Override
    public void denyFollowRequest(FollowRequest request) {
        followRequestsPath()
                .document(request.getUid())
                .delete()
                .addOnFailureListener(e -> Log.e(TAG, "Failed to deny request: ", e));
    }

    @Override
    public void addFollowRequestsListener(FollowRequestsListener listener) {
        followRequestsPath().addSnapshotListener(((queryDocumentSnapshots, e) -> {
            List<FollowRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : queryDocumentSnapshots) {
                requests.add(doc.toObject(FollowRequest.class));
            }
            listener.onDataChanged(requests);
            Log.e(TAG, "Notified listener");
        }));
    }

    private CollectionReference followRequestsPath() {
        return userPath().collection("follow_requests");
    }

    private DocumentReference userPath() {
        return db.collection("users").document(user.getUid());
    }

    private CollectionReference followersPath() {
        return userPath().collection("followers");
    }
}