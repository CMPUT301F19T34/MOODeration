package com.example.mooderation;

/**
 * Stores public information about a single participant (generally not the current user)
 */
public class Participant {
    private String username;
    private String uid;

    public Participant(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
    public String getUid() {
        return uid;
    }
}
