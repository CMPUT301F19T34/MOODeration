package com.example.mooderation;

import org.jetbrains.annotations.NotNull;

public class Participant {
    private String uid;
    private String username;

    public Participant() {
        uid = "";
        username = "";
    }

    public Participant(String uid, String username) {
        this.uid = uid;
        this.username = username;
    }

    public String getUid() {
        return uid;
    }
    public String getUsername() {
        return username;
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) return true;
        if (other == null) return false;
        if (other.getClass() != getClass()) return false;
        Participant participant = (Participant) other;
        return participant.uid.equals(uid) && participant.username.equals(username);
    }

    @NotNull
    @Override
    public String toString() {
        return String.format("{uid: %s, username: %s}", getUid(), getUsername());
    }

    @Override
    public int hashCode() {
        return this.getUid().hashCode() + this.getUsername().hashCode();
    }
}
