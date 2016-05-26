package com.example.sammengistu.stuck.activities;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
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

public class ForgotPasswordActivity extends AppCompatActivity {

    private final String TAG = "ForgotPasswordActivity";

    @BindView(R.id.forgot_password_email_edit_text)
    EditText mEmailEditText;
    @BindView(R.id.send_temp_button)
    Button mSendTempEmailButton;
    @BindView(R.id.forgot_password_sign_up_accout)
    TextView mSignUpButton;

    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle onSavedInstanceState){
        super.onCreate(onSavedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        dialog = new ProgressDialog(this);
        dialog.setTitle(getString(R.string.send_temp_email));

        if (NetworkStatus.isOnline(this)) {
            if (StuckSignUpActivity.vaildEmail(mEmailEditText)) {

                resetPassword();
            }
        } else {
            dialog.dismiss();
            NetworkStatus.showOffLineDialog(this);
        }

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ForgotPasswordActivity.this, StuckSignUpActivity.class);
                startActivity(intent);
            }
        });

        mSendTempEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
                resetPassword();
            }
        });
    }

    /**
     * Updates the UI to a reset password view
     */
    private void resetPassword() {
        String email = mEmailEditText.getText().toString();

        FirebaseAuth auth = FirebaseAuth.getInstance();

        Log.i(TAG, email);

        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    dialog.dismiss();
                    if (task.isSuccessful()) {
                        Toast.makeText(ForgotPasswordActivity.this, R.string.sent_temp_password,
                            Toast.LENGTH_LONG).show();

                        changeUserUsedTempToTrue();

                        Intent intent = new Intent(ForgotPasswordActivity.this, StuckLoginActivity.class);
                        startActivity(intent);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                     dialog.dismiss();
                    Log.i(TAG, e.getMessage());
                    new AlertDialog.Builder(ForgotPasswordActivity.this)
                        .setTitle(getString(R.string.forgot_password_error_title))
                        .setMessage(getString(R.string.forgot_password_error))
                        .show();
                }
            });
    }


    /**
     * Updates the users firebase to show that they are logged in with a temp password
     * so that they later rest their password
     */
    private void changeUserUsedTempToTrue() {

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS)
            .child(StuckSignUpActivity.encodeEmail(mEmailEditText.getText().toString()));

        Map<String, Object> changePasswordOnLogin = new HashMap<>();
        changePasswordOnLogin.put(StuckConstants.USER_LOGGED_IN_WITH_TEMP_PASSWORD, true);
        mRef.updateChildren(changePasswordOnLogin);
    }

    @Override
    protected void onStop(){
        super.onStop();
        FirebaseAuth.getInstance().signOut();
    }

}
