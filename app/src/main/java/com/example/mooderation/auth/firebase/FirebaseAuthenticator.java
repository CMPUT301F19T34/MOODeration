package com.example.mooderation.auth.firebase;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mooderation.auth.base.AuthenticationError;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthentication;
import com.example.mooderation.auth.base.IAuthenticator;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.SuccessContinuation;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthEmailException;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.StructuredQuery;

import java.util.HashMap;
import java.util.concurrent.ExecutionException;


/**
 * An implementation of the IAuthenticator interface that wraps firebase authentication.
 *
 * TODO: the sign-up logic pertaining to a username is not written yet.
 */
public class FirebaseAuthenticator implements IAuthenticator {
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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
                    final FirebaseUser firebaseUser = authResult.getUser();
                    db.collection("users").document(firebaseUser.getUid()).get()
                        .addOnSuccessListener(documentSnapshot -> {
                            String username = (String) documentSnapshot.get("username");
                            FirebaseAuthentication auth = new FirebaseAuthentication(firebaseAuth.getCurrentUser(), username);
                            listener.onAuthenticateResult(new AuthenticationResult(auth));
                        })
                        .addOnFailureListener(authException -> {
                            AuthenticationError e = translateFirebaseException(authException);
                            listener.onAuthenticateResult(new AuthenticationResult(e));
                        })
                    ;
                })
                .addOnFailureListener(authException -> {
                    AuthenticationError e = translateFirebaseException(authException);
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
        db.collection("users").whereEqualTo("username", username).get().addOnCompleteListener(queryTask -> {
            if (queryTask.getException() != null || !queryTask.getResult().isEmpty()) {
                listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.USERNAME_COLLISION));
                return;
            }

            firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(authTask -> {
                if (authTask.getException() != null) {
                    listener.onAuthenticateResult(new AuthenticationResult(translateFirebaseException(authTask.getException())));
                    return;
                }
                FirebaseUser user = authTask.getResult().getUser();

                db.collection("users").document(user.getUid()).set(new HashMap<String, String>() {{
                    put("uid", user.getUid());
                    put("username", username);
                }})
                .addOnCompleteListener(setTask -> {
                    if (setTask.getException() != null) {
                        user.delete();
                        listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.UNKNOWN));
                        return;
                    }

                    listener.onAuthenticateResult(new AuthenticationResult(new FirebaseAuthentication(user, username)));
                });
            });
        });
    }

    /**
     * Translates firebase's authentication exceptions into AuthenticationErrors that can be
     * attached to AuthenticationResults.
     *
     * @param e Exception
     * @return corresponding AuthenticationError
     */
    private AuthenticationError translateFirebaseException(Exception e) {
        // Convert from generic exceptions to AuthenticationExceptions
        if (e instanceof FirebaseAuthUserCollisionException)
            return AuthenticationError.EMAIL_COLLISION;
        if (e instanceof FirebaseAuthInvalidUserException)
            return AuthenticationError.INVALID_EMAIL;
        if (e instanceof FirebaseAuthInvalidCredentialsException)
            return AuthenticationError.INVALID_PASSWORD;

        return AuthenticationError.UNKNOWN;
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
