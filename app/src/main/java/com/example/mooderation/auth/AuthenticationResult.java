package com.example.mooderation.auth;

import android.util.Log;

import androidx.annotation.Nullable;

import com.example.mooderation.R;
import com.google.firebase.auth.UserInfo;

class AuthenticationResult {
    @Nullable
    private UserInfo success;
    @Nullable
    private AuthenticationError error;

    AuthenticationResult(@Nullable AuthenticationError error) {
        this.error = error;
    }

    AuthenticationResult(@Nullable UserInfo success) {
        this.success = success;
    }

    @Nullable
    UserInfo getSuccess() {
        return success;
    }

    @Nullable
    AuthenticationError getError() {
        return error;
    }

    @Nullable
    Integer getErrorInt() {
        if (getError() == null) {
            return null;
        }

        Log.d("AuthenticationResult", getError().toString());

        if (getError() instanceof InvalidEmailError) return R.string.invalid_email;
        if (getError() instanceof InvalidUsernameError) return R.string.invalid_username;
        if (getError() instanceof InvalidPasswordError) return R.string.invalid_password;

        return R.string.auth_failed;
    }
}
