package com.example.mooderation.auth.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.mooderation.auth.base.IAuthenticator;

import java.lang.reflect.InvocationTargetException;

/**
 * Allows creation of ViewModels that require IAuthenticator instances in their constructors.
 * Recommended usage:
 *
 * <pre>
 *     IAuthenticator authenticator = ...;
 *     ViewModelAuthenticationFactory f = new ViewModelAuthenticationFactory(authenticator);
 *     MyViewModel myViewModel = ViewModelProviders.of(this, f).get(MyViewModel.class);
 * </pre>
 */
class ViewModelAuthenticationFactory implements ViewModelProvider.Factory {
    private IAuthenticator authenticator;

    /**
     * Creates a ViewModelAuthenticationFactory with the IAuthenticator instance it will pass to
     * the constructors of the ViewModels it creates.
     *
     * @param authenticator IAuthenticator instance to give to created ViewModels
     */
    ViewModelAuthenticationFactory(IAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    /**
     * Creates a ViewModel of type T. Do not call manually, used internally by
     * ViewModelProviders.of().get()
     */
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
