package com.example.sammengistu.stuck.stuck_offline_db;


import com.example.sammengistu.stuck.StuckConstants;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StuckOfflineDBHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "Stuck_Database_offline";
    private static final int DB_VERSION = 1;
    private static final String CREATE_DATABASE =
        "CREATE TABLE " + StuckConstants.TABLE_OFFLINE_POST + " ("
            + StuckConstants.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + StuckConstants.COLUMN_EMAIL + " REAL, "
            + StuckConstants.COLUMN_QUESTION + " REAL, "
            + StuckConstants.COLUMN_LOCATION + " REAL, "
            + StuckConstants.COLUMN_CHOICE_ONE + " REAL, "
            + StuckConstants.COLUMN_CHOICE_TWO + " REAL, "
            + StuckConstants.COLUMN_CHOICE_THREE + " REAL, "
            + StuckConstants.COLUMN_CHOICE_FOUR + " REAL, "
            + StuckConstants.COLUMN_MOST_RECENT_POST + " REAL)";

    public StuckOfflineDBHelper (Context helperContext) {
        super(helperContext, DATABASE_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(CREATE_DATABASE);
    }
}
