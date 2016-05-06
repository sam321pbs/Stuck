package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.MyPostChoiceAdapter;
import com.example.sammengistu.stuck.model.Choice;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckNewPostActivity extends AppCompatActivity {

    private static String TAG = "StuckNewPostActivity";
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;
    private Firebase mRef;
    private Firebase mNewListRef;
    private String mEmail;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mFirebaseRef;

    @BindView(R.id.my_post_edit_text)
    EditText mQuestionEditText;

    @BindView(R.id.recycler_view_single_choice_view)
    RecyclerView mMyChoicesRecyclerView;

    @BindView(R.id.add_choice_button)
    FloatingActionButton mAddChoiceButton;

    @BindView(R.id.new_post_done)
    TextView mNewPostDone;

    @BindView(R.id.new_stuck_post_toolbar)
    Toolbar mNewPostToolbar;

    @OnClick(R.id.add_choice_button)
    public void setAddChoiceButton() {

        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (mChoicesList.size() == 4) {
            mAddChoiceButton.setVisibility(View.INVISIBLE);
            mAddChoiceButton.setEnabled(false);
        }
    }

    @OnClick(R.id.new_post_done)
    public void setNewPostDone(){
        if (NetworkStatus.isOnline(this)) {
            if (isQuestionFilled() && isAllChoicesFilled()) {
                /**
                 * Create Firebase references
                 */
                    /* Save listsRef.push() to maintain same random Id */
                final String listId = mNewListRef.getKey();

                /**
                 * Set raw version of date to the ServerValue.TIMESTAMP value and save into
                 * timestampCreatedMap
                 */
                HashMap<String, Object> timestampCreated = new HashMap<>();
                timestampCreated.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    /* Add a posy list */

                StuckPostSimple stuckPost = new StuckPostSimple();
                switch (mChoicesList.size()) {
                    case 2:
                        stuckPost = new StuckPostSimple(
                            mEmail,
                            mQuestionEditText.getText().toString(),
                            "College Park",
                            mChoicesList.get(0).getChoice(),
                            mChoicesList.get(1).getChoice(),
                            "", "", 0, 0, 0, 0,
                            timestampCreated);
                        break;

                    case 3:
                        stuckPost = new StuckPostSimple(
                            mEmail,
                            mQuestionEditText.getText().toString(),
                            "College Park",
                            mChoicesList.get(0).getChoice(),
                            mChoicesList.get(1).getChoice(),
                            mChoicesList.get(2).getChoice(),
                            "", 0, 0, 0, 0,
                            timestampCreated);
                        break;
                    case 4:
                        stuckPost = new StuckPostSimple(
                            mEmail,
                            mQuestionEditText.getText().toString(),
                            "College Park",
                            mChoicesList.get(0).getChoice(),
                            mChoicesList.get(1).getChoice(),
                            mChoicesList.get(2).getChoice(),
                            mChoicesList.get(3).getChoice(),
                            0, 0, 0, 0,
                            timestampCreated);
                }

                mNewListRef.setValue(stuckPost);

                Intent intent = new Intent(StuckNewPostActivity.this, StuckMainListActivity.class);
                startActivity(intent);

            } else {
                AlertDialog.Builder fillEveryThingDialog = new AlertDialog.Builder(StuckNewPostActivity.this);
                fillEveryThingDialog.setTitle("Please fill all boxes");
                fillEveryThingDialog.show();
                fillEveryThingDialog.setCancelable(true);
            }
        } else {
            NetworkStatus.showOffLineDialog(this);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stuck_post);
        ButterKnife.bind(this);

        setUpToolbar();

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode
        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL);
        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                /* The user has been logged out */
                if (authData == null) {
                    Log.i(TAG, "USer has been logged out");
                    StuckMainListActivity.takeUserToLoginScreenOnUnAuth(StuckNewPostActivity.this);
                } else {
                    //not logged out
                    Log.i(TAG, "USer not been logged out");
                }
            }
        };

        mFirebaseRef.addAuthStateListener(mAuthListener);

        mRef = new Firebase(StuckConstants.FIREBASE_URL).child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);
        mNewListRef = mRef.push();

        mMyChoicesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(mLayoutManager);

        mChoicesList = new ArrayList<>();

        mChoicesList.add(new Choice("", 0));
        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }

    private void setUpToolbar() {

        setSupportActionBar(mNewPostToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab !=null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private boolean isQuestionFilled() {
        return !mQuestionEditText.getText().toString().equals("");
    }

    private boolean isAllChoicesFilled() {
        for (Choice choice : mChoicesList) {
            Log.i("NewPost", "current choice = " + choice.getChoice());
            if (choice.getChoice().equals("")) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mRef.removeAuthStateListener(mAuthListener);
    }
}
