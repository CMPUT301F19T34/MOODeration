package com.example.mooderation.auth.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.mooderation.auth.base.AuthenticationError;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthentication;
import com.example.mooderation.auth.base.IAuthenticator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;


/**
 * An implementation of the IAuthenticator interface that wraps firebase authentication.
 *
 * TODO: the sign-up logic pertaining to a username is not written yet.
 */
public class FirebaseAuthenticator implements IAuthenticator {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    /**
     * Attempt to authenticate using firebase's email-and-password method.
     *
     * @param email email
     * @param password password
     * @param listener listener called when login succeeds or fails
     */
    @Override
    public void login(String email, String password, AuthenticationResultListener listener) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    IAuthentication auth = new FirebaseAuthentication(authResult.getUser());
                    listener.onAuthenticateResult(new AuthenticationResult(auth));
                })
                .addOnFailureListener(authException -> {
                    AuthenticationError e = translateFirebaseException((FirebaseAuthException) authException);
                    listener.onAuthenticateResult(new AuthenticationResult(e));
                });
    }

    /**
     * Attempt to create a new firebase account with the given username, email and password, then
     * authenticate using the new account.
     *
     * Note: the username is not part of firebase's built-in authentication, and so is handled
     * seperately, by storing it in a firestore collection. This is unfortunately therefore a bit of
     * a leaky abstraction.
     *
     * @param username username
     * @param email email
     * @param password password
     * @param listener listener called when the signup succeeds or fails
     */
    @Override
    public void signup(String username, String email, String password, AuthenticationResultListener listener) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    // TODO: Create username here
                    IAuthentication auth = new FirebaseAuthentication(authResult.getUser());
                    listener.onAuthenticateResult(new AuthenticationResult(auth));
                })
                .addOnFailureListener(authException -> {
                    AuthenticationError e = translateFirebaseException((FirebaseAuthException) authException);
                    listener.onAuthenticateResult(new AuthenticationResult(e));
                });
    }

    /**
     * Translates firebase's authentication exceptions into AuthenticationErrors that can be
     * attached to AuthenticationResults.
     *
     * @param e FirebaseException
     * @return corresponding AuthenticationError
     */
    private AuthenticationError translateFirebaseException(FirebaseAuthException e) {
        // Convert from generic exceptions to AuthenticationExceptions
        // TODO: implement this properly
        return new AuthenticationError();
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
