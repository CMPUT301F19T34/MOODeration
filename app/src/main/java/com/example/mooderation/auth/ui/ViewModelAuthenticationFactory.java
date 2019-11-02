package com.example.mooderation.auth.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mooderation.auth.base.IAuthenticator;

import java.lang.reflect.InvocationTargetException;

class ViewModelAuthenticationFactory implements ViewModelProvider.Factory {
    private IAuthenticator authenticator;

    ViewModelAuthenticationFactory(IAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        try {
            return modelClass.getConstructor(IAuthenticator.class)
                    .newInstance(authenticator);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Cannot create an instance of " + modelClass, e);
        }
    }
}
