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
import com.example.mooderation.auth.base.IAuthenticator;

/**
 * Activity that prompts the user to create an account with a username, email and password,
 * according to an Authenticator instance given to it in an Intent.
 * <br />
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
    private SignUpViewModel signUpViewModel;
    public static String AUTHENTICATOR = "com.example.mooderation.signUpAuthenticator";

    /**
     * Sets up the Activity, binding the text fields and buttons appropriately.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent enterItent = getIntent();
        IAuthenticator authenticator = enterItent.getParcelableExtra(AUTHENTICATOR);

        ViewModelAuthenticationFactory f = new ViewModelAuthenticationFactory(authenticator);
        this.signUpViewModel = ViewModelProviders.of(this, f).get(SignUpViewModel.class);

        final EditText usernameEditText = findViewById(R.id.username);
        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final EditText password2EditText = findViewById(R.id.password2);
        final Button signUpButton = findViewById(R.id.signup);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        signUpViewModel.getSignUpFormState().observe(this, signUpFormState -> {
            if (signUpFormState == null) {
                return;
            }
            signUpButton.setEnabled(signUpFormState.isDataValid());
            if (signUpFormState.getUsernameError() != null) {
                usernameEditText.setError(getString(signUpFormState.getUsernameError()));
            }
            if (signUpFormState.getEmailError() != null) {
                emailEditText.setError(getString(signUpFormState.getEmailError()));
            }
            if (signUpFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(signUpFormState.getPasswordError()));
            }
            if (signUpFormState.getPassword2Error() != null) {
                password2EditText.setError(getString(signUpFormState.getPassword2Error()));
            }
        });

        signUpViewModel.getsignUpResult().observe(this, signUpResult -> {
            if (signUpResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (signUpResult.getFailure() != null) {
                showSignUpFailed(signUpResult.getFailure());
            } else {
                setResult(RESULT_OK);
                finish();
            }
        });


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

        password2EditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                signUpViewModel.signUp(
                        usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        signUpButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            signUpViewModel.signUp(
                    usernameEditText.getText().toString(),
                    emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });
    }

    /**
     * Given an AuthenticationError, prints out a human-readable representation of the error to the
     * user in the form of a Toast.
     * <br />
     * TODO: This needs to print different errors depending on the argument given to it.
     *
     * @param error the error encountered when attempting to authenticate.
     */
    private void showSignUpFailed(AuthenticationError error) {
        Toast.makeText(getApplicationContext(), "Error signing up. Do you already have an account?", Toast.LENGTH_LONG).show();
    }
}
