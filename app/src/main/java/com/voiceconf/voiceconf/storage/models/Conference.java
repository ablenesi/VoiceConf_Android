package com.voiceconf.voiceconf.storage.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.List;

/**
 * Created by Attila Blenesi on 01 Jan 2016
 */
@ParseClassName("Conference")
public class Conference extends ParseObject{

    //region CONSTANTS
    public static final String OWNER = "owner";
    public static final String TITLE = "title";
    public static final String CLOSED = "closed";
    public static final String INVITEES = "invitees";
    //endregion

    //region GETTERS/SETTERS
    public ParseUser getOwner(){
        return getParseUser(OWNER);
    }

    public void setOwner(){
        put(OWNER, ParseUser.createWithoutData(ParseUser.class, ParseUser.getCurrentUser().getObjectId()));
    }

    public String getTitle(){
        return getString(TITLE);
    }

    public void setTitle(String title){
        put(TITLE, title);
    }

    public boolean isClosed(){
        return getBoolean(CLOSED);
    }

    public void setClosed(boolean closed){
        put(CLOSED, closed);
    }

    public List<Invite> getInvitees(){
        return getList(INVITEES);
    }

    public void putInvites(List<Invite> invites){
        put(INVITEES, invites);
    }

//    public void putInvites(List<String> inviteeIds){
//        List<Invite> invites = new ArrayList<>();
//        for (String id : inviteeIds){
//            invites.add(Invite.createWithoutData(Invite.class, id));
//        }
//        put(INVITEES, invites);
//    }
    //endregion
}
