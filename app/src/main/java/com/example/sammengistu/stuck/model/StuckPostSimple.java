package com.example.sammengistu.stuck.model;

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

    public StuckPostSimple() {
    }

    public StuckPostSimple(String question, String choiceOne, String choiceTwo, String choiceThree, String location) {
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.location = location;
        this.choiceThree = choiceThree;
    }

    public StuckPostSimple(String question, String choiceOne,
                           String choiceTwo, String choiceThree, String choiceFour, String location) {
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.location = location;
        this.choiceThree = choiceThree;
        this.choiceFour = choiceFour;
    }

    public StuckPostSimple(String question, String choiceOne, String choiceTwo, String location) {
        this.question = question;
        this.choiceOne = choiceOne;
        this.choiceTwo = choiceTwo;
        this.choiceThree = "";
        this.choiceFour = "";
        this.location = location;
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
}
