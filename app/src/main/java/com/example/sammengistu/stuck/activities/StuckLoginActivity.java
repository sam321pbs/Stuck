package com.example.sammengistu.stuck.activities;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.asynctask.GoogleAuthTokenTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckLoginActivity extends AppCompatActivity {

    private static String TAG = "StuckLoginActivity55";
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;

    @BindView(R.id.login_email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.login_password_edit_text)
    EditText mPasswordEditText;
    @BindView(R.id.login_sign_up_accout)
    TextView mLoggingTextView;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.login_forgot_password_accout)
    TextView mForgotPasswordTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();

        mProgressDialog = new ProgressDialog(StuckLoginActivity.this);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
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

        //If user logs in this will get called
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mProgressDialog.dismiss();

                    SharedPreferences pref = getApplicationContext()
                        .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    //on the login store the login
                    editor.putString(StuckConstants.KEY_ENCODED_EMAIL,
                        StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()));

                    editor.putString(StuckConstants.PROVIDER,
                        StuckConstants.SHARED_PREFERENCE_PASSWORD);
                    editor.apply();

                    final String email = mEmailEditText.getText().toString();

                    DatabaseReference refUserLogInType = FirebaseDatabase.getInstance().getReference()
                        .child(StuckConstants.FIREBASE_URL_USERS)
                        .child(StuckSignUpActivity
                        .encodeEmail(email))
                        .child(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD);

                    Log.i(TAG, "Ref = " + refUserLogInType.toString());

                    refUserLogInType.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.i(TAG, "Login type snap = "  + dataSnapshot.toString());

                            if (dataSnapshot != null && !email.equals("")) {
                                launchNextActivity(dataSnapshot);
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    mProgressDialog.dismiss();
                }
                // ...
            }
        };
    }

    /**
     * Authenticates the user to through email and password
     * then updates the shared preference based on whether then launches the next activity
     */
    private void loginEmailPassword() {

        if (allFieldsAreEntered() && StuckSignUpActivity.vaildEmail(mEmailEditText)) {

            FirebaseAuth auth = FirebaseAuth.getInstance();

            auth.signInWithEmailAndPassword(
                mEmailEditText.getText().toString(), mPasswordEditText.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            mProgressDialog.dismiss();
                            Log.w(TAG, "signInWithEmail", task.getException());
                            Toast.makeText(StuckLoginActivity.this, R.string.login_failed,
                                Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mProgressDialog.dismiss();
                    }
                });
        } else {
            mProgressDialog.dismiss();
            Toast.makeText(this, R.string.invailid_email_or_all_fields_were_not_entered_toast,
                Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Based on whether the user logged in with a temp password it will launch the reset password
     * activity or the MainListActivity if they didn't use a a temp password
     *
     * @param snapshot - reset password value
     */
    private void launchNextActivity(DataSnapshot snapshot) {

        boolean resetPassword = snapshot.getValue(Boolean.class);

        Log.i(TAG, "Reset password = " + resetPassword);
        if (resetPassword) {
            Log.i(TAG, "Intent = reset" );
            Intent intent = new Intent(StuckLoginActivity.this,
                StuckResetPasswordActivity.class);
            intent.putExtra(StuckConstants.RESET_PASSWORD,
                mPasswordEditText.getText().toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        } else {
            Log.i(TAG, "Intent = mainlist" );
            Intent intent = new Intent(StuckLoginActivity.this,
                StuckMainListActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        // ...
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(StuckLoginActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }

    @OnClick(R.id.login_forgot_password_accout)
    public void setForgotPasswordTextView() {
        Intent intent = new Intent(StuckLoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

//    @OnClick(R.id.log_in_button_google)
//    public void signInWithGoogle() {
//        if (NetworkStatus.isOnline(this)) {
//            signInGoogle();
//        } else {
//            NetworkStatus.showOffLineDialog(this);
//        }
//    }

    @OnClick(R.id.login_sign_up_accout)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckSignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void loginButton() {
        mProgressDialog.setMessage("Logging in..");
        mProgressDialog.show();
        if (NetworkStatus.isOnline(this)) {
                loginEmailPassword();

        } else {
            mProgressDialog.dismiss();
            NetworkStatus.showOffLineDialog(this);

        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
