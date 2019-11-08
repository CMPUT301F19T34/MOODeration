package com.example.mooderation.auth.ui;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.R;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthenticator;

/**
 * ViewModel for the SignUpActivity
 */
class SignUpViewModel extends ViewModel {

    private MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();
    private MutableLiveData<AuthenticationResult> signUpResult = new MutableLiveData<>();
    private IAuthenticator authenticator;

    /**
     * Initialize the ViewModel.
     *
     * @param authenticator IAuthenticator instance the SignUpActivity will use
     */
    public SignUpViewModel(IAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Gets the SignUpFormState corresponding to the SignUpActivity
     *
     * @return SignUpFormState corresponding to the SignUpActivity
     */
    LiveData<SignUpFormState> getSignUpFormState() {
        return signUpFormState;
    }

    /**
     * Gets the AuthenticationResult corresponding to the SignUpActivity
     *
     * @return AuthenticationResult corresponding to the SignUpActivity
     */
    LiveData<AuthenticationResult> getsignUpResult() {
        return signUpResult;
    }

    /**
     * Gets the IAuthentication instance corresponding to the SignUpActivity
     *
     * @return IAuthentication corresponding to the SignUpActivity
     */
    IAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Sets the sign-up result from an AuthenticationResult
     *
     * @param signUpResult sign-up result to set the ViewModel's sign-up result to
     */
    void setSignUpResult(AuthenticationResult signUpResult) {
        this.signUpResult.setValue(signUpResult);
    }

    /**
     * Sets the error state according to the current value of the fields entered by the user
     *
     * @param username Current username form value
     * @param email Current email form value
     * @param password Current password form value
     * @param password2 Current validation-password form value
     */
    void signUpDataChanged(String username, String email, String password, String password2) {
        signUpFormState.setValue(new SignUpFormState(
                isUsernameValid(username) ? null : R.string.auth_prompt_invalid_username,
                isEmailValid(email) ? null : R.string.auth_prompt_invalid_email,
                isPasswordValid(password) ? null : R.string.auth_prompt_invalid_password,
                password.equals(password2) ? null : R.string.auth_prompt_invalid_password2
            ));
    }

    /**
     * Check if a string is a valid username.
     *
     * @param username String to check
     * @return True iff the string is a valid username
     */
    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 5;
    }

    /**
     * Check if a string is a valid email.
     *
     * @param email String to check
     * @return True iff the string is a valid email
     */
    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Check if a string is a valid password
     *
     * @param password String to check
     * @return True iff the string is a valid password
     */
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
