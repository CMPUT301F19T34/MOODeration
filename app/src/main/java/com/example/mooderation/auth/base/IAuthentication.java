package com.example.mooderation.auth.base;

/**
 * Interface for authentication credentials returned by successfully logging in with an
 * IAuthenticator. Because we don't know what type of credentials any specific IAuthenticator
 * instance will use, this is empty.
 *
 * TODO: a completely empty interface seems like bad design -- it's really no better than using an Object. Is there a better way?
 */
public interface IAuthentication {

}
