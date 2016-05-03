package com.example.sammengistu.stuck.model;


import java.util.UUID;

public class StuckPost {

    private UUID mUUID;
    private String mQuestion;
    private Choice mChoice1;
    private Choice mChoice2;
    private Choice mChoice3;
    private Choice mChoice4;

    private String mStuckPostLocation;

    public StuckPost() {
    }

    public StuckPost (String question, Choice choice1,
                      Choice choice2, Choice choice3, Choice choice4,
                      String stuckPostLocation){

//        mUUID = UUID.randomUUID();
        mQuestion = question;
        mChoice1 = choice1;
        mChoice2 = choice2;
        mChoice3 = choice3;
        mChoice4 = choice4;
        mStuckPostLocation = stuckPostLocation;
    }

    public StuckPost( String question, Choice choice1, Choice choice2, String stuckPostLocation) {
//        mUUID = UUID.randomUUID();
        mQuestion = question;
        mChoice1 = choice1;
        mChoice2 = choice2;
        mChoice3 = new Choice("", 0);
        mChoice4 = new Choice("", 0);
        mStuckPostLocation = stuckPostLocation;
    }

    public StuckPost(String question, Choice choice1, Choice choice2, Choice choice3, String stuckPostLocation) {
        mQuestion = question;
        mChoice1 = choice1;
        mChoice2 = choice2;
        mChoice3 = choice3;
        mChoice4 = new Choice("", 0);
        mStuckPostLocation = stuckPostLocation;
    }

//    public UUID getUUID() {
//        return mUUID;
//    }

    public String getQuestion() {
        return mQuestion;
    }

    public Choice getChoice1() {
        return mChoice1;
    }

    public Choice getChoice2() {
        return mChoice2;
    }

    public Choice getChoice3() {
        return mChoice3;
    }

    public Choice getChoice4() {
        return mChoice4;
    }

    public String getStuckPostLocation() {
        return mStuckPostLocation;
    }
}
