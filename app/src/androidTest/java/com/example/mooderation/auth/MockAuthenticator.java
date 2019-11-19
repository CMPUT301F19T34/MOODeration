package com.example.mooderation.auth;

import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.mooderation.auth.base.AuthenticationError;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthentication;
import com.example.mooderation.auth.base.IAuthenticator;


class MockAuthenticationResult implements IAuthentication {
    MockAuthenticationResult() {}

    @NonNull
    static final Parcelable.Creator<MockAuthenticationResult> CREATOR
            = new Parcelable.Creator<MockAuthenticationResult>() {
        public MockAuthenticationResult createFromParcel(Parcel in) {
            return new MockAuthenticationResult();
        }

        @Override
        public MockAuthenticationResult[] newArray(int size) {
            return new MockAuthenticationResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}


class MockAuthenticator implements IAuthenticator {
    static final String NEW_USERNAME = "new-username";
    static final String NEW_EMAIL = "new-email@mail.com";
    static final String NEW_PASSWORD = "new-password";
    static final String REGISTERED_USERNAME = "registered-username";
    static final String REGISTERED_EMAIL = "registered-email@mail.com";
    static final String REGISTERED_PASSWORD = "registered-password";
    static final String INVALID_USERNAME = "123";
    static final String INVALID_EMAIL = "invalid-email";
    static final String INVALID_PASSWORD = "123";
    static final String WRONG_EMAIL = "wrong-email@mail.com";
    static final String WRONG_PASSWORD = "wrong-password@mail.com";
    static final String GENERIC_ERROR_EMAIL = "generic-error@mail.com";
    static final String DELAY_EMAIL = "delay@email.com";

    MockAuthenticator() {}

    @Override
    public void login(String email, String password, AuthenticationResultListener listener) {
        if (email.equals(REGISTERED_EMAIL) && password.equals(REGISTERED_PASSWORD)) {
            listener.onAuthenticateResult(new AuthenticationResult(new MockAuthenticationResult()));
        } else if (email.equals(WRONG_EMAIL)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.INVALID_EMAIL));
        } else if (password.equals(WRONG_PASSWORD)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.INVALID_PASSWORD));
        } else if (email.equals(GENERIC_ERROR_EMAIL)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.UNKNOWN));
        } else if (email.equals(DELAY_EMAIL)) {
            new Handler().postDelayed(
                    () -> {
                        Log.d("DELAYED", "DELAY");
                        listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.UNKNOWN));
                    },
                    2000
            );
        }
    }

    @Override
    public void signup(String username, String email, String password, AuthenticationResultListener listener) {
        if (username.equals(NEW_USERNAME) && email.equals(NEW_EMAIL) && password.equals(NEW_PASSWORD)) {
            listener.onAuthenticateResult(new AuthenticationResult(new MockAuthenticationResult()));
        } else if (email.equals(REGISTERED_EMAIL)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.EMAIL_COLLISION));
        } else if (username.equals(REGISTERED_USERNAME)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.USERNAME_COLLISION));
        } else if (email.equals(GENERIC_ERROR_EMAIL)) {
            listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.UNKNOWN));
        } else if (email.equals(DELAY_EMAIL)) {
            new Handler().postDelayed(
                    () -> listener.onAuthenticateResult(new AuthenticationResult(AuthenticationError.UNKNOWN)),
                    500
            );
        }
    }

    public static final Parcelable.Creator<MockAuthenticator> CREATOR
            = new Parcelable.Creator<MockAuthenticator>() {

        @Override
        public MockAuthenticator createFromParcel(Parcel source) {
            return new MockAuthenticator(source);
        }

        @Override
        public MockAuthenticator[] newArray(int size) {
            return new MockAuthenticator[size];
        }
    };

    private MockAuthenticator(Parcel in) {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
