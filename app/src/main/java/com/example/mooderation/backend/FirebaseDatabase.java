package com.example.mooderation.backend;

import android.util.Log;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Participant;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.HashMap;
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
        batch.delete(followRequestsPath().document(request.getParticipant().getUid()));
        batch.set(
                followersPath().document(request.getParticipant().getUid()),
                new HashMap<String, Object>()
        );
        batch.commit().addOnFailureListener(e -> Log.e(TAG, "Failed to accept request"));
    }

    @Override
    public void denyFollowRequest(FollowRequest request) {
        followRequestsPath()
                .document(request.getParticipant().getUid())
                .delete()
                .addOnFailureListener(e -> Log.e(TAG, "Failed to deny request: ", e));
    }

    @Override
    public void addFollowRequestsListener(FollowRequestsListener listener) {
        followRequestsPath().addSnapshotListener(((queryDocumentSnapshots, e) -> {
            followRequestsPath().get().addOnSuccessListener(queryDocumentSnapshots1 -> {
                List<FollowRequest> requests = new ArrayList<>();
                for (DocumentSnapshot doc : queryDocumentSnapshots1) {
                    Timestamp createTimestamp = doc.getTimestamp("createTimestamp");
                    String uid = doc.getString("uid");
                    String username = doc.getString("username");
                    requests.add(new FollowRequest(new Participant(uid, username), createTimestamp));
                }
                listener.onDataChanged(requests);
            }).addOnFailureListener(e1 -> Log.e(TAG, "Failed to get follow requests" + e1));
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