package com.example.mooderation.auth.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.util.Patterns;

import com.example.mooderation.R;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthenticator;

/**
 * ViewModel for the LoginActivity
 */
public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<AuthenticationResult> loginResult = new MutableLiveData<>();
    private IAuthenticator authenticator;

    /**
     * Initialize the ViewModel.
     *
     * @param authenticator IAuthenticator instance the LoginState will use
     */
    public LoginViewModel(IAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Gets the LoginFormState corresponding to the LoginActivity
     *
     * @return LoginFormState corresponding to the LoginActivity
     */
    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    /**
     * Gets the AuthenticationResult corresponding to the LoginActivity
     *
     * @return AuthenticationResult corresponding to the LoginActivity
     */
    LiveData<AuthenticationResult> getLoginResult() {
        return loginResult;
    }

    /**
     * Gets the IAuthentication instance corresponding to the LoginActivity
     *
     * @return IAuthentication corresponding to the LoginActivity
     */
    IAuthenticator getAuthenticator() {
        return authenticator;
    }

    /**
     * Sets the login result from an AuthenticationResult
     *
     * @param loginResult login result to set the ViewModel's login result to
     */
    void setLoginResult(AuthenticationResult loginResult) {
        this.loginResult.setValue(loginResult);
    }

    /**
     * Sets the error state according to the current value of the email and password entered by the
     * user
     *
     * @param email Current email form value
     * @param password Current password form value
     */
    void loginDataChanged(String email, String password) {
        loginFormState.setValue(new LoginFormState(
                isEmailValid(email) ? null : R.string.auth_prompt_invalid_email,
                isPasswordValid(password) ? null : R.string.auth_prompt_invalid_password
        ));
    }

    /**
     * Check if a string is a valid email.
     *
     * @param email String to check
     * @return True iff the string is a valid email
     */
    private boolean isEmailValid(String email) {
        if (email == null) {
            return false;
        }
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
