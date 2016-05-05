package com.example.sammengistu.stuck.model;

import com.example.sammengistu.stuck.StuckConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class StuckPostSimple {

    private String email;
    private String question;
    private String choiceOne;
    private String choiceTwo;
    private String location;
    private String choiceThree;
    private String choiceFour;
    private int choiceOneVotes;
    private int choiceTwoVotes;
    private int choiceThreeVotes;
    private int choiceFourVotes;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    public StuckPostSimple() {
    }

    public StuckPostSimple(String email, String question, String location, String choiceOne, String choiceTwo,
                           int choiceOneVotes, int choiceTwoVotes,HashMap<String, Object> timestampCreated) {
        this.email = email;
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = "";
        this.choiceFour = "";
        this.location = location;
        this.choiceOneVotes = choiceOneVotes;
        this.choiceTwoVotes = choiceTwoVotes;
        this.choiceThreeVotes = 0;
        this.choiceFourVotes = 0;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

    }

    public StuckPostSimple(String email, String question, String location, String choiceOne, String choiceTwo,
                           String choiceThree, int choiceOneVotes, int choiceTwoVotes,
                           int choiceThreeVotes, HashMap<String, Object> timestampCreated) {
        this.email = email;
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.location = location;
        this.choiceThree = choiceThree;
        this.choiceFour = "";
        this.choiceOneVotes = choiceOneVotes;
        this.choiceTwoVotes = choiceTwoVotes;
        this.choiceThreeVotes = choiceThreeVotes;
        this.choiceFourVotes = 0;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

    }

    public StuckPostSimple(String email, String question, String location, String choiceOne, String choiceTwo,
                           String choiceThree, String choiceFour, int choiceOneVotes,
                           int choiceTwoVotes, int choiceThreeVotes, int choiceFourVotes,
                           HashMap<String, Object> timestampCreated) {
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
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;

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

    @JsonIgnore
    public long getTimestampLastChangedLong() {

        return (long) timestampLastChanged.get(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }

    @JsonIgnore
    public long getTimestampCreatedLong() {
        return (long) timestampLastChanged.get(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP);
    }
}
