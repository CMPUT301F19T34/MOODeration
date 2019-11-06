package com.example.mooderation.auth.base;


/**
 * Contains the results of an authentication query. The query is either a success, in which case
 * the authentication credentials may be obtained using getSuccess(), or a failure, in which case
 * the error may be obtained using getFailure().
 */
public class AuthenticationResult {
    private IAuthentication success;
    private AuthenticationError failure;

    /**
     * Initialize AuthenticationResult indicating successful authentication.
     *
     * @param success authentication credentials
     */
    public AuthenticationResult(IAuthentication success) {
        this.success = success;
        this.failure = null;
    }

    /**
     * Initialize AuthenticationResult indicating failed authentication.
     *
     * @param failure failure type
     */
    public AuthenticationResult(AuthenticationError failure) {
        this.success = null;
        this.failure = failure;
    }

    /**
     * Obtains the authentication credentials from a successful authentication, or null if the
     * authentication failed.
     *
     * @return authentication credentials, or null.
     */
    public IAuthentication getSuccess() {
        return success;
    }

    /**
     * Obtains the error type of a failed authentication, or null if the authentication succeeded.
     *
     * @return failure type, or null
     */
    public AuthenticationError getFailure() {
        return failure;
    }
}
