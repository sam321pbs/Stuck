package com.example.sammengistu.stuck.stuck_offline_db;

import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.StuckPostSimple;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class StuckDBConverter {

    /**
     * Converts a movie into a ContentValues Object
     */
    public static ContentValues insertStuckPostToDB(StuckPostSimple stuckPostSimple, String mostRecentPost) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(StuckConstants.COLUMN_EMAIL, stuckPostSimple.getEmail());
        contentValues.put(StuckConstants.COLUMN_QUESTION, stuckPostSimple.getQuestion());
        contentValues.put(StuckConstants.COLUMN_LOCATION, stuckPostSimple.getLocation());
        contentValues.put(StuckConstants.COLUMN_CHOICE_ONE, stuckPostSimple.getChoiceOne());
        contentValues.put(StuckConstants.COLUMN_CHOICE_TWO, stuckPostSimple.getChoiceTwo());
        contentValues.put(StuckConstants.COLUMN_CHOICE_THREE, stuckPostSimple.getChoiceThree());
        contentValues.put(StuckConstants.COLUMN_CHOICE_FOUR, stuckPostSimple.getChoiceFour());
        contentValues.put(StuckConstants.COLUMN_MOST_RECENT_POST, mostRecentPost);

        return contentValues;
    }

    /**
     * Converts a cursor to a list of movie objects
     *
     * @param cursor - where to get all the movies from
     * @return - list of movies
     */
    public static List<StuckPostSimple> getStuckPostsFromDb(Cursor cursor) {

        List<StuckPostSimple> stuckPostSimples = new ArrayList<>();

        cursor.moveToFirst();

        //Got from http://stackoverflow.com/questions/10111166/get-all-rows-from-sqlite
        if (cursor.moveToFirst()) {

            while (!cursor.isAfterLast()) {
                String stuckEmail = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_EMAIL));

                String stuckQuestion = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_QUESTION));

                String stuckLocation = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_LOCATION));

                String stuckChoiceOne = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_ONE));

                String stuckChoiceTwo = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_TWO));

                String stuckChoiceThree = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_THREE));

                String stuckChoiceFour = cursor.getString(cursor
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_FOUR));

                StuckPostSimple stuckPostSimple = new StuckPostSimple(
                    stuckEmail, stuckQuestion, stuckLocation, stuckChoiceOne, stuckChoiceTwo,
                    stuckChoiceThree, stuckChoiceFour, 0, 0,0,0, new HashMap<String, Object>()
                   );

                //TODO: new to change hashmap to timestamp
                stuckPostSimples.add(stuckPostSimple);
                cursor.moveToNext();
            }
        }
        return stuckPostSimples;
    }
}
