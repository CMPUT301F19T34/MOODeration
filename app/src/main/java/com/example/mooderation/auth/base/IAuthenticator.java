package com.example.mooderation.auth.base;

import android.os.Parcelable;


public interface IAuthenticator extends Parcelable {
    interface AuthenticationResultListener {
        void onAuthenticateResult(AuthenticationResult authResult);
    }

    void login(String email, String password, AuthenticationResultListener listener);
    void signup(String username, String email, String password, AuthenticationResultListener listener);
}
