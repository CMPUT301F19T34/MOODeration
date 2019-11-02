package com.example.mooderation.auth.base;

import android.os.Parcelable;


/**
 * Interface for a class that wraps an authentication system. Allows a user to authenticate either
 * by logging into an existing account, or by creating a new one. Implements the Parcelable
 * interface so it can be passed between activities.
 *
 * todo: allowing authentication and being Parcelable are not related -- perhaps seperate into IAuthenticator and IParcelableAuthenticator?
 */
public interface IAuthenticator extends Parcelable {
    /**
     * Listener that yields an AuthenticationResult when authentication either succeeds or fails.
     */
    interface AuthenticationResultListener {
        /**
         * Method that gives an AuthenticationResult indicating whether the authentication attempt
         * succeeded or failed.
         * @param authResult authentication result
         */
        void onAuthenticateResult(AuthenticationResult authResult);
    }

    /**
     * Attempt to authenticate by logging into a pre-existing account with an email and password.
     * This method should be asynchronous, calling the onAuthenticateResult of the given listener
     * when authentication succeeds or fails.
     *
     * @param email email
     * @param password password
     * @param listener listener called when login succeeds or fails
     */
    void login(String email, String password, AuthenticationResultListener listener);

    /**
     * Attempt to authenticate by creating a new account with a username, email, and password. This
     * method should be asynchronous, calling the onAuthenticateResult of the given listener
     * when account creation and subsequent login succeeds or fails.
     * @param username username
     * @param email email
     * @param password password
     * @param listener listener called when the signup succeeds or fails
     */
    void signup(String username, String email, String password, AuthenticationResultListener listener);
}
