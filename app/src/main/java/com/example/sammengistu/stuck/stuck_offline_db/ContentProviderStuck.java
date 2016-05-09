package com.example.sammengistu.stuck.stuck_offline_db;

import com.example.sammengistu.stuck.StuckConstants;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

public class ContentProviderStuck extends ContentProvider {

    private StuckOfflineDBHelper dbHelper;
    //specific for our our app, will be specified in manifest
    public static final String STUCK_AUTHORITY = "stuckProviderAuthorities";
    public static final Uri CONTENT_URI = Uri.parse("content://" + STUCK_AUTHORITY);

    @Override
    public boolean onCreate() {
        dbHelper = new StuckOfflineDBHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        return database.rawQuery(
            StuckConstants.SELECT_FROM + table, null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        String tableUri = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        long value = database.insert(tableUri, null, values);
        return Uri.withAppendedPath(CONTENT_URI, String.valueOf(value));
    }

    @Override
    public int delete(Uri uri, String where, String[] args) {
        String table = getTableName(uri);
        SQLiteDatabase dataBase = dbHelper.getWritableDatabase();
        return dataBase.delete(table, where, args);
    }

    @Override
    public int update(Uri uri, ContentValues values, String whereClause, String[] whereArgs) {
        String table = getTableName(uri);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        return database.update(table, values, whereClause, whereArgs);
    }

    public static String getTableName(Uri uri) {
        String value = uri.getPath();
        value = value.replace("/", "");//we need to remove '/'
        return value;
    }
}
