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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.User;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckSignUpActivity extends AppCompatActivity {

    private static String TAG = "StuckSignUpActivity55";

    @BindView(R.id.email_edit_text)
    EditText mEmailField;
    @BindView(R.id.password_edit_text)
    EditText mPasswordField;
    @BindView(R.id.reenter_password_edit_text)
    EditText mRE_EnterField;
    @BindView(R.id.create_account_button)
    Button mCreateAccountButton;
    @BindView(R.id.go_to_login_account)
    TextView mLoginTextView;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mRE_EnterField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    putEmailInSharedPref(user.getEmail());
                    createUserInFBHelper(encodeEmail(mEmailField.getText().toString()));

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    private void putEmailInSharedPref(String email) {
        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);
        SharedPreferences.Editor editor = pref.edit();
        //on the login store the login
        editor.putString(StuckConstants.KEY_ENCODED_EMAIL, encodeEmail(email));
        editor.putString(StuckConstants.PROVIDER,
            StuckConstants.SHARED_PREFRENCE_PROVIDER_TYPE_PASSWORD);
        editor.apply();
    }

    /**
     * Checks to see if user already has an account before creating them another one
     * @param emailUser - email to search for
     */
       private void createUserInFBHelper(String emailUser) {
        final String encodedEmail = encodeEmail(emailUser);
        final DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS)
            .child(encodedEmail);

        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Log.i(TAG, "datasnap = " + dataSnapshot + "");
                /* If there is no user, make one */
                if (dataSnapshot.getValue() == null) {
                    /* Set raw version of date to the ServerValue.TIMESTAMP
                     value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP,
                        ServerValue.TIMESTAMP);

                    User newUser = new User(encodedEmail, timestampJoined);
                    userLocation.setValue(encodedEmail);

                    DatabaseReference userLocationToAddTo =
                        FirebaseDatabase.getInstance().getReference()
                        .child(StuckConstants.FIREBASE_URL_USERS).child(encodedEmail);

                    userLocationToAddTo.setValue(newUser,
                        new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.i(TAG, databaseError.getMessage());
                                } else {
                                    Toast.makeText(StuckSignUpActivity.this,
                                        R.string.account_created, Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(StuckSignUpActivity.this,
                                        StuckLoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                } else {
                    Toast.makeText(StuckSignUpActivity.this, R.string.user_already_exists,
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.d(TAG, "Error logging in" + firebaseError.getMessage());
            }
        });
    }

    /**
     * Actually creates the user in the auth section of firebase
     * @param dialog - dialog to dismiss
     */
    private void createUser(final ProgressDialog dialog) {

        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();

        Log.i(TAG, "Email = " + email);
        Log.i(TAG, "PassWord = " + password);

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    dialog.dismiss();
                    Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Toast.makeText(StuckSignUpActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();

                        new AlertDialog.Builder(StuckSignUpActivity.this)
                            .setTitle(getString(R.string.error_creating_account_title))
                            .setMessage(getString(R.string.error_creating_account_message))
                            .setPositiveButton(getString(R.string.okay), null)
                            .show();
                    }

                    // ...
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    dialog.dismiss();
                }
            });
    }

    /**
     * Firebase doesnt allow periods so the period is replaced with a comma
     *
     * @param userEmail - normal email
     * @return - encoded email
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    public static boolean validEmail(EditText emailField) {
        return emailField.getText().toString().contains("@") && emailField.getText().toString().contains(".");
    }

    public static boolean passwordsMatch(EditText passwordField, EditText reenterField) {
        return passwordField.getText().toString().equals(reenterField.getText().toString());
    }

    private boolean allFieldsAreEntered() {

        return !mEmailField.getText().toString().equals("") &&
            !mEmailField.getText().toString().equals("") &&
            !mEmailField.getText().toString().equals("");
    }

    @OnClick(R.id.go_to_login_account)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckLoginActivity.class);
        startActivity(intent);
    }


    @OnClick(R.id.create_account_button)
    public void onClickCreate() {
        final ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating account");
        dialog.show();

        if (NetworkStatus.isOnline(this)) {
            if (allFieldsAreEntered() && passwordsMatch(mPasswordField,
                mRE_EnterField) && validEmail(mEmailField)) {

                if (mPasswordField.getText().toString().length() >= 5) {

                    createUser(dialog);

                } else {
                    Toast.makeText(this, R.string.make_password_longer, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            } else {
                new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.error))
                    .setMessage(getString(R.string.error_in_sign_up))
                    .setPositiveButton(getString(R.string.okay), null)
                    .show();
                dialog.dismiss();
            }
        } else {
            NetworkStatus.showOffLineDialog(this);
            dialog.dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
        FirebaseAuth.getInstance().signOut();
    }

}
