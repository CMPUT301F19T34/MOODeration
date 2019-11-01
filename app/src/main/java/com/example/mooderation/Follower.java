package com.example.mooderation;

/**
 * Stores public information about a single participant (generally not the current user)
 */
public class Follower {
    private String username;
    private String uid;

    public Follower() {
        username = "";
        uid = "";
    }

    public Follower(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getUid() {
        return uid;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        Follower follower = (Follower) other;
        return follower.uid.equals(uid) && follower.username.equals(username);
    }
}
