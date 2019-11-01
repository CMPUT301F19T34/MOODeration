package com.example.mooderation.auth;


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
    public Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    public Integer getEmailError() {
        return emailError;
    }

    @Nullable
    public Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    public Integer getPassword2Error() {
        return password2Error;
    }

    boolean isDataValid() {
        return getUsernameError() == null && getEmailError() == null &&
                getPasswordError() == null && getPassword2Error() == null;
    }
}
