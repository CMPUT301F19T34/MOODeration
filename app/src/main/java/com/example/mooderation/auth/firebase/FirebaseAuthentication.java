package com.example.mooderation.auth.firebase;

import com.example.mooderation.auth.base.IAuthentication;
import com.google.firebase.auth.FirebaseUser;


public class FirebaseAuthentication implements IAuthentication {
    private FirebaseUser firebaseUser;

    public FirebaseAuthentication(FirebaseUser user) {
        this.firebaseUser = user;
    }

    public FirebaseUser getUser() {
        return firebaseUser;
    }
}
