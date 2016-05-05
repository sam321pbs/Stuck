package com.example.sammengistu.stuck.model;

import java.util.HashMap;

/**
 * Created by SamMengistu on 5/4/16.
 */
public class User {

    private String email;
    private HashMap<String, Object> timestampJoined;

    public User() {
    }

    public User(String email, HashMap<String, Object> timestampJoined) {
        this.email = email;
        this.timestampJoined = timestampJoined;
    }

    public String getEmail() {
        return email;
    }

    public HashMap<String, Object> getTimestampJoined() {
        return timestampJoined;
    }
}
