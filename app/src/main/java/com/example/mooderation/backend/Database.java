package com.example.mooderation.backend;


import com.example.mooderation.FollowRequest;
import com.example.mooderation.Follower;
import com.example.mooderation.Participant;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A database that uses firebase as its backend
 */
public class Database {
    private FirebaseFirestore db;
    private FirebaseUser user;

    /**
     * Initialize the database by connecting to the firebase and firebase auth singletons
     */
    public Database() {
        db = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
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
     * @return A task which completes when the request has been accepted
     */
    public Task<Void> acceptFollowRequest(FollowRequest request) {
        return deleteFollowRequest(request)
                .continueWithTask(task -> addFollower(new Follower(request.getUid(), request.getUsername())));
    }

    /**
     * As the current user, deny a follow request
     * @param request The follow request to deny
     * @return A task which completes when the request has been denied
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
        }));
    }

    /**
     * Adds a follow request to the current user's list of follow requests. This overwrites any
     * follow requests that already might exist from the potentially following participant.
     * @param request The follow request to add
     * @return A task which completes when the request has been added
     */
    public Task<Void> addFollowRequest(FollowRequest request) {
        return followRequestsPath().document(request.getUid()).set(request);
    }

    /**
     * Deletes a follow request from the current user's list of follow requests. If the follow
     * request is not in the user's list of follow requests than nothing happens.
     * @param request The follow request to delete
     * @return A task which completes when the request has been deleted
     */
    public Task<Void> deleteFollowRequest(FollowRequest request) {
        return followRequestsPath().document(request.getUid()).delete();
    }

    /**
     * Gets all follow requests for the current user
     * @return A task which has as its result a list of the user's follow requests, in no
     *      particular order
     */
    public Task<List<FollowRequest>> getFollowRequests() {
        return followRequestsPath().get().continueWith(task -> {
            List<FollowRequest> requests = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult()) {
                requests.add(doc.toObject(FollowRequest.class));
            }
            return requests;
        });
    }

    /**
     * Adds a follower to the given user. If the follower already follows the current user,
     * overwrites information for that follower.
     * @param follower The follower to add
     * @return A task which completes once the follower has been added
     */
    public Task<Void> addFollower(Follower follower) {
        return followersPath().document(follower.getUid()).set(follower);
    }

    /**
     * Deletes a follower from the given user. If the follower doesn't follow the current user,
     * nothing happens.
     * @param follower The follower to delete
     * @return A task which completes once the follower has been deleted
     */
    public Task<Void> deleteFollower(Follower follower) {
        return followersPath().document(follower.getUid()).delete();
    }

    /**
     * Gets all followers for the current user
     * @return A task which has as its result a list of the user's followers, in no particular
     *      order
     */
    public Task<List<Follower>> getFollowers() {
        return followersPath().get().continueWith(task -> {
            List<Follower> followers = new ArrayList<>();
            for (DocumentSnapshot doc : task.getResult()) {
                followers.add(doc.toObject(Follower.class));
            }
            return followers;
        });
    }

    /**
     * Adds a user to the main list of app users
     * @param participant User to add
     * @return A task which completes once the user has been added
     */
    public Task<Void> addUser(Participant participant) {
        return db.collection("users").document(participant.getUid()).set(participant);
    }

    private Task<Void> deleteAllImmediateDocuments(QuerySnapshot ref) {
        List<Task<Void>> tasks = new ArrayList<>();
        for (DocumentSnapshot doc : ref) {
            tasks.add(doc.getReference().delete());
        }
        return Tasks.whenAll(tasks);
    }

    /**
     * Deletes a user entirely. This includes information in collections by other users. Slow
     * and costly. Use only for testing.
     * @param participant The user to delete.
     * @return
     */
    public Task<Void> deleteUser(Participant participant) {
        DocumentReference userPath = db.collection("users").document(participant.getUid());
        return userPath.collection("followers").get()
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> userPath.collection("follow_requests").get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> db.collectionGroup("followers").whereEqualTo("uid", user.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> db.collectionGroup("follow_requests").whereEqualTo("uid", user.getUid()).get())
                .continueWithTask(task -> deleteAllImmediateDocuments(task.getResult()))

                .continueWithTask(task -> userPath.delete());
    }

    /**
     * Checks whether or not the current user is following a given participant.
     * @param other The participant to check
     * @return A task which evaluates to true if and only if the current user is following the participant
     */
    public Task<Boolean> isFollowing(Follower other) {
        return followersPath().document(other.getUid())
                              .get()
                              .continueWith(task -> task.getResult().exists());
    }

    /**
     * Checks whether or not the current user is a follower of a given participant.
     * @param other The participant to check
     * @return A task which evaluates to true if and only if the current user is a follower of the participant
     */
    public Task<Boolean> isFollower(Participant other) {
        return db.collection("users")
                 .document(other.getUid())
                 .collection("followers")
                 .document(user.getUid())
                 .get()
                 .continueWith(task -> task.getResult().exists());
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