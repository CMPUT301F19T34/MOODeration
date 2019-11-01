package com.example.mooderation;

import com.google.firebase.Timestamp;

/**
 * Represents a single request by a given participant to follow the current user
 */
public class FollowRequest {
    /**
     * Initialize the follow request
     * @param uid User id of the potential follower
     * @param username User name of the potential follower
     * @param createTimestamp Time at which the follow request was created
     */
    public FollowRequest(String uid, String username, Timestamp createTimestamp) {
        this.uid = uid;
        this.username = username;
        this.createTimestamp = createTimestamp;
    }

    /**
     * A blank constructor to allow FollowRequest to be serialized by Firestore
     */
    public FollowRequest() {
        uid = "";
        username = "";
        createTimestamp = Timestamp.now();
    }

    /**
     * Gets the user id of the potential follower
     * @return User id of the potential follower
     */
    public String getUid() {
        return uid;
    }

    /**
     * Gets the user name of the potential follower
     * @return User name of the potential follower
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the creation time of the follow request
     * @return The creation time of the follow request
     */
    public Timestamp getCreateTimestamp() {
        return createTimestamp;
    }

    /**
     * Follow requests are equal if they have identical fields
     *
     * @param other The object against which to check equality
     * @return True if and only if other is a FollowRequest and has equal uid, username, and createTimestamp
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        FollowRequest request = (FollowRequest) other;
        return request.uid.equals(uid) && request.username.equals(username) && request.createTimestamp.equals(createTimestamp);
    }

    private String uid;
    private String username;
    private Timestamp createTimestamp;
}
