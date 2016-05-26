package com.example.sammengistu.stuck.asynctask;

import com.google.firebase.database.FirebaseDatabase;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.stuck_offline_db.ContentProviderStuck;
import com.example.sammengistu.stuck.stuck_offline_db.StuckDBConverter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;


public class SaveToDBTask extends AsyncTask<Void, Void, Void> {

    private ProgressDialog mProgressDialog;
    private StuckPostSimple mStuckPostSimple;
    private boolean mPostToFirebase;
    private Activity mActivity;
    private ProgressDialog mDialog;

    public SaveToDBTask(StuckPostSimple stuckPostSimple, Activity activity, ProgressDialog dialog) {
        mStuckPostSimple = stuckPostSimple;
        mProgressDialog = new ProgressDialog(activity);
        mPostToFirebase = true;
        mActivity = activity;
        mDialog = dialog;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.setMessage(mActivity.getString(R.string.uploading_post));
        mProgressDialog.show();
        mDialog.dismiss();
    }

    @Override
    protected Void doInBackground(Void... params) {

        if (!NetworkStatus.isOnline(mActivity)) {

            mPostToFirebase = false;

            Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                StuckConstants.TABLE_OFFLINE_POST);

            mActivity.getContentResolver().insert(contentUri,
                StuckDBConverter.insertStuckPostToDB(mStuckPostSimple, StuckConstants.FALSE));
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        mProgressDialog.dismiss();

        SharedPreferences pref = mActivity.getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);

        SharedPreferences.Editor editor = pref.edit();

        if (mPostToFirebase) {
            FirebaseDatabase.getInstance().getReference()
                .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS).push().setValue(mStuckPostSimple);
            editor.putBoolean(StuckConstants.USER_MADE_OFFLINE_POST,
                false);

            editor.apply();
        } else {

            editor.putBoolean(StuckConstants.USER_MADE_OFFLINE_POST,
                true);

            editor.apply();

            Toast.makeText(mActivity,
                R.string.oofline_will_make_post_later, Toast.LENGTH_LONG).show();
        }
    }
}
