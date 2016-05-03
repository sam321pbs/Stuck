package com.example.sammengistu.stuck.model;

/**
 * Created by SamMengistu on 4/27/16.
 */
public class Choice {
    private String mChoice;
    private int mVote;

    public Choice() {
    }

    public Choice(String choice, int vote) {
        mChoice = choice;
        mVote = vote;
    }

    public String getChoice() {
        return mChoice;
    }

    public void setChoice(String choice) {
        mChoice = choice;
    }

    public int getVote() {
        return mVote;
    }

    public void setVote(int vote) {
        mVote = vote;
    }
}
