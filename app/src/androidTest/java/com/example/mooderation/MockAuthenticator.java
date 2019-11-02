package com.example.mooderation;

import android.os.Parcel;
import android.os.Parcelable;


import com.example.mooderation.auth.base.AuthenticationError;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthentication;
import com.example.mooderation.auth.base.IAuthenticator;


class MockAuthenticator implements IAuthenticator {
    MockAuthenticator() {}

    @Override
    public void login(String email, String password, AuthenticationResultListener listener) {
        if (email.equals("registered-email@mail.com") && password.equals("registered-password")) {
            IAuthentication authentication = new IAuthentication() {
            };
            listener.onAuthenticateResult(new AuthenticationResult(authentication));
        } else {
            listener.onAuthenticateResult(new AuthenticationResult(new AuthenticationError()));
        }
    }

    @Override
    public void signup(String username, String email, String password, AuthenticationResultListener listener) {
        listener.onAuthenticateResult(new AuthenticationResult(new AuthenticationError()));
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
