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
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.asynctask.GoogleAuthTokenTask;
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
    private DatabaseReference mFirebaseRef = FirebaseDatabase.getInstance().getReference();
    private String mEncodedEmail;
    private GoogleApiClient mGoogleApiClient;

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
//    @BindView(R.id.sign_in_button_google)
//    SignInButton mSignInButton;

    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mRE_EnterField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        // Configure sign-in to request the user's ID, mEncodedEmail address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestEmail()
//            .build();
//         Configure Google Sign In
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

//        mSignInButton.setSize(SignInButton.SIZE_STANDARD);
//        mSignInButton.setScopes(gso.getScopeArray());

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
                // ...
            }
        };

        //Todo: remove
        FirebaseAuth.getInstance().signOut();
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
                        Toast.makeText(StuckSignUpActivity.this,
                            "Authentication failed: create a more complex password",
                            Toast.LENGTH_SHORT).show();
                    }
                    // ...
                }
            });
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
     * Adds user to firebase and creates a vote location for them to store their votes
     *
     * @param emailUser - users email
     */
    public static void createUserInFirebaseHelper(String emailUser) {

        final String encodedEmail = encodeEmail(emailUser);
        final DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS)
            .child(encodedEmail);

        /**
         * See if there is already a user (for example, if they already logged in with an associated
         * Google account.
         */
        userLocation.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    //                Log.i(TAG, dataSnapshot + "");
                /* If there is no user, make one */
                    if (dataSnapshot.getValue() == null) {
                                        /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                        HashMap<String, Object> timestampJoined = new HashMap<>();
                        timestampJoined.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                        User newUser = new User(encodedEmail, timestampJoined);
                        userLocation.setValue(encodedEmail);

                        DatabaseReference userLocationToAddTo = FirebaseDatabase.getInstance().getReference()
                            .child(StuckConstants.FIREBASE_URL_USERS).child(encodedEmail);

                        userLocationToAddTo.setValue(newUser, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    Log.i(TAG, databaseError.getMessage());
                                }
                            }
//                            @Override
//                            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                                if (firebaseError != null) {
//                                    Log.i(TAG, firebaseError.getMessage());
//                                }
//                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

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
                                        "Account created, time to login", Toast.LENGTH_LONG).show();
                                    Intent intent = new Intent(StuckSignUpActivity.this,
                                        StuckLoginActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
                } else {
                    Toast.makeText(StuckSignUpActivity.this, "User already exists",
                        Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError firebaseError) {
                Log.d(TAG, "Error logging in" + firebaseError.getMessage());
            }
        });
    }


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
                            .setTitle("There was an error creating your account")
                            .setMessage("The following could of occurred: \n" +
                                "\t 1) Already have an account \n" +
                                "\t 2) Make a more complex password:\n \t use letters, numbers, and special characters \n" +
                                "\t 3) There is an error with your email")
                            .setPositiveButton("Okay", null)
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

    public static boolean vaildEmail(EditText emailField) {
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
                mRE_EnterField) && vaildEmail(mEmailField)) {


                if (mPasswordField.getText().toString().length() >= 5) {

                    createUser(dialog);

                } else {
                    Toast.makeText(this, R.string.make_password_longer, Toast.LENGTH_LONG).show();
                    dialog.dismiss();
                }
            } else {
                new AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage("Error could of been caused by : \n" +
                        "\t 1) Invalid email \n" +
                        "\t 2) All fields weren't entered \n" +
                        "\t 3) Passwords didn't match")
                    .setPositiveButton("Okay", null)
                    .show();
                dialog.dismiss();
            }
        } else {
            NetworkStatus.showOffLineDialog(this);
            dialog.dismiss();
        }
    }

    //Google sign does not work keep getting error
    //05-25 14:22:22.198 5818-6366/com.example.sammengistu.stuck E/DynamiteModule: Failed to load module descriptor class: Didn't find class "com.google.android.gms.dynamite.descriptors.com.google.firebase.auth.ModuleDescriptor" on path: DexPathList[[zip file "/data/app/com.example.sammengistu.stuck-1/base.apk"],nativeLibraryDirectories=[/vendor/lib, /system/lib]]

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i(TAG, "Google handle signin ");
        if (result.isSuccess()) {
            Log.i(TAG, "Google handle signin " + result.isSuccess());
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            mEncodedEmail = acct.getEmail();

            new GoogleAuthTokenTask(mEncodedEmail, this, true).execute();
        } else {
            Toast.makeText(this, R.string.error_google_sign_in, Toast.LENGTH_LONG).show();
        }
    }

//    @OnClick(R.id.sign_in_button_google)
//    public void signInWithGoogle() {
//        if (NetworkStatus.isOnline(this)) {
//            signInGoogle();
//        } else {
//            NetworkStatus.showOffLineDialog(this);
//        }
//    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == StuckConstants.RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
//            Log.i(TAG, result.getStatus().getStatusMessage().to);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.i(TAG, "Sign in success");
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.i(TAG, "Sign in fail");
            }
        }
    }


    private void signInGoogle() {
        Log.i(TAG, "Google signin ");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, StuckConstants.RC_SIGN_IN);
    }


}
