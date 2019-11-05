package com.example.mooderation.auth.base;

/**
 * Wraps errors generated by an IAuthenticator.
 *
 */
public enum AuthenticationError {
    INVALID_EMAIL,
    INVALID_PASSWORD,
    EMAIL_COLLISION,
    USERNAME_COLLISION,
    UNKNOWN
}