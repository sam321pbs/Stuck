package com.example.sammengistu.stuck;

import com.example.sammengistu.stuck.activities.StuckLoginActivity;
import com.example.sammengistu.stuck.activities.StuckSignUpActivity;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.stuck_offline_db.ContentProviderStuck;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Date;
import java.util.HashMap;

public class SinglePostProvider extends AppWidgetProvider {

    private static final String TAG = "SinglePostProvider55";
    private Context mContext;
    private StuckPostSimple mStuckPostSimple;

    @Override
    public void onReceive(Context context, Intent intent) {

        super.onReceive(context, intent);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        mContext = context;
        mStuckPostSimple = new StuckPostSimple("", "", "", "", "", "", "",
            0, 0, 0, 0, new HashMap<String, Object>(), (-1 * new Date().getTime()));

        for (int widgetId : appWidgetIds) {

            // Create an Intent to launch StuckMainListActivity
            Intent intent = new Intent(context, StuckLoginActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);

            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(
                context.getPackageName(), R.layout.widget_view);

            getNewPost(getMostRecentPost());
            singlePostForView(views);

            views.setOnClickPendingIntent(R.id.widget_layout, pendingIntent);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, views);
        }
    }

    private void singlePostForView(RemoteViews views) {

        views.setTextViewText(R.id.single_item_question, mStuckPostSimple.getQuestion());
        views.setTextViewText(R.id.sneak_peak_choice_1, mStuckPostSimple.getChoiceOne());
        views.setTextViewText(R.id.post_location, mStuckPostSimple.getLocation());

        int qusetionTotal = mStuckPostSimple.getChoiceOneVotes() + mStuckPostSimple.getChoiceThreeVotes() +
            mStuckPostSimple.getChoiceTwoVotes() + mStuckPostSimple.getChoiceFourVotes();
        views.setTextViewText(R.id.stuck_question_total_votes, qusetionTotal + "");

    }

    private Cursor getMostRecentPost() {
        Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
            StuckConstants.TABLE_OFFLINE_POST);

        Log.i(TAG, "path 1 = " + contentUri.getPath());

        return mContext.getContentResolver().query(
            contentUri,
            null,
            null,
            null,
            null);
    }

    private void getNewPost(Cursor data) {
        data.moveToFirst();
        //Got from http://stackoverflow.com/questions/10111166/get-all-rows-from-sqlite
        if (data.moveToFirst()) {

            while (!data.isAfterLast()) {

                if ((data.getString(data.getColumnIndex(
                    StuckConstants.COLUMN_MOST_RECENT_POST))).equals(StuckConstants.TRUE)) {

                    String stuckEmail = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_EMAIL));

                    String stuckQuestion = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_QUESTION));

                    String stuckLocation = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_LOCATION));

                    String stuckChoice1 = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_CHOICE_ONE));

                    String stuckChoice2 = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_CHOICE_TWO));

                    String stuckChoice3 = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_CHOICE_THREE));

                    String stuckChoice4 = data.getString(data
                        .getColumnIndex(StuckConstants.COLUMN_CHOICE_FOUR));

                    mStuckPostSimple = new StuckPostSimple(
                        StuckSignUpActivity.encodeEmail(stuckEmail), stuckQuestion,
                        stuckLocation, stuckChoice1, stuckChoice2, stuckChoice3, stuckChoice4,
                        StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                        StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                        new HashMap<String, Object>(),
                        (-1 * new Date().getTime()));

                    Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                        StuckConstants.TABLE_OFFLINE_POST);

                    mContext.getContentResolver().delete(contentUri,
                        StuckConstants.COLUMN_MOST_RECENT_POST + " = ?",
                        new String[]{StuckConstants.TRUE});

                    break;
                }
                data.moveToNext();
            }
        }
    }
}
