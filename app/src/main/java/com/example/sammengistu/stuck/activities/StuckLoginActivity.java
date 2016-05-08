package com.example.sammengistu.stuck.activities;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.User;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.SecureRandom;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckLoginActivity extends AppCompatActivity {

    private static String TAG = "StuckLoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private Firebase mFirebase;
    private SecureRandom mRandom = new SecureRandom();
    private boolean mSendRestPassword = false;

    @BindView(R.id.login_email_edit_text)
    EditText mEmailEditText;

    @BindView(R.id.login_password_edit_text)
    EditText mPasswordEditText;

    @BindView(R.id.login_sign_up_accout)
    TextView mLogingTextView;

    @BindView(R.id.login_button)
    Button mLoginButton;

    @BindView(R.id.log_in_button_google)
    SignInButton mLoginButtonGoogle;

    @BindView(R.id.login_forgot_password_accout)
    TextView mForgotPasswordTextView;

    @OnClick(R.id.login_forgot_password_accout)
    public void setForgotPasswordTextView() {

        mSendRestPassword = true;
        Toast.makeText(this, "Enter your email", Toast.LENGTH_LONG).show();
        mPasswordEditText.setVisibility(View.INVISIBLE);
        mPasswordEditText.setEnabled(false);

        mLoginButton.setText("Send temp email");
        mLoginButtonGoogle.setVisibility(View.INVISIBLE);
        mLoginButtonGoogle.setEnabled(false);
    }

    @OnClick(R.id.log_in_button_google)
    public void signInWithGoogle() {
        if (NetworkStatus.isOnline(this)) {
            signIn();
        } else {
            NetworkStatus.showOffLineDialog(this);
        }
    }

    @OnClick(R.id.login_sign_up_accout)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckSignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void loginButton() {

        if (NetworkStatus.isOnline(this)) {
            if (mSendRestPassword && StuckSignUpActivity.vaildEmail(mEmailEditText)) {

               resetPassword();


            } else {
                loginEmailPassword();
            }
        } else {
            NetworkStatus.showOffLineDialog(this);
        }
    }

    private void resetPassword(){
        String email = mEmailEditText.getText().toString();
        final Firebase mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS);

        final Firebase userRef = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS).child(
                StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()));

        userRef.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

                Toast.makeText(StuckLoginActivity.this, "Temp password sent",
                    Toast.LENGTH_LONG).show();

                mSendRestPassword = false;
                mPasswordEditText.setVisibility(View.VISIBLE);
                mPasswordEditText.setEnabled(true);
                mLoginButton.setText("Login");
                mLoginButtonGoogle.setVisibility(View.VISIBLE);
                mLoginButtonGoogle.setEnabled(true);
            }

            @Override
            public void onError(FirebaseError firebaseError) {

            }
        });
    }

    private void loginEmailPassword() {
        if (allFieldsAreEntered() && StuckSignUpActivity.vaildEmail(mEmailEditText)) {
            Firebase ref = new Firebase(StuckConstants.FIREBASE_URL)
                .child(StuckConstants.FIREBASE_URL_USERS);

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Logging in..");
            dialog.show();

            ref.authWithPassword(mEmailEditText.getText().toString(),
                mPasswordEditText.getText().toString(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        dialog.dismiss();

                        SharedPreferences pref = getApplicationContext()
                            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);
                        SharedPreferences.Editor editor = pref.edit();
                        //on the login store the login
                        editor.putString(StuckConstants.KEY_ENCODED_EMAIL,
                            mEmailEditText.getText().toString());

                        editor.putString(StuckConstants.PROVIDER, "password");
                        editor.apply();

                        final Firebase userRef = new Firebase(StuckConstants.FIREBASE_URL)
                            .child(StuckConstants.FIREBASE_URL_USERS)
//                            .child(StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()))
                            ;

                        userRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Log.i(TAG, dataSnapshot.toString() + "");

                                User user = dataSnapshot.getValue(User.class);

                                if (user.isHasLoggedInWithTempPassword()){

                                    Intent intent = new Intent(StuckLoginActivity.this,
                                        StuckSignUpActivity.class);
                                    intent.putExtra(StuckConstants.RESET_PASSWORD,
                                        mPasswordEditText.getText().toString());
                                    startActivity(intent);

                                } else {
                                    Intent intent = new Intent(StuckLoginActivity.this,
                                        StuckMainListActivity.class);
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        dialog.dismiss();

                        // Something went wrong :(
                        switch (firebaseError.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // handle a non existing user
                                Toast.makeText(StuckLoginActivity.this,
                                    "User does not exist", Toast.LENGTH_LONG).show();
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                // handle an invalid password
                                Toast.makeText(StuckLoginActivity.this,
                                    "Invalid password", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                // handle other errors
                                Toast.makeText(StuckLoginActivity.this,
                                    "Error Logging in, try again", Toast.LENGTH_LONG).show();

                                break;
                        }
                    }
                });
        }
    }

    private boolean allFieldsAreEntered() {
        return !mEmailEditText.getText().toString().equals("") &&
            !mPasswordEditText.getText().toString().equals("");
    }

    private void signIn() {
        Log.i(TAG, "Google signin ");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, StuckConstants.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == StuckConstants.RC_SIGN_IN) {
            Log.i(TAG, "Google signin on act " + requestCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            Log.i(TAG, "Google signin failon act");

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "Google handle signin ");
        if (result.isSuccess()) {
            Log.i(TAG, "Google handle signin " + result.isSuccess());
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            new GoogleAuthtokenTask(acct.getEmail()).execute();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mFirebase = new Firebase(StuckConstants.FIREBASE_URL);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                @Override
                public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

                }
            } /* OnConnectionFailedListener */)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build();
    }

    private class GoogleAuthtokenTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        private String mEmail;

        public GoogleAuthtokenTask(String email) {
            mEmail = email;
        }

        @Override
        protected void onPreExecute() {
            dialog = new ProgressDialog(StuckLoginActivity.this);
            dialog.setMessage("Logging in..");
            dialog.show();
        }

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), mEmail, scopes);
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

                    dialog.dismiss();
                    Log.i(TAG, "Google signin success");
                    String encodedEmail = StuckSignUpActivity.encodeEmail(mEmail);

                    SharedPreferences pref = getApplicationContext()
                        .getSharedPreferences("UserPref", 0); // 0 - for private mode
                    SharedPreferences.Editor editor = pref.edit();
                    //on the login store the login
                    editor.putString(StuckConstants.KEY_ENCODED_EMAIL, encodedEmail);
                    editor.putString(StuckConstants.PROVIDER, "google");
                    editor.apply();

                    Intent intent = new Intent(StuckLoginActivity.this, StuckMainListActivity.class);
                    intent.putExtra(StuckConstants.PASSED_IN_EMAIL, encodedEmail);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    dialog.dismiss();
                    // there was an error
                    Log.i(TAG, "Google signin error");
                }
            });
        }
    }
}