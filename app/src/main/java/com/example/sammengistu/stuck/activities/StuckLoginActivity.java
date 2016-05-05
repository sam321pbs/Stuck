package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckLoginActivity extends AppCompatActivity {

    @BindView(R.id.login_email_edit_text)
    EditText mLoginEditText;

    @BindView(R.id.login_password_edit_text)
    EditText mPasswordEditText;

    @BindView(R.id.login_sign_up_accout)
    TextView mLogingTextView;

    @BindView(R.id.login_button)
    Button mLoginButton;

    @OnClick(R.id.login_sign_up_accout)
    public void onClickLoginActivity() {
        Intent intent = new Intent(this, StuckSignUpActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.login_button)
    public void loginButton(){
        if (allFieldsAreEntered() && StuckSignUpActivity.vaildEmail(mLoginEditText)) {
            Firebase ref = new Firebase(StuckConstants.FIREBASE_URL);
            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setMessage("Logging in..");
            dialog.show();
            ref.authWithPassword(mLoginEditText.getText().toString(), mPasswordEditText.getText().toString(),
                new Firebase.AuthResultHandler() {
                    @Override
                    public void onAuthenticated(AuthData authData) {
                        dialog.dismiss();
                        Intent intent = new Intent(StuckLoginActivity.this, StuckMainListActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onAuthenticationError(FirebaseError firebaseError) {
                        dialog.dismiss();

                        // Something went wrong :(
                        switch (firebaseError.getCode()) {
                            case FirebaseError.USER_DOES_NOT_EXIST:
                                // handle a non existing user
                                Toast.makeText(StuckLoginActivity.this, "User does not exist", Toast.LENGTH_LONG).show();
                                break;
                            case FirebaseError.INVALID_PASSWORD:
                                // handle an invalid password
                                Toast.makeText(StuckLoginActivity.this, "Invalid password", Toast.LENGTH_LONG).show();
                                break;
                            default:
                                // handle other errors
                                Toast.makeText(StuckLoginActivity.this, "Error Logging in, try again", Toast.LENGTH_LONG).show();

                                break;
                        }
                    }
                });

        }
    }

    private boolean allFieldsAreEntered() {

        return !mLoginEditText.getText().toString().equals("") &&
            !mPasswordEditText.getText().toString().equals("");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

    }
}
