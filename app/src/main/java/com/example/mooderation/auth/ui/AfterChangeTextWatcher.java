package com.example.mooderation.auth.ui;

import android.text.TextWatcher;

/**
 * TextWatcher with default (empty) beforeTextChanged and onTextChanged methods, thereby allowing
 * one to use Java8-style lambda expressions like:
 * <pre>
 * AfterChangeTextWatcher watcher = s -> doSomethingAfterTextChanges();
 * myText.addTextChangedListener(watcher);
 * </pre>
 */
interface AfterChangeTextWatcher extends TextWatcher {
    @Override
    public default void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public default void onTextChanged(CharSequence s, int start, int before, int count) {}
}
