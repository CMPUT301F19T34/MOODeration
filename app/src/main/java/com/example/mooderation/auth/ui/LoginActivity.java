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
import com.example.mooderation.auth.base.AuthenticationResult;
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

    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar loadingProgressBar;

    /**
     * Sets up the Activity, binding the text fields and buttons appropriately.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_login);

        // Recover the IAuthenticator instance from the Intent
        Intent enterItent = getIntent();
        IAuthenticator authenticator = enterItent.getParcelableExtra(AUTHENTICATOR);

        // Make/recover the view model
        ViewModelAuthenticationFactory f = new ViewModelAuthenticationFactory(authenticator);
        this.loginViewModel = ViewModelProviders.of(this, f).get(LoginViewModel.class);

        // Bind listeners to text, buttons and ViewModel observers
        loadingProgressBar = findViewById(R.id.loading);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        final Button loginButton = findViewById(R.id.login);
        final Button signUpButton = findViewById(R.id.signup);

        // Display/hide errors and enable/disable login button depending on form state
        loginViewModel.getLoginFormState().observe(this, loginFormState -> {
            if (loginFormState == null) {
                return;
            }
            loginButton.setEnabled(loginFormState.isDataValid());
            emailEditText.setError(loginFormState.getEmailError() == null ? null : getString(loginFormState.getEmailError()));
            passwordEditText.setError(loginFormState.getPasswordError() == null ? null : getString(loginFormState.getPasswordError()));
        });

        // Display login error / exit activity on different login results
        loginViewModel.getLoginResult().observe(this, this::handleLoginResult);


        // Notify ViewModel of text changes
        AfterChangeTextWatcher afterTextChangedListener = s -> loginViewModel.loginDataChanged(
                emailEditText.getText().toString(), passwordEditText.getText().toString()
        );
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);


        // Attempt login when user enters IME_ACTION_DONE from the password input
        passwordEditText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE && isFormValid()) {
                beginLogin();
            }
            return false;
        });

        // Handle login and signup buttons
        loginButton.setOnClickListener(v -> beginLogin());
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
        int errorText = R.string.auth_error_login_generic;

        if (error == AuthenticationError.INVALID_EMAIL) errorText = R.string.auth_error_login_invalid_email;
        else if (error == AuthenticationError.INVALID_PASSWORD) errorText = R.string.auth_error_login_invalid_password;

        Toast.makeText(getApplicationContext(), errorText, Toast.LENGTH_LONG).show();
    }

    /**
     * Launches a new activity that allows the user to create an account.
     */
    private void startSignupActivity() {
        Intent intent = new Intent(this, SignUpActivity.class);
        intent.putExtra(SignUpActivity.AUTHENTICATOR, loginViewModel.getAuthenticator());
        startActivityForResult(intent, REQUEST_SIGNUP);
    }

    /**
     * Query the ViewModel to find out if the form is in a valid state, and ready to be submitted
     *
     * @return True iff the login form is in a valid state
     */
    private boolean isFormValid() {
        LoginFormState loginFormState = loginViewModel.getLoginFormState().getValue();
        return loginFormState != null && loginFormState.isDataValid();
    }

    /**
     * Called when the user indicates they are ready to log in. Starts the progress bar and
     * initiates the Authenticator.login process
     */
    private void beginLogin() {
        loadingProgressBar.setVisibility(View.VISIBLE);
        loginViewModel.getAuthenticator().login(
                emailEditText.getText().toString(),
                passwordEditText.getText().toString(),
                authResult -> loginViewModel.setLoginResult(authResult));
    }

    /**
     * Called when an authentication result is generated. Hides the progress bar and either exits
     * this activity (if the login succeeded) or displays an error message (if the login failed).
     *
     * @param loginResult result of the login attempt
     */
    private void handleLoginResult(AuthenticationResult loginResult) {
        if (loginResult == null)
            return;

        loadingProgressBar.setVisibility(View.GONE);
        if (loginResult.getFailure() != null) {
            showLoginFailed(loginResult.getFailure());
        } else {
            setResult(RESULT_OK);
            finish();
        }
    }
}
