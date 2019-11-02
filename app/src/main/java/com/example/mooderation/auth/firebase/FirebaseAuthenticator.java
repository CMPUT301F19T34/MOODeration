package com.example.mooderation.auth.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.mooderation.auth.base.AuthenticationException;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthentication;
import com.example.mooderation.auth.base.IAuthenticator;
import com.google.firebase.auth.FirebaseAuth;


public class FirebaseAuthenticator implements IAuthenticator {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    public void login(String email, String password, AuthenticationResultListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    IAuthentication auth = new FirebaseAuthentication(authResult.getUser());
                    listener.onAuthenticateResult(new AuthenticationResult(auth));
                })
                .addOnFailureListener(authException -> {
                    AuthenticationException e = translateFirebaseException(authException);
                    listener.onAuthenticateResult(new AuthenticationResult(e));
                });
    }

    @Override
    public void signup(String username, String email, String password, AuthenticationResultListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // TODO: Create username here
                    IAuthentication auth = new FirebaseAuthentication(authResult.getUser());
                    listener.onAuthenticateResult(new AuthenticationResult(auth));
                })
                .addOnFailureListener(authException -> {
                    AuthenticationException e = translateFirebaseException(authException);
                    listener.onAuthenticateResult(new AuthenticationResult(e));
                });
    }

    private AuthenticationException translateFirebaseException(Exception e) {
        // Convert from generic exceptions to AuthenticationExceptions
        // TODO: implement this properly
        return new AuthenticationException();
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
}
