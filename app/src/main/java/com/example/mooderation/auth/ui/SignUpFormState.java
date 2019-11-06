package com.example.mooderation.auth.ui;


import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

/**
 * Data validation state of the sign-up form
 */
class SignUpFormState {
    @Nullable @StringRes private Integer usernameError;
    @Nullable @StringRes private Integer emailError;
    @Nullable @StringRes private Integer passwordError;
    @Nullable @StringRes private Integer password2Error;

    SignUpFormState(@Nullable @StringRes Integer usernameError,
                    @Nullable @StringRes Integer emailError,
                    @Nullable @StringRes Integer passwordError,
                    @Nullable @StringRes Integer password2Error) {
        this.usernameError = usernameError;
        this.emailError = emailError;
        this.passwordError = passwordError;
        this.password2Error = password2Error;
    }

    /**
     * Returns the username error tet, or null if none
     *
     * @return Username error text, or null
     */
    @Nullable @StringRes
    Integer getUsernameError() {
        return usernameError;
    }

    /**
     * Returns the email error tet, or null if none
     *
     * @return Email error text, or null
     */
    @Nullable @StringRes
    Integer getEmailError() {
        return emailError;
    }

    /**
     * Returns the password error tet, or null if none
     *
     * @return Password error text, or null
     */
    @Nullable @StringRes
    Integer getPasswordError() {
        return passwordError;
    }

    /**
     * Returns the verification-password error tet, or null if none
     *
     * @return Verification-password error text, or null
     */
    @Nullable @StringRes
    Integer getPassword2Error() {
        return password2Error;
    }

    /**
     * Test if all data in the form is in a valid state, i.e. if the form may be submitted.
     *
     * @return True iff username, email, password and verification password errors are null.
     */
    boolean isDataValid() {
        return getUsernameError() == null && getEmailError() == null &&
                getPasswordError() == null && getPassword2Error() == null;
    }
}
