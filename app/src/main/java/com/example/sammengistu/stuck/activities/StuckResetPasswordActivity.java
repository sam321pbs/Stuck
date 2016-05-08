package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.User;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class StuckResetPasswordActivity extends AppCompatActivity {
    final EditText passwordED = (EditText) findViewById(R.id.password_edit_text);
    final EditText reenterED = (EditText) findViewById(R.id.reenter_password_edit_text);
    Button resetButton = (Button) findViewById(R.id.reset_password_button);
    Firebase userRef;
    String email ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_reset_password);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode
        userRef = new Firebase(StuckConstants.FIREBASE_URL_USERS).child(email);
        email = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");


        assert resetButton != null;
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Check if current user has logged in at least once
                 */
                userRef.addListenerForSingleValueEvent(mValueEventListener);
            }
        });
    }

    ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            User user = dataSnapshot.getValue(User.class);

//                        Todo: have user RESET PASSWORD
            if (user != null) {

                /**
                 * If recently registered user has hasLoggedInWithPassword = "false"
                 * (never logged in using password provider)
                 */
                if (!user.isHasLoggedInWithTempPassword()) {

                    /**
                     * Change password if user that just signed in signed up recently
                     * to make sure that user will be able to use temporary password
                     * from the email more than 24 hours
                     */
                    userRef.changePassword(email,
                        getIntent().getStringExtra(StuckConstants.TEMP_PASSWORD),
                        passwordED.getText().toString(),
                        new Firebase.ResultHandler() {
                            @Override
                            public void onSuccess() {
                                userRef.unauth();
                                Intent intent  = new Intent(StuckResetPasswordActivity.this,
                                    StuckLoginActivity.class);

                                startActivity(intent);
                            }

                            @Override
                            public void onError(FirebaseError firebaseError) {

                            }
                        });
                }
            }
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };
}
