package com.example.mooderation.auth.firebase;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.example.mooderation.auth.base.IAuthentication;
import com.google.firebase.auth.FirebaseUser;

/**
 * Stores the credentials of a successful Firebase authentication.
 */
public class FirebaseAuthentication implements IAuthentication {
    private FirebaseUser firebaseUser;
    private String username;

    /**
     * Create a FirebaseAuthentication that refers to a FirebaseUser storing firebase authentication
     * credentials.
     *
     * @param user firebase authentication credentials
     */
    public FirebaseAuthentication(FirebaseUser user, String username) {
        this.firebaseUser = user;
        this.username = username;
    }

    /**
     * Returns firebase authentication credentials
     * @return FirebaseUser containing firebase authentication credentials
     */
    public FirebaseUser getUser() {
        return firebaseUser;
    }

    public String getUsername() {
        return username;
    }

    @NonNull
    public static final Parcelable.Creator<FirebaseAuthentication> CREATOR
            = new Parcelable.Creator<FirebaseAuthentication>() {
        public FirebaseAuthentication createFromParcel(Parcel in) {
            return new FirebaseAuthentication(in);
        }

        @Override
        public FirebaseAuthentication[] newArray(int size) {
            return new FirebaseAuthentication[size];
        }
    };

    private FirebaseAuthentication(Parcel in) {
        this(
                in.readParcelable(FirebaseUser.class.getClassLoader()),
                in.readString()
        );
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(getUser(), flags);
        dest.writeString(getUsername());
    }

    @Override
    public int describeContents() {
        return 0;
    }
}
