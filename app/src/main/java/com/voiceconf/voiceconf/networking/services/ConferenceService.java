package com.voiceconf.voiceconf.networking.services;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.voiceconf.voiceconf.storage.models.Conference;
import com.voiceconf.voiceconf.storage.models.Invite;
import com.voiceconf.voiceconf.storage.nonpersistent.VoiceConfApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Attila Blenesi on 01 Jan 2016
 */
public class ConferenceService {

    public static void getConferences(@Nullable final ParseGetCallback<Conference> callback) {
        ParseQuery<Invite> inviteRequest = ParseQuery.getQuery(Invite.class);
        inviteRequest.whereEqualTo(Invite.INVITED, ParseUser.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));
        inviteRequest.include(Invite.CONFERENCE);
        inviteRequest.findInBackground((objects, e) -> {
            if(e == null) {
                List<String> conferenceIds = new ArrayList<>();
                for (Invite invite : objects) {
                    conferenceIds.add(invite.getConferenceId());
                }

                ParseQuery<Conference> otherRequests = ParseQuery.getQuery(Conference.class);
                otherRequests.whereContainedIn("objectId", conferenceIds);

                ParseQuery<Conference> myRequest = ParseQuery.getQuery(Conference.class);
                myRequest.whereEqualTo(Conference.OWNER, ParseUser.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));

                List<ParseQuery<Conference>> queries = new ArrayList<>();
                queries.add(otherRequests);
                queries.add(myRequest);

                ParseQuery<Conference> conferenceParseQuery = ParseQuery.or(queries);
                conferenceParseQuery.include(Conference.OWNER);
                conferenceParseQuery.include(Conference.INVITEES);
                conferenceParseQuery.orderByDescending("createdAt");
                conferenceParseQuery.findInBackground((objects1, e1) -> {
                    if (e1 == null) {
                        if (callback == null) {
                            VoiceConfApplication.sDataManager.setConferences(objects1);
                        } else {
                            callback.onResult(objects1);
                        }
                    } else {
                        if (callback != null) {
                            callback.onFailure(e1);
                        }
                    }
                });
            }else{
                if(callback != null) {
                    callback.onFailure(e);
                }
            }
        });
    }

    /**
     * Use this method to create a Conference.
     *
     * @param callback   The callback to notify the user.
     * @param title      The Conference title.
     * @param inviteeIds The list of invited users.
     */
    public static void saveConferenceWithInvites(@NonNull final ConferenceCallback callback, @NonNull String title, @NonNull final List<String> inviteeIds) {
        final List<Invite> invites = new ArrayList<>();
        final Conference conference = new Conference();
        conference.setOwner();
        conference.setTitle(title);

        conference.saveInBackground(e -> {
            if (e == null) {
                // Creating invites
                inviteeIds.add(ParseUser.getCurrentUser().getObjectId());
                for (String id : inviteeIds) {
                    Invite invite = new Invite();
                    invite.setConference(conference.getObjectId());
                    invite.setInvited(id);
                    invites.add(invite);
                    Collections.reverse(invites);
                }
                ParseObject.saveAllInBackground(invites, e1 -> {
                    getConferences(null);
                    if (e1 == null) {
                        conference.putInvites(invites);
                        conference.saveInBackground();
                        callback.onSuccess("Invitees where notified successfully.", conference);
                    } else {
                        callback.onFailure(e1, "Something went wrong while sending invites.");
                    }
                });
                callback.onSuccess("Conference created successfully.", null);
            } else {
                callback.onFailure(e, "Something went wrong while creating the conference.");
            }
        });
    }
}
