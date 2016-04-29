package com.example.sammengistu.stuck.model;

/**
 * Created by SamMengistu on 4/29/16.
 */
public class VoteChoice {

    private String mChoice;
    private boolean mVotedFor;
    private int mVotes;

    public VoteChoice(String choice, boolean votedFor, int votes) {
        mChoice = choice;
        mVotedFor = votedFor;
        mVotes = votes;
    }

    public String getChoice() {
        return mChoice;
    }

    public void setChoice(String choice) {
        mChoice = choice;
    }

    public boolean isVotedFor() {
        return mVotedFor;
    }

    public void setVotedFor(boolean votedFor) {
        mVotedFor = votedFor;
    }

    public int getVotes() {
        return mVotes;
    }

    public void setVotes(int votes) {
        mVotes = votes;
    }
}
