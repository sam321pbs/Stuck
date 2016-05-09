package com.example.sammengistu.stuck.model;

import java.util.HashMap;

public class User {

    private String email;
    private HashMap<String, Object> timestampJoined;
    private boolean hasLoggedInWithTempPassword;

    public User() {
    }

    public User(String email, HashMap<String, Object> timestampJoined) {
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithTempPassword = false;
    }

    public User(String email, HashMap<String, Object> timestampJoined, boolean hasLoggedInWithTempPassword) {
        this.email = email;
        this.timestampJoined = timestampJoined;
        this.hasLoggedInWithTempPassword = hasLoggedInWithTempPassword;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }

    public boolean getHasLoggedInWithTempPassword() {
        return hasLoggedInWithTempPassword;
    }

    public void setHasLoggedInWithTempPassword(boolean hasLoggedInWithTempPassword) {
        this.hasLoggedInWithTempPassword = hasLoggedInWithTempPassword;
    }
}
