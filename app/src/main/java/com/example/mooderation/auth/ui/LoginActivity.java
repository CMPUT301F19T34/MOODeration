package com.example.mooderation.auth.ui;

import androidx.annotation.Nullable;
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
 * Activity that prompts the user to log in using an email and password, or create with a username,
 * email and password, according to an Authenticator instance given to it in an Intent.
 * <br />
 * One may, for example, use this to allow a user to authenticate with Firebase:
 *
 * <pre>
 *     FirebaseAuth.getInstance().addAuthStateListener(firebaseAuth -> {
 *             if (firebaseAuth.getCurrentUser() == null) {
 *                 Intent intent = new Intent(this, LoginActivity.class);
 *                 intent.putExtra(LoginActivity.AUTHENTICATOR, new FirebaseAuthenticator());
 *                 startActivityForResult(intent, REQUEST_AUTHENTICATE);
 *             } else {
 *                 String welcome = "Logged in as " + firebaseAuth.getCurrentUser().getEmail();
 *                 Toast.makeText(this, welcome, Toast.LENGTH_LONG).show();
 *             }
 *         });
 * </pre>
 */
public class LoginActivity extends AppCompatActivity {
    public static int REQUEST_SIGNUP = 0;
    public static String AUTHENTICATOR = "com.example.mooderation.loginAuthenticator";

    private LoginViewModel loginViewModel;

    /**
     * Sets up the Activity, binding the text fields and buttons appropriately.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        Intent enterItent = getIntent();
        IAuthenticator authenticator = enterItent.getParcelableExtra(AUTHENTICATOR);

        ViewModelAuthenticationFactory f = new ViewModelAuthenticationFactory(authenticator);
        this.loginViewModel = ViewModelProviders.of(this, f).get(LoginViewModel.class);

        final EditText emailEditText = findViewById(R.id.email);
        final EditText passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.signup);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);

        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            if (loginFormState.getEmailError() != null) {
                emailEditText.setError(getString(loginFormState.getEmailError()));
            }
            if (loginFormState.getPasswordError() != null) {
                passwordEditText.setError(getString(loginFormState.getPasswordError()));
            }
        });

        loginViewModel.getLoginResult().observe(this, loginResult -> {
            if (loginResult == null) {
                return;
            }
            loadingProgressBar.setVisibility(View.GONE);
            if (loginResult.getFailure() != null) {
                showLoginFailed(loginResult.getFailure());
            }
            else {
                setResult(RESULT_OK);
                finish();
            }
        });

        AfterChangeTextWatcher afterTextChangedListener = s -> loginViewModel.loginDataChanged(
                emailEditText.getText().toString(), passwordEditText.getText().toString()
        );
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(emailEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
            return false;
        });

        loginButton.setOnClickListener(v -> {
            loadingProgressBar.setVisibility(View.VISIBLE);
            loginViewModel.login(emailEditText.getText().toString(),
                    passwordEditText.getText().toString());
        });

        signUpButton.setOnClickListener(v -> startSignupActivity());
    }

    /**
     * Called when the user exits the SignUpActivity. If the result code is RESULT_OK, the user has
     * created an account and signed in, so quit out of this activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    /**
     * Called when the user presses the back button. Overrides the default behaviour to prevent the
     * user from quitting out of this activity back into the main activity.
     */
    @Override
    public void onBackPressed() {
        // Prevent the back button from switching activities here
        this.moveTaskToBack(true);
    }

    /**
     * Given an AuthenticationError, prints out a human-readable representation of the error to the
     * user in the form of a Toast.
     * <br />
     * TODO: This needs to print different errors depending on the argument given to it.
     *
     * @param error the error encountered when attempting to authenticate.
     */
    private void showLoginFailed(AuthenticationError error) {
        Toast.makeText(getApplicationContext(), "Error logging in. Are your email and password correct?", Toast.LENGTH_LONG).show();
    }

    /**
     * Launches a new activity that allows the user to create an account.
     */
    private void startSignupActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(SignUpActivity.AUTHENTICATOR, loginViewModel.getAuthenticator());
        startActivityForResult(intent, REQUEST_SIGNUP);
    }
}
