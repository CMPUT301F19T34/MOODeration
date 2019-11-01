package com.example.mooderation.auth;

import android.os.Parcelable;

class AuthenticationError extends Exception {}

class InvalidEmailError extends AuthenticationError {}
class InvalidUsernameError extends AuthenticationError {}
class InvalidPasswordError extends AuthenticationError {}
class UsernameCollisionError extends AuthenticationError {}
class EmailCollisionError extends AuthenticationError {}
class LoginError extends AuthenticationError {}

public interface Authenticator extends Parcelable {

    interface OnResultListener {
        void onResult(AuthenticationResult result);
    }

    void login(String email, String password, OnResultListener onResultListener);
    void signup(String username, String email, String password, OnResultListener onResultListener);
}
