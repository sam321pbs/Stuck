package com.example.sammengistu.stuck.model;


import java.util.UUID;

public class StuckPost {

    private UUID mUUID;
    private String mQuestion;
    private String mChoice1;
    private String mChoice2;
    private String mChoice3;
    private String mChoice4;

    private String mStuckPostLocation;

    public StuckPost (String question, String choice1,
                       String choice2, String choice3, String choice4,
                      String stuckPostLocation){

        mUUID = UUID.randomUUID();
        mQuestion = question;
        mChoice1 = choice1;
        mChoice2 = choice2;
        mChoice3 = choice3;
        mChoice4 = choice4;
        mStuckPostLocation = stuckPostLocation;
    }

    public UUID getUUID() {
        return mUUID;
    }

    public String getQuestion() {
        return mQuestion;
    }

    public String getChoice1() {
        return mChoice1;
    }

    public String getChoice2() {
        return mChoice2;
    }

    public String getChoice3() {
        return mChoice3;
    }

    public String getChoice4() {
        return mChoice4;
    }

    public String getStuckPostLocation() {
        return mStuckPostLocation;
    }
}
