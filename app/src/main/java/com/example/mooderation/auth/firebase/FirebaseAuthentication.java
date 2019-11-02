package com.example.mooderation.auth.firebase;

import com.example.mooderation.auth.base.IAuthentication;
import com.google.firebase.auth.FirebaseUser;

/**
 * Stores the credentials of a successful Firebase authentication.
 */
public class FirebaseAuthentication implements IAuthentication {
    private FirebaseUser firebaseUser;

    /**
     * Create a FirebaseAuthentication that refers to a FirebaseUser storing firebase authentication
     * credentials.
     *
     * @param user firebase authentication credentials
     */
    public FirebaseAuthentication(FirebaseUser user) {
        this.firebaseUser = user;
    }

    /**
     * Returns firebase authentication credentials
     * @return FirebaseUser containing firebase authentication credentials
     */
    public FirebaseUser getUser() {
        return firebaseUser;
    }
}
