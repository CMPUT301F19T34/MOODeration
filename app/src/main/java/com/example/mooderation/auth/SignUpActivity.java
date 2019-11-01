package com.example.mooderation.auth;

import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.mooderation.R;

public class SignUpActivity extends AppCompatActivity {
    private SignUpViewModel signUpViewModel;
    public static String AUTHENTICATOR = "com.example.mooderation.signUpAuthenticator";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        Intent enterItent = getIntent();
        Authenticator authenticator = enterItent.getParcelableExtra(AUTHENTICATOR);

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
            if (signUpResult.getError() != null) {
                showSignUpFailed(signUpResult.getErrorInt());
            } else {
                setResult(RESULT_OK);
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                signUpViewModel.signUpDataChanged(
                        usernameEditText.getText().toString(),
                        emailEditText.getText().toString(),
                        passwordEditText.getText().toString(),
                        password2EditText.getText().toString());
            }
        };
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

    private void showSignUpFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_LONG).show();
    }
}
