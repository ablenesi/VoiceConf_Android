package com.voiceconf.voiceconf.networking.services;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.voiceconf.voiceconf.storage.models.Friend;
import com.voiceconf.voiceconf.storage.models.User;
import com.voiceconf.voiceconf.storage.nonpersistent.VoiceConfApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Attila Blenesi on 27 Dec 2015
 */
public class FriendService {

    /**
     * Use this method to get all friends from Parse
     *
     * @param callback If null the data will be updated in the DataManager else it will be returned,
     *                 to the callback, and if the query failed the exception will be passed to the
     *                 onFailure method.
     */
    public static void getFriends(@Nullable final ParseGetCallback<Friend> callback) {
        // Preparing query
        ParseQuery<Friend> fromCurrent = ParseQuery.getQuery(Friend.class);
        fromCurrent.whereEqualTo(Friend.USER_ID, ParseUser.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));

        ParseQuery<Friend> fromOther = ParseQuery.getQuery(Friend.class);
        fromOther.whereEqualTo(Friend.FRIEND_ID, ParseUser.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));

        List<ParseQuery<Friend>> queries = new ArrayList<>();
        queries.add(fromCurrent);
        queries.add(fromOther);

        ParseQuery<Friend> friendQuery = ParseQuery.or(queries);
        friendQuery.whereEqualTo(Friend.ARCHIVED, false);
        friendQuery.include(Friend.FRIEND_ID);
        friendQuery.include(Friend.USER_ID);

        // Running query in background
        friendQuery.findInBackground((objects, e) -> {
            if (e == null) {
                if (callback == null) {
                    VoiceConfApplication.sDataManager.setFriends(objects);
                } else {
                    callback.onResult(objects);
                }
            } else {
                if (callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * User this method to add a new friend via email.
     *
     * @param context Needed for toast to notify the user.
     * @param email   The email address for the friend
     */
    public static void addNewFriend(@NonNull final Context context, @NonNull String email) {
        if (email.equals(ParseUser.getCurrentUser().getEmail())) {
            Toast.makeText(context, "You cant add yourself as friend", Toast.LENGTH_LONG).show();
            return;
        }
        for (Friend friend : VoiceConfApplication.sDataManager.getFriends()) {
            if (friend.getFriend().getEmail().equals(email) || friend.getUser().getEmail().equals(email)) {
                Toast.makeText(context, "You already have a connection.", Toast.LENGTH_LONG).show();
                return;
            }
        }
        final Friend friend = new Friend();
        ParseQuery<ParseUser> userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo(User.EMAIL, email);
        userQuery.getFirstInBackground((parseUser, e) -> {
            if (parseUser != null) {
                friend.setFriend(parseUser.getObjectId());
                friend.setUser(User.getCurrentUser().getObjectId());
                friend.setPending(true);
                friend.setArchived(false);
                friend.saveInBackground(e1 -> {
                    if (e1 == null) {
                        Toast.makeText(context, "Friend added successfully", Toast.LENGTH_LONG).show();
                        getFriends(null);
                    }
                });
            }
        });
    }

    public static void archiveFriend(@NonNull Friend friend, boolean archive) {
        Friend object = Friend.createWithoutData(Friend.class, friend.getObjectId());
        object.setArchived(archive);
        object.saveInBackground(e -> getFriends(null));
    }

    public static void acceptFriend(@NonNull Friend friend) {
        Friend object = Friend.createWithoutData(Friend.class, friend.getObjectId());
        object.setPending(false);
        object.saveInBackground(e -> getFriends(null));
    }
}
