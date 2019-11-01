package com.example.mooderation.backend;

import android.util.Log;

import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * A database that uses firebase as its backend
 */
public class Database {
    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseFunctions functions;

    private static String TAG = "Database";

    /**
     * Initialize the database by connecting to the firebase and firebase auth singletons
     */
    public Database() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        functions = FirebaseFunctions.getInstance();
    }

    /**
     * Checks whether the user is logged in
     * @return true if and only if any user is currently authenticated
     */
    public boolean authenticated() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    /**
     * As the current user, accept a follow request
     * @param request The follow request to accept
     */
    public Task<Void> acceptFollowRequest(FollowRequest request) {
        return deleteFollowRequest(request)
                .continueWithTask(task -> addFollower(new Follower(request.getUid(), request.getUsername())));
    }

    /**
     * As the current user, deny a follow request
     * @param request The follow request to deny
     */
    public Task<Void> denyFollowRequest(FollowRequest request) {
        return deleteFollowRequest(request);
    }

    /**
     * Listen for changes to the current user's list of follow requests. The listener is called
     * once immediately and thereafter whenever the current user's list of follow requests changes.
     * @param listener Listener to listen for changes
     */
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

    public Task<Void> addFollowRequest(FollowRequest request) {
        return followRequestsPath().document(request.getUid()).set(request);
    }

    public Task<Void> deleteFollowRequest(FollowRequest request) {
        return followRequestsPath().document(request.getUid()).delete();
    }

    public Task<List<FollowRequest>> getFollowRequests() {
        return followRequestsPath().get().continueWith(task -> {
            List<FollowRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult()) {
                requests.add(doc.toObject(FollowRequest.class));
            }
            return requests;
        });
    }

    public Task<Void> addFollower(Follower follower) {
        return followersPath().document(follower.getUid()).set(follower);
    }

    public Task<Void> deleteFollower(Follower follower) {
        return followersPath().document(follower.getUid()).delete();
    }

    public Task<List<Follower>> getFollowers() {
        return followersPath().get().continueWith(task -> {
            List<Follower> followers = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult()) {
                followers.add(doc.toObject(Follower.class));
            }
            return followers;
        });
    }

    public Task<Void> deleteAllUsers() {
        return deleteCollection("users", "followers", "follow_requests");
    }

    public Task<Void> addUser(String uid, String username) {
        Map<String, Object> data = new HashMap<>();
        data.put("username", username);
        return db.collection("users").document(uid).set(data);
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

    private void deleteCollectionImpl(String path, String[] subcollections) throws ExecutionException, InterruptedException, TimeoutException {
        Log.e("Database", "Deleting path " + path + " impl");
        QuerySnapshot documents = Tasks.await(db.collection(path).get(), 1, TimeUnit.valueOf("second"));
        Log.e("Database", "Path " + path + " has " + documents.size() + " documents");
        for (DocumentSnapshot doc : documents) {
            for (String subcollection : subcollections) {
                Log.e("Database", "Deleting path " + doc.getReference().collection(subcollection).getPath());
                deleteCollectionImpl(doc.getReference().collection(subcollection).getPath(), subcollections);
            }
            Log.e("Database", "Deleting document " + doc.getReference().getPath());
            Tasks.await(doc.getReference().delete());
        }
    }

    private Task<Void> deleteCollection(String path, String... subcollections) {
        Log.e("Database", "Deleting path " + path);
        return Tasks.call(() -> {
            deleteCollectionImpl(path, subcollections);
            return null;
        });
    }

    /**
     * Listener to listen for changes to the current user's follow request list
     */
    public interface FollowRequestsListener {
        /**
         * Notify the listener that the follower list data has changed. This function is called
         * once immediately when the listener is added and thereafter whenever the list changes.
         * @param requests The updated list of follow requests from the database, in arbitrary order
         */
        void onDataChanged(List<FollowRequest> requests);
    }
}