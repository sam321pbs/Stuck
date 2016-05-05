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
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.ProgressDialog;
import android.content.Intent;
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
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckSignUpActivity extends AppCompatActivity {

    @BindView(R.id.email_edit_text)
    EditText mEmailField;

    @BindView(R.id.password_edit_text)
    EditText mPasswordField;

    @BindView(R.id.reenter_password_edit_text)
    EditText mRE_EnterField;

    @BindView(R.id.create_account_button)
    Button mCreateAccountButton;

    @BindView(R.id.sign_up_login_account)
    TextView mLoginTextView;

    @BindView(R.id.sign_in_button_google)
    SignInButton mSignInButton;

    private  Firebase firebase = new Firebase(StuckConstants.FIREBASE_URL);

    @OnClick(R.id.sign_up_login_account)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckLoginActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.sign_in_button_google)
    public void signInWithGoogle(){

        signIn();

    }

    private String email;

    private void signIn() {
        Log.i("StuckSignIn", "Google signin ");
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, StuckConstants.RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == StuckConstants.RC_SIGN_IN) {
            Log.i("StuckSignIn", "Google signin on act " + requestCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        } else {
            Log.i("StuckSignIn", "Google signin failon act");

        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.i("StuckSignIn", "Google handle signin " );
        if (result.isSuccess()) {
            Log.i("StuckSignIn", "Google handle signin " + result.isSuccess());
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            email = acct.getEmail();

            new GoogleAuthtokenTask().execute();
//
//            if (acct.getIdToken() != null){
//                firebase.authWithOAuthToken("google", acct.getIdToken(), new Firebase.AuthResultHandler() {
//                    @Override
//                    public void onAuthenticated(AuthData authData) {
//                        // the Google user is now authenticated with your Firebase app
//
//                        Log.i("StuckSignIn", "Google signin success");
//                        Intent intent = new Intent(StuckSignUpActivity.this, StuckMainListActivity.class);
//                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onAuthenticationError(FirebaseError firebaseError) {
//                        // there was an error
//                        Log.i("StuckSignIn", "Google signin error");
//                    }
//                });
//            } else {
//                Log.i("StuckSignIn", "Google handle signin account id null" );
//            }
//
////            mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
////            updateUI(true);
//        } else {
//            // Signed out, show unauthenticated UI.
////            updateUI(false);
//            Log.i("StuckSignIn", "Google signin fail");
        }
    }

    private class GoogleAuthtokenTask extends AsyncTask<Void,Void,String>{

        @SuppressWarnings("deprecation")
        @Override
        protected String doInBackground(Void... params) {
            String scopes = "oauth2:profile email";
            String token = null;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), email, scopes);
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

                    Log.i("StuckSignIn", "Google signin success");
                    Intent intent = new Intent(StuckSignUpActivity.this, StuckMainListActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    // there was an error
                    Log.i("StuckSignIn", "Google signin error");
                }
            });
        }
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
                            Log.i("SignUp", "Successfully created user account with uid: " + stringObjectMap.get("uid"));
                            dialog.dismiss();
                        }

                        @Override
                        public void onError(FirebaseError firebaseError) {
                            dialog.dismiss();
                            Toast.makeText(StuckSignUpActivity.this, "There was an error", Toast.LENGTH_LONG).show();
                        }
                    });
            } else {
                Toast.makeText(this, "Password needs to be atleast 5 characters long", Toast.LENGTH_LONG).show();
            }
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
