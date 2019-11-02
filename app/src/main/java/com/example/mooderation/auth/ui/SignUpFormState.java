package com.example.mooderation.auth.ui;


import androidx.annotation.Nullable;

class SignUpFormState {
    @Nullable private Integer usernameError;
    @Nullable private Integer emailError;
    @Nullable private Integer passwordError;
    @Nullable private Integer password2Error;

    SignUpFormState(@Nullable Integer usernameError, @Nullable Integer emailError,
                    @Nullable Integer passwordError, @Nullable Integer password2Error) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.password2Error = password2Error;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getPassword2Error() {
        return password2Error;
    }

    boolean isDataValid() {
        return getUsernameError() == null && getEmailError() == null &&
                getPasswordError() == null && getPassword2Error() == null;
    }
}
