package com.example.sammengistu.stuck.activities;

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
import com.example.sammengistu.stuck.asynctask.GoogleAuthTokenTask;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckLoginActivity extends AppCompatActivity {

    private static String TAG = "StuckLoginActivity55";
    private GoogleApiClient mGoogleApiClient;
    private boolean mSendRestPassword = false;
    private Firebase mUserRef;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mUserRef = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS).child(
                StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()));

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

    /**
     * Updates the UI to a reset password view
     */
    private void resetPassword() {
        String email = mEmailEditText.getText().toString();

        mUserRef.resetPassword(email, new Firebase.ResultHandler() {
            @Override
            public void onSuccess() {

                Toast.makeText(StuckLoginActivity.this, R.string.sent_temp_password,
                    Toast.LENGTH_LONG).show();

                changeUserUsedTempToTrue();
                mSendRestPassword = false;
                mPasswordEditText.setVisibility(View.VISIBLE);
                mPasswordEditText.setEnabled(true);
                mLoginButton.setText(R.string.login);
                mLoginButtonGoogle.setVisibility(View.VISIBLE);
                mLoginButtonGoogle.setEnabled(true);
                mForgotPasswordTextView.setEnabled(true);
                mForgotPasswordTextView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FirebaseError firebaseError) {

            }
        });
    }

    /**
     * Updates the users firebase to show that they are logged in with a temp password
     * so that they later rest their password
     */
    private void changeUserUsedTempToTrue() {

        Firebase mRef = mUserRef.child(StuckSignUpActivity.encodeEmail(
            mEmailEditText.getText().toString()));

        Map<String, Object> changePasswordOnLogin = new HashMap<String, Object>();
        changePasswordOnLogin.put(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD, true);
        mRef.updateChildren(changePasswordOnLogin);

    }

    /**
     * Authenticates the user to through email and password
     * then updates the shared preference based on whether then launches the next activity
     */
    private void loginEmailPassword() {

        if (allFieldsAreEntered() && StuckSignUpActivity.vaildEmail(mEmailEditText)) {

            Firebase refUsers = new Firebase(StuckConstants.FIREBASE_URL)
                .child(StuckConstants.FIREBASE_URL_USERS);

            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Logging in..");
            dialog.show();

            refUsers.authWithPassword(mEmailEditText.getText().toString(),
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
                            StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()));

                        editor.putString(StuckConstants.PROVIDER,
                            StuckConstants.SHARED_PREFERENCE_PASSWORD);
                        editor.apply();

                        Firebase refUserLogInType = mUserRef.child(StuckSignUpActivity
                            .encodeEmail(mEmailEditText.getText().toString()))
                            .child(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD);

                        refUserLogInType.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {

                                launchNextActivity(snapshot);

                            }
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                            }
                        });
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        dialog.dismiss();

                        handleLoginError(firebaseError.getCode());
                    }
                });
        }
    }

    /**
     * Based on whether the user logged in with a temp password it will launch the reset password
     * activity or the MainListActivity if they didn't use a a temp password
     * @param snapshot - reset password value
     */
    private void launchNextActivity( DataSnapshot snapshot){
        boolean resetPassword = snapshot.getValue(Boolean.class);

        if (resetPassword) {

            Intent intent = new Intent(StuckLoginActivity.this,
                StuckResetPasswordActivity.class);
            intent.putExtra(StuckConstants.RESET_PASSWORD,
                mPasswordEditText.getText().toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {
            Intent intent = new Intent(StuckLoginActivity.this,
                StuckMainListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

    /**
     * Will display a toast of the type of error
     * @param errorCode - type of error
     */
    private void handleLoginError(int errorCode) {
        // Something went wrong :(
        switch (errorCode) {
            case FirebaseError.USER_DOES_NOT_EXIST:
                // handle a non existing user
                Toast.makeText(StuckLoginActivity.this,
                    R.string.login_error_user_does_not_exist, Toast.LENGTH_LONG).show();
                break;
            case FirebaseError.INVALID_PASSWORD:
                // handle an invalid password
                Toast.makeText(StuckLoginActivity.this,
                    R.string.login_error_invalid_password, Toast.LENGTH_LONG).show();
                break;
            default:
                // handle other errors
                Toast.makeText(StuckLoginActivity.this,
                    R.string.login_error_error_logging_in, Toast.LENGTH_LONG).show();
                break;
        }
    }

    private boolean allFieldsAreEntered() {
        return !mEmailEditText.getText().toString().equals("") &&
            !mPasswordEditText.getText().toString().equals("");
    }

    /**
     * Signs user in with google
     */
    private void signInGoogle() {
        Log.i(TAG, "Google signin ");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, StuckConstants.RC_SIGN_IN);
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "Google handle signin ");
        if (result.isSuccess()) {
            Log.i(TAG, "Google handle signin " + result.isSuccess());
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            new GoogleAuthTokenTask(acct.getEmail(), this, false).execute();
        }
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

    @OnClick(R.id.login_forgot_password_accout)
    public void setForgotPasswordTextView() {

        mSendRestPassword = true;
        Toast.makeText(this, R.string.enter_your_email, Toast.LENGTH_LONG).show();
        mPasswordEditText.setVisibility(View.INVISIBLE);
        mPasswordEditText.setEnabled(false);

        mLoginButton.setText(R.string.sent_temp_password);
        mLoginButtonGoogle.setVisibility(View.INVISIBLE);
        mLoginButtonGoogle.setEnabled(false);

        mForgotPasswordTextView.setEnabled(false);
        mForgotPasswordTextView.setVisibility(View.INVISIBLE);
    }

    @OnClick(R.id.log_in_button_google)
    public void signInWithGoogle() {
        if (NetworkStatus.isOnline(this)) {
            signInGoogle();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
