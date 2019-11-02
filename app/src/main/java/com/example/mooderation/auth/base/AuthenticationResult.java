package com.example.mooderation.auth.base;


public class AuthenticationResult {
    private IAuthentication success;
    private AuthenticationException failure;

    public AuthenticationResult(IAuthentication success) {
        this.success = success;
        this.failure = null;
    }

    public AuthenticationResult(AuthenticationException failure) {
        this.success = null;
        this.failure = failure;
    }

    public IAuthentication getSuccess() {
        return success;
    }

    public AuthenticationException getFailure() {
        return failure;
    }
}
