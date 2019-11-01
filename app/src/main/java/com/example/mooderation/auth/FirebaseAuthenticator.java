package com.example.mooderation.auth;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.UserProfileChangeRequest;


public class FirebaseAuthenticator implements Authenticator {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void login(String email, String password, OnResultListener onResultListener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> onResultListener.onResult(new AuthenticationResult(authResult.getUser())))
                .addOnFailureListener(e -> onResultListener.onResult(new AuthenticationResult(firebaseErrorToAuthenticationError(e))));
    }

    @Override
    public void signup(String username, String email, String password, OnResultListener onResultListener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> authResult.getUser().updateProfile(
                        new UserProfileChangeRequest.Builder().setDisplayName(username).build())
                        .addOnSuccessListener(aVoid -> onResultListener.onResult(new AuthenticationResult(authResult.getUser())))
                        .addOnFailureListener(e -> onResultListener.onResult(new AuthenticationResult(firebaseErrorToAuthenticationError(e))))
                )
                .addOnFailureListener(e -> onResultListener.onResult(new AuthenticationResult(firebaseErrorToAuthenticationError(e))));
    }

    @NonNull
    public static final Parcelable.Creator<FirebaseAuthenticator> CREATOR
            = new Parcelable.Creator<FirebaseAuthenticator>() {
        public FirebaseAuthenticator createFromParcel(Parcel in) {
            return new FirebaseAuthenticator();
        }

        @Override
        public FirebaseAuthenticator[] newArray(int size) {
            return new FirebaseAuthenticator[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {}

    @Override
    public int describeContents() {
        return 0;
    }

    private static AuthenticationError firebaseErrorToAuthenticationError(Exception e) {
        Log.d("FirebaseAuthenticator", e.toString());

        if (e instanceof FirebaseAuthUserCollisionException)
            return new EmailCollisionError();
        if (e instanceof FirebaseAuthEmailException)
            return new InvalidEmailError();
        if (e instanceof FirebaseAuthInvalidUserException)
            return new InvalidEmailError();
        if (e instanceof FirebaseAuthInvalidCredentialsException)
            return new InvalidPasswordError();

        return new AuthenticationError();
    }
}
