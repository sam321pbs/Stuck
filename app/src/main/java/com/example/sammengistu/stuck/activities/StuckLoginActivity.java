package com.example.sammengistu.stuck.activities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;

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

        //If user logs in this will get called
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    final String email = mEmailEditText.getText().toString();

                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    mProgressDialog.dismiss();

                    //Store users email
                    SharedPreferences pref = getApplicationContext()
                        .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(StuckConstants.KEY_ENCODED_EMAIL,
                        StuckSignUpActivity.encodeEmail(email));
                    //store type of login
                    editor.putString(StuckConstants.PROVIDER,
                        StuckConstants.SHARED_PREFERENCE_PASSWORD);
                    editor.apply();

                    DatabaseReference refUserLogInType = FirebaseDatabase.getInstance().getReference()
                        .child(StuckConstants.FIREBASE_URL_USERS)
                        .child(StuckSignUpActivity.encodeEmail(email))
                        .child(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD);

                    Log.i(TAG, "Ref = " + refUserLogInType.toString());

                    //Check log in type, in case they have to reset their email
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
            }
        };
    }

    /**
     * Authenticates the user to through email and password
     * then updates the shared preference based on whether then launches the next activity
     */
    private void loginEmailPassword() {

        if (allFieldsAreEntered() && StuckSignUpActivity.validEmail(mEmailEditText)) {

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

    @OnClick(R.id.login_forgot_password_accout)
    public void setForgotPasswordTextView() {
        Intent intent = new Intent(StuckLoginActivity.this, ForgotPasswordActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_sign_up_accout)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckSignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void loginButton() {
        mProgressDialog.setMessage(getString(R.string.logging_in_dialog));
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
