package com.voiceconf.voiceconf.networking.services;

/**
 * Basic interface to handle login and registration processes.
 */
public interface LoginCallback {

    void onSuccess();

    void onFailure(Exception e);
}
