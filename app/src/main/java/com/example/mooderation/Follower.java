package com.example.mooderation;

/**
 * Stores information about a single follower who is following the current user
 */
public class Follower {
    private String username;
    private String uid;

    /**
     * Empty constructor to allow this class to be serialized by Firebase
     */
    public Follower() {
        username = "";
        uid = "";
    }

    /**
     * Initialize the follower information
     * @param uid User id of the follower
     * @param username User name of the follower
     */
    public Follower(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    /**
     * Gets the follower's user name
     * @return The user name of the follower
     */
    public String getUsername() {
        return username;
    }

    /**
     * Gets the follower's user id
     * @return The user id of the follower
     */
    public String getUid() {
        return uid;
    }

    /**
     * Follower are considered to be equal if they have the same username and user id
     * @param other Object against which to check equality
     * @return True if and only if other is a follower and is equal to this follower
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        Follower follower = (Follower) other;
        return follower.uid.equals(uid) && follower.username.equals(username);
    }
}
