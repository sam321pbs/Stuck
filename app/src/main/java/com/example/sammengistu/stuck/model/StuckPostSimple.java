package com.example.sammengistu.stuck.model;

import com.example.sammengistu.stuck.StuckConstants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.firebase.client.ServerValue;

import java.util.HashMap;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class StuckPostSimple {

    private String question;
    private String choiceOne;
    private String choiceTwo;
    private String location;
    private String choiceThree;
    private String choiceFour;
    private HashMap<String, Object> timestampCreated;
    private HashMap<String, Object> timestampLastChanged;

    public StuckPostSimple() {
    }

    public StuckPostSimple(String question, String choiceOne, String choiceTwo,
                           String choiceThree, String location, HashMap<String, Object> timestampCreated) {
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = choiceThree;
        this.location = location;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    public StuckPostSimple(String question, String choiceOne,
                           String choiceTwo, String choiceThree, String choiceFour, String location,
                           HashMap<String, Object> timestampCreated) {

        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = choiceThree;
        this.choiceFour = choiceFour;
        this.location = location;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
    }

    public StuckPostSimple(String question, String choiceOne, String choiceTwo, String location,
                           HashMap<String, Object> timestampCreated) {
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = "";
        this.choiceFour = "";
        this.location = location;

        this.timestampCreated = timestampCreated;
        HashMap<String, Object> timestampNowObject = new HashMap<String, Object>();
        timestampNowObject.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);
        this.timestampLastChanged = timestampNowObject;
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
