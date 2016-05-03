package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import butterknife.BindView;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class StuckSignUpActivity extends AppCompatActivity {

    @BindView(R.id.telaphone_number_edit_text)
    EditText mPhoneNumberEditText;

    @BindView(R.id.sms_code_edit_text)
    EditText mSMSEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_account_activity);
    }
}
