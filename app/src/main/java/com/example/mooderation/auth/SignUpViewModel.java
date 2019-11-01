package com.example.mooderation.auth;

import android.util.Patterns;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mooderation.R;

class SignUpViewModel extends ViewModel {

    private MutableLiveData<SignUpFormState> signUpFormState = new MutableLiveData<>();
    private MutableLiveData<AuthenticationResult> signUpResult = new MutableLiveData<>();
    private Authenticator authenticator;

    LiveData<SignUpFormState> getSignUpFormState() {return signUpFormState;}
    LiveData<AuthenticationResult> getsignUpResult() {return signUpResult;}

    public SignUpViewModel(Authenticator authenticator) {
        this.authenticator = authenticator;
    }

    void signUp(String username, String email, String password) {
        authenticator.signup(username, email, password, result -> signUpResult.setValue(result));
    }

    public void signUpDataChanged(String username, String email, String password, String password2) {
        signUpFormState.setValue(new SignUpFormState(
                isUsernameValid(username) ? null : R.string.invalid_username,
                isEmailValid(email) ? null : R.string.invalid_email,
                isPasswordValid(password) ? null : R.string.invalid_password,
                password.equals(password2) ? null : R.string.invalid_password2
            ));
    }

    private boolean isUsernameValid(String username) {
        return username != null && username.trim().length() > 5;
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}
