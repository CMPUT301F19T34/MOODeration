package com.example.mooderation.backend;

import com.example.mooderation.FollowRequest;

import java.util.List;

/**
 * The connection to the database backend (and to user authentication)
 */
public interface Database {
    /**
     * Checks whether the user is logged in
     * @return true if and only if any user is currently authenticated
     */
    boolean authenticated();

    /**
     * As the current user, accept a follow request
     * @param request The follow request to accept
     */
    void acceptFollowRequest(FollowRequest request);

    /**
     * As the current user, deny a follow request
     * @param request The follow request to deny
     */
    void denyFollowReqest(FollowRequest request);

    /**
     * Listen for changes to the current user's list of follow requests. The listener is called
     * once immediately and thereafter whenever the current user's list of follow requests changes.
     * @param listener Listener to listen for changes
     */
    void addFollowRequestsListener(FollowRequestsListener listener);

    /**
     * Listener to listen for changes to the current user's follow request list
     */
    interface FollowRequestsListener {
        /**
         * Notify the listener that the follower list data has changed. This function is called
         * once immediately when the listener is added and thereafter whenever the list changes.
         * @param requests The updated list of follow requests from the database, in arbitrary order
         */
        void onDataChanged(List<FollowRequest> requests);
    }
}
