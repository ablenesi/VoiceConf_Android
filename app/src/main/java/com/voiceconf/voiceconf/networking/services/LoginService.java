package com.voiceconf.voiceconf.networking.services;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.voiceconf.voiceconf.storage.models.User;

/**
 * Created by Zoltán Benedek on 12/30/2015.
 * Edited by Attila Blenesi
 */
public class LoginService {
    private static final String AUTH_DATA_END = "\"}";
    private static final String DEFAULT_PASSWORD = "voiceConf";

    public static void login(final LoginCallback callback, final String name, final String uri, final String id, final String accId, final String email) {
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo(User.EMAIL, email);
        userQuery.getFirstInBackground((parseUser, e) -> {
            if (parseUser == null) {
                // User does not exists => creating new user
                User user = new User();
                user.setUsername(name);
                user.setEmail(email);
                user.setPassword(DEFAULT_PASSWORD); // This field must be set to create a user

                // Start sign up request to parse.com
                user.signUpInBackground(e1 -> {
                    if (e1 == null) {
                        User registeredUser = User.createWithoutData(User.class, User.getCurrentUser().getObjectId());
                        User.setAvatar(registeredUser, uri);
                        User.setUserData(registeredUser, id + accId + AUTH_DATA_END);

                        registeredUser.saveInBackground(e11 -> {
                            if (e11 == null) {
                                // Creating user was successful starting the main activity
                                callback.onSuccess();
                            } else {
                                callback.onFailure(e11);
                            }
                        });
                    } else {
                        callback.onFailure(e1);
                    }
                });
            } else {
                // User exists => Start Log In request to parse.com
                ParseUser.logInInBackground(name, DEFAULT_PASSWORD, (parseUser1, e1) -> {
                    if (e1 == null) {
                        // Existing user logged in successfully starting the main activity
                        callback.onSuccess();
                    } else {
                        callback.onFailure(e1);
                    }
                });
            }
        });
    }
}