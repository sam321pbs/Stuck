package com.example.sammengistu.stuck.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.ServerValue;

import com.example.sammengistu.stuck.StuckConstants;

import java.util.HashMap;


public class StuckPostSimple {

    private String email;
    private String question;
    private String location;
    private String choiceOne;
    private String choiceTwo;
    private String choiceThree;
    private String choiceFour;
    private int choiceOneVotes;
    private int choiceTwoVotes;
    private int choiceThreeVotes;
    private int choiceFourVotes;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;
    private long dateTimeStamp;
    private DatabaseReference mDatabaseReference;

    public StuckPostSimple() {
    }

    public StuckPostSimple(String email, String question, String location, String choiceOne, String choiceTwo,
                           String choiceThree, String choiceFour, int choiceOneVotes,
                           int choiceTwoVotes, int choiceThreeVotes, int choiceFourVotes,
                           HashMap<String, Object> timestampCreated, long dateTimeStamp) {
        this.email = email;
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.location = location;
        this.choiceThree = choiceThree;
        this.choiceFour = choiceFour;
        this.choiceOneVotes = choiceOneVotes;
        this.choiceTwoVotes = choiceTwoVotes;
        this.choiceThreeVotes = choiceThreeVotes;
        this.choiceFourVotes = choiceFourVotes;
        this.timestampCreated = timestampCreated;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

        this.dateTimeStamp = dateTimeStamp;

    }

    public DatabaseReference getDatabaseReference() {
        return mDatabaseReference;
    }

    public void setDatabaseReference(DatabaseReference databaseReference) {
        mDatabaseReference = databaseReference;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public long getDateTimeStamp() {
        return dateTimeStamp;
    }

    public void setDateTimeStamp(long dateTimeStamp) {
        this.dateTimeStamp = dateTimeStamp;
    }

    public String getEmail() {
        return email;
    }

    public String getLocation() {
        return location;
    }

    public String getChoiceThree() {
        return choiceThree;
    }

    public String getChoiceFour() {
        return choiceFour;
    }

    public String getQuestion() {
        return question;
    }

    public String getChoiceOne() {
        return choiceOne;
    }

    public String getChoiceTwo() {
        return choiceTwo;
    }

    public int getChoiceOneVotes() {
        return choiceOneVotes;
    }

    public void setChoiceOneVotes(int choiceOneVotes) {
        this.choiceOneVotes = choiceOneVotes;
    }

    public int getChoiceTwoVotes() {
        return choiceTwoVotes;
    }

    public void setChoiceTwoVotes(int choiceTwoVotes) {
        this.choiceTwoVotes = choiceTwoVotes;
    }

    public int getChoiceThreeVotes() {
        return choiceThreeVotes;
    }

    public void setChoiceThreeVotes(int choiceThreeVotes) {
        this.choiceThreeVotes = choiceThreeVotes;
    }

    public int getChoiceFourVotes() {
        return choiceFourVotes;
    }

    public void setChoiceFourVotes(int choiceFourVotes) {
        this.choiceFourVotes = choiceFourVotes;
    }

    public HashMap<String, Object> getTimestampCreated() {
        return timestampCreated;
    }

    @Exclude
    public long getTimestampLastChangedLong() {

        return (long) timestampLastChanged.get(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @Exclude
    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
