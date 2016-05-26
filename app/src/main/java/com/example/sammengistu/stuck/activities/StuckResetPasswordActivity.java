package com.example.sammengistu.stuck.activities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class StuckResetPasswordActivity extends AppCompatActivity {
    private static final String TAG = "ResetPasswordActivity55";
    private EditText mPasswordED;
    private EditText mReenterED;
    private String mEmail;
    private DatabaseReference mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mPasswordED = (EditText) findViewById(R.id.password_edit_text);
        mReenterED = (EditText) findViewById(R.id.reenter_password_edit_text);
        Button resetButton = (Button) findViewById(R.id.reset_password_button);

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode

        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "").replace(",", ".");

        mUserRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS)
            .child(StuckSignUpActivity.encodeEmail(mEmail));

        assert resetButton != null;
        resetButton.setOnClickListener(mResetPasswordOnClickLis);
    }

    View.OnClickListener mResetPasswordOnClickLis = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mPasswordED.getText().toString().equals(mReenterED.getText().toString())) {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String newPassword = mReenterED.getText().toString();

                user.updatePassword(newPassword)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(StuckResetPasswordActivity.this,
                                    "Password has been reset", Toast.LENGTH_LONG).show();

                                changeUserUsedTempToFalse();

                                FirebaseAuth.getInstance().signOut();
                                Intent intent = new Intent(StuckResetPasswordActivity.this,
                                    StuckLoginActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
            } else {
                Toast.makeText(StuckResetPasswordActivity.this,
                    "Passwords don't match", Toast.LENGTH_LONG).show();
            }
        }
    };



    /**
     * Next time the user logs in they won't be sent to this activity since it was already reset
     */
    private void changeUserUsedTempToFalse() {

        Map<String, Object> changePasswordOnLogin = new HashMap<>();
        changePasswordOnLogin.put(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD, false);
        mUserRef.updateChildren(changePasswordOnLogin);
//            new Firebase.CompletionListener() {
//            @Override
//            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                Log.i(TAG, firebase.getRef().toString());
//            }
//        }
//        );
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
//        mFirebaseRef.unauth();
    }
}
