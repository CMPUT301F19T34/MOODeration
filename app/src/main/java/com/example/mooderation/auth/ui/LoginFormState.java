package com.example.mooderation.auth.ui;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable @StringRes private Integer emailError;
    @Nullable @StringRes private Integer passwordError;

    /**
     * Create a LoginFormState that stores a possible email error and a possible password error.
     *
     * @param emailError Email error text, or null if no error
     * @param passwordError Password error text, or null if no error
     */
    LoginFormState(@Nullable @StringRes Integer emailError, @Nullable @StringRes Integer passwordError) {
        this.emailError = emailError;
        this.passwordError = passwordError;
    }

    /**
     * Returns the email error text, or null if none
     *
     * @return Email error text, or null
     */
    @Nullable
    @StringRes
    Integer getEmailError() {
        return emailError;
    }

    /**
     * Returns the password error text, or null if none
     *
     * @return Password error text, or null
     */
    @Nullable
    @StringRes
    Integer getPasswordError() {
        return passwordError;
    }

    /**
     * Test if all data in the form is in a valid state, i.e. if the form may be submitted.
     *
     * @return True iff both email and password errors are null.
     */
    boolean isDataValid() {
        return getEmailError() == null && getPasswordError() == null;
    }
}
