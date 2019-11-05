package com.example.mooderation.auth.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mooderation.R;
import com.example.mooderation.auth.base.AuthenticationError;
import com.example.mooderation.auth.base.AuthenticationResult;
import com.example.mooderation.auth.base.IAuthenticator;

/**
 * Activity that prompts the user to create an account with a username, email and password,
 * according to an Authenticator instance given to it in an Intent.
 * <br>
 * One may, for example, use this to allow a user to create an account with a FirebaseAuthenticator
 * instance:
 *
 * <pre>
 *     Intent intent = new Intent(this, SignUpActivity.class);
 *     intent.putExtra(SignUpActivity.AUTHENTICATOR, new FirebaseAuthenticator());
 *     startActivityForResult(intent, REQUEST_AUTHENTICATE);
 * </pre>
 */
public class SignUpActivity extends AppCompatActivity {
    public static String AUTHENTICATOR = "com.example.mooderation.signUpAuthenticator";

    private SignUpViewModel signUpViewModel;

    private EditText usernameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText password2EditText;
    private ProgressBar loadingProgressBar;

    /**
     * Sets up the Activity, binding the text fields and buttons appropriately.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Recover the IAuthenticator instance from the Intent
        Intent enterItent = getIntent();
        IAuthenticator authenticator = enterItent.getParcelableExtra(AUTHENTICATOR);

        // Make/recover the view model
        ViewModelAuthenticationFactory f = new ViewModelAuthenticationFactory(authenticator);
        this.signUpViewModel = ViewModelProviders.of(this, f).get(SignUpViewModel.class);

        // Bind listeners to text, buttons and ViewModel observers
        usernameEditText = findViewById(R.id.username);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        password2EditText = findViewById(R.id.password2);
        loadingProgressBar = findViewById(R.id.loading);
        final Button signUpButton = findViewById(R.id.signup);

        // Display/hide errors and enable/disable signup button depending on form state
        signUpViewModel.getSignUpFormState().observe(this, signUpFormState -> {
            if (signUpFormState == null) {
                return;
            }
            signUpButton.setEnabled(signUpFormState.isDataValid());
            usernameEditText.setError(signUpFormState.getUsernameError() == null ? null : getString(signUpFormState.getUsernameError()));
            emailEditText.setError(signUpFormState.getEmailError() == null ? null : getString(signUpFormState.getEmailError()));
            passwordEditText.setError(signUpFormState.getPasswordError() == null ? null : getString(signUpFormState.getPasswordError()));
            password2EditText.setError(signUpFormState.getPassword2Error() == null ? null : getString(signUpFormState.getPassword2Error()));
        });

        //Display sign-up error / exit activity on different sign-up results
        signUpViewModel.getsignUpResult().observe(this, this::handleSignUpResult);

        // Notify ViewModel of text changes
        AfterChangeTextWatcher afterTextChangedListener = s -> signUpViewModel.signUpDataChanged(
                usernameEditText.getText().toString(),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                password2EditText.getText().toString()
        );
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        password2EditText.addTextChangedListener(afterTextChangedListener);

        // Attempt sign-up when user enters IME_ACTION_DONE from the password-verify input
        password2EditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && isFormValid()) {
                beginSignUp();
            }
            return false;
        });

        // Attempt sign-up when the user presses the sign-up button
        signUpButton.setOnClickListener(v -> beginSignUp());
    }

    /**
     * Given an AuthenticationError, prints out a human-readable representation of the error to the
     * user in the form of a Toast.
     * <br>
     * TODO: This needs to print different errors depending on the argument given to it.
     *
     * @param error the error encountered when attempting to authenticate.
     */
    private void showSignUpFailed(AuthenticationError error) {
        int errorText = R.string.auth_error_signup_generic;

        if (error == AuthenticationError.USERNAME_COLLISION) errorText = R.string.auth_error_signup_username_collision;
        else if (error == AuthenticationError.EMAIL_COLLISION) errorText = R.string.auth_error_signup_email_collision;

        Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
    }

    /**
     * Query the ViewModel to find out if the form is in a valid state, and ready to be submitted
     *
     * @return True iff the sign-up form is in a valid state
     */
    private boolean isFormValid() {
        SignUpFormState signUpFormState = signUpViewModel.getSignUpFormState().getValue();
        return signUpFormState != null && signUpFormState.isDataValid();
    }

    /**
     * Called when the user indicates they are ready to sign up. Starts the progress bar and
     * initiates the Authenticator.signup process
     */
    private void beginSignUp() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        signUpViewModel.getAuthenticator().signup(
                usernameEditText.getText().toString(),
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                authResult -> signUpViewModel.setSignUpResult(authResult));
    }

    /**
     * Called when an authentication result is generated. Hides the progress bar and either exits
     * this activity (if the sign-up succeeded) or displays an error message (if it failed).
     *
     * @param signUpResult result of the sign-up attempt
     */
    private void handleSignUpResult(AuthenticationResult signUpResult) {
        if (signUpResult == null)
            return;

        loadingProgressBar.setVisibility(View.GONE);
        if (signUpResult.getFailure() != null) {
            showSignUpFailed(signUpResult.getFailure());
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }
}
