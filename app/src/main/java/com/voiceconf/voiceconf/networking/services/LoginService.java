package com.voiceconf.voiceconf.networking.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.voiceconf.voiceconf.storage.models.User;

/**
 * Handles login and registration processes.
 */
public class LoginService {
    private static final String AUTH_DATA_END = "\"}";
    private static final String DEFAULT_PASSWORD = "voiceConf";

    /**
     * Checks if the user with given {@code id} exist in the database. if exists will be logged in,
     * otherwise registration process starts.
     *
     * @param callback Allows to notify the UI, the registration process is finished.
     * @param id       User Id of the created {@link ParseUser}.
     * @param accId    Google account Id.
     * @param email    The users email address.
     * @param name     The users full name.
     * @param uri      The users avatar image url.
     * @see LoginService#registerNewUser(LoginCallback, String, String, String, String, String)
     */
    public static void login(@NonNull LoginCallback callback, @NonNull String id, @NonNull String accId, @NonNull String email, @NonNull String name, @Nullable String uri) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo(User.EMAIL, email);
        userQuery.getFirstInBackground((parseUser, e) -> {
            if (parseUser == null) {
                registerNewUser(callback, id, accId, email, name, uri);
            } else {
                // User exists => Start Log In request to parse.com
                ParseUser.logInInBackground(name, DEFAULT_PASSWORD, (parseUser1, loginError) -> {
                    if (loginError == null) {
                        // Existing user logged in successfully starting the main activity
                        callback.onSuccess();
                    } else {
                        callback.onFailure(loginError);
                    }
                });
            }
        });
    }

    /**
     * Creates and registers a new user.
     *
     * @param callback Allows to notify the UI, the registration process is finished.
     * @param id       User Id of the created {@link ParseUser}.
     * @param accId    Google account Id.
     * @param email    The users email address.
     * @param name     The users full name.
     * @param uri      The users avatar image url.
     */
    private static void registerNewUser(@NonNull LoginCallback callback, @NonNull String id, @NonNull String accId, @NonNull String email, @NonNull String name, @Nullable String uri) {
        // User does not exists => creating new user
        User user = new User();
        user.setUsername(name);
        user.setEmail(email);
        user.setPassword(DEFAULT_PASSWORD); // This field must be set to create a user

        // Start sign up request to parse.com
        user.signUpInBackground(signUpError -> {
            if (signUpError == null) {
                User registeredUser = User.createWithoutData(User.class, User.getCurrentUser().getObjectId());
                User.setAvatar(registeredUser, uri);
                User.setUserData(registeredUser, id + accId + AUTH_DATA_END);

                registeredUser.saveInBackground(saveError -> {
                    if (saveError == null) {
                        // Creating user was successful starting the main activity
                        callback.onSuccess();
                    } else {
                        callback.onFailure(saveError);
                    }
                });
            } else {
                callback.onFailure(signUpError);
            }
        });
    }
}