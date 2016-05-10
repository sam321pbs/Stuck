package com.example.sammengistu.stuck.asynctask;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;

import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.activities.StuckMainListActivity;
import com.example.sammengistu.stuck.activities.StuckSignUpActivity;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;

/**
 * Gets google auth token for user
 */
public class GoogleAuthTokenTask extends AsyncTask<Void, Void, String> {

    private static final String TAG = "GoogleAuthTokenTask55";
    private ProgressDialog mProgressDialog;

    private String mEmail;
    private Firebase mFirebase;
    private Context mAppContext;
    private boolean mCreateUserInFB;

    public GoogleAuthTokenTask(String email, Context appContext, boolean createUserInFB) {
        mEmail = email;
        mFirebase = new Firebase(StuckConstants.FIREBASE_URL);
        mAppContext = appContext;
        mCreateUserInFB = createUserInFB;
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog = new ProgressDialog(mAppContext);
        mProgressDialog.setMessage("Logging in..");
        mProgressDialog.show();
    }

    @SuppressWarnings("deprecation")
    @Override
    protected String doInBackground(Void... params) {
        String scopes = "oauth2:profile email";
        String token = null;
        try {
            token = GoogleAuthUtil.getToken(mAppContext, mEmail, scopes);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (GoogleAuthException e) {
            e.printStackTrace();
        }
        // exception handling removed for brevity
        return token;
    }

    @Override
    protected void onPostExecute(String s) {
        mFirebase.authWithOAuthToken("google", s, new Firebase.AuthResultHandler() {
            @Override
            public void onAuthenticated(AuthData authData) {
                // the Google user is now authenticated with your Firebase app

                mProgressDialog.dismiss();
                Log.i(TAG, "Google signin success");
                String encodedEmail = StuckSignUpActivity.encodeEmail(mEmail);

                SharedPreferences pref = mAppContext
                    .getSharedPreferences("UserPref", 0); // 0 - for private mode
                SharedPreferences.Editor editor = pref.edit();
                //on the login store the login
                editor.putString(StuckConstants.KEY_ENCODED_EMAIL, encodedEmail);
                editor.putString(StuckConstants.PROVIDER, "google");
                editor.apply();

                if (mCreateUserInFB) {
                    StuckSignUpActivity.createUserInFirebaseHelper(encodedEmail);
                }
                Intent intent = new Intent(mAppContext, StuckMainListActivity.class);
                intent.putExtra(StuckConstants.PASSED_IN_EMAIL, encodedEmail);
                mAppContext.startActivity(intent);
            }

            @Override
            public void onAuthenticationError(FirebaseError firebaseError) {
                mProgressDialog.dismiss();
                // there was an error
                Log.i(TAG, "Google signin error");
            }
        });
    }
}
