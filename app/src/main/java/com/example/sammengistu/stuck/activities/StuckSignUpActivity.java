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

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.User;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ServerValue;
import com.firebase.client.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckSignUpActivity extends AppCompatActivity {

    private static String TAG = "StuckSignUpActivity";
    @BindView(R.id.email_edit_text)
    EditText mEmailField;

    @BindView(R.id.password_edit_text)
    EditText mPasswordField;

    @BindView(R.id.reenter_password_edit_text)
    EditText mRE_EnterField;

    @BindView(R.id.create_account_button)
    Button mCreateAccountButton;

    @BindView(R.id.sign_up_to_login_account)
    TextView mLoginTextView;

    @BindView(R.id.sign_in_button_google)
    SignInButton mSignInButton;

    private Firebase firebase = new Firebase(StuckConstants.FIREBASE_URL);

    @OnClick(R.id.sign_up_to_login_account)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckLoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.sign_in_button_google)
    public void signInWithGoogle() {
        signIn();
    }

    @OnClick(R.id.create_account_button)
    public void onClickCreate() {

        if (allFieldsAreEntered() && passwordsMatch() && vaildEmail(mEmailField)) {
            if (mPasswordField.getText().toString().length() >= 5) {
                final ProgressDialog dialog = new ProgressDialog(this);
                dialog.setMessage("Creating account..");
                dialog.show();

                firebase.createUser(mEmailField.getText().toString(),
                    mPasswordField.getText().toString(),
                    new Firebase.ValueResultHandler<Map<String, Object>>() {
                        @Override
                        public void onSuccess(Map<String, Object> stringObjectMap) {
                            Log.i(TAG, "Successfully created user account with uid: " + stringObjectMap.get("uid"));
                            dialog.dismiss();

                            email = encodeEmail(mEmailField.getText().toString());
                            String uid = (String) stringObjectMap.get("uid");
                            createUserInFirebaseHelper(email);

                            Log.i(TAG, "after created user in db");
                            SharedPreferences pref = getApplicationContext()
                                .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);
                            SharedPreferences.Editor editor = pref.edit();
                            //on the login store the login
                            editor.putString(StuckConstants.KEY_ENCODED_EMAIL, email);
                            editor.putString(StuckConstants.PROVIDER,
                                StuckConstants.SHARED_PREFRENCE_PROVIDER_TYPE_PASSWORD);
                            editor.apply();

                            Intent intent = new Intent(StuckSignUpActivity.this, StuckLoginActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            dialog.dismiss();
                            Toast.makeText(StuckSignUpActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                            Log.i(TAG, firebaseError.getMessage());
                        }
                    });
            } else {
                Toast.makeText(this, "Password needs to be atleast 5 characters long", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void createUserInFirebaseHelper(String emailUser) {

        final String encodedEmail = encodeEmail(emailUser);
        final Firebase userLocation = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS).child(encodedEmail);

        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Firebase userLocationToAddTo = new Firebase(StuckConstants.FIREBASE_URL)
                    .child(StuckConstants.FIREBASE_URL_USERS);

                Log.i(TAG, dataSnapshot.getChildren().toString());
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null) {
                                        /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    User newUser = new User(encodedEmail, timestampJoined);
                    userLocationToAddTo.push().setValue(newUser);

                } else {

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d("StuckSignUpActivity", "Error logging in" + firebaseError.getMessage());
            }
        });

    }

    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    private String email;

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

            email = acct.getEmail();

            new GoogleAuthtokenTask(email).execute();

        }
    }

    public class GoogleAuthtokenTask extends AsyncTask<Void, Void, String> {

        private ProgressDialog dialog;

        private String mEmail;

        public GoogleAuthtokenTask(String email) {
            dialog = new ProgressDialog(StuckSignUpActivity.this);
            mEmail = email;
        }

        @Override
        protected void onPreExecute() {

            dialog.setMessage("Creating account..");
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
            firebase.authWithOAuthToken("google", s, new Firebase.AuthResultHandler() {
                @Override
                public void onAuthenticated(AuthData authData) {
                    // the Google user is now authenticated with your Firebase app

                    dialog.dismiss();
                    Log.i(TAG, "Google signin success");
                    String encodedEmail = encodeEmail(mEmail);

                    createUserInFirebaseHelper(encodedEmail);


                    Intent intent = new Intent(StuckSignUpActivity.this, StuckMainListActivity.class);
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

    private GoogleApiClient mGoogleApiClient;

    public static boolean vaildEmail(EditText emailField) {
        return emailField.getText().toString().contains("@") && emailField.getText().toString().contains(".");
    }

    private boolean passwordsMatch() {
        return mPasswordField.getText().toString().equals(mRE_EnterField.getText().toString());
    }

    private boolean allFieldsAreEntered() {

        return !mEmailField.getText().toString().equals("") &&
            !mEmailField.getText().toString().equals("") &&
            !mEmailField.getText().toString().equals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mRE_EnterField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

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

        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
        mSignInButton.setScopes(gso.getScopeArray());
    }
}
