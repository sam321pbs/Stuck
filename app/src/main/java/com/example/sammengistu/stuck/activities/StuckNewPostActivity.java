package com.example.sammengistu.stuck.activities;

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

public class StuckNewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private static String TAG = "StuckNewPostActivity";
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

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;

    private String avtivePost = "activePost";
    private Firebase mRef;
    private Firebase mNewListRef;
    private String mEmail;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stuck_post);
        ButterKnife.bind(this);
        Firebase.setAndroidContext(this);

        setUpToolbar();

        initializeViews();

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences("UserPref", 0); // 0 - for private mode
        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL);
        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                /* The user has been logged out */
                if (authData == null) {

                    Log.i(TAG, "USer has been logged out");
//                    takeUserToLoginScreenOnUnAuth();
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

    private void initializeViews() {

        mAddChoiceButton.setOnClickListener(this);
    }

    private void setUpToolbar() {

        setSupportActionBar(mNewPostToolbar);

        mNewPostDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check all boxs are filled
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
                                "",
                                "",
                                0,
                                0,
                                0,
                                0,
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
                                "",
                                0,
                                0,
                                0,
                                0,
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
                                0,
                                0,
                                0,
                                0,
                                timestampCreated);
                    }

                    mNewListRef.setValue(stuckPost);


//                ref.push().setValue(stuckPost);
//                    ref.child(avtivePost).setValue(stuckPost);

//                    ref.addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//
//                            StuckPost stuckPost1 = dataSnapshot.getValue(StuckPost.class);
//
//                            Log.i("Firebase Data", "Data changed " + stuckPost1.getChoice1());
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });
                    Intent intent = new Intent(StuckNewPostActivity.this, StuckMainListActivity.class);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder fillEveryThingDialog = new AlertDialog.Builder(StuckNewPostActivity.this);
                    fillEveryThingDialog.setTitle("Please fill all boxes");
                    fillEveryThingDialog.show();
                    fillEveryThingDialog.setCancelable(true);
                }
            }
        });

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
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
    public void onClick(View v) {
//        Firebase ref = new Firebase(StuckConstants.FIREBASE_URL).child(avtivePost);
//
//        ref.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                StuckPostSimple stuckPost1 = dataSnapshot.getValue(StuckPostSimple.class);
//
//                if (stuckPost1 != null) {
//                    Log.i("Firebase Data", "Data changed " + stuckPost1.getChoiceOne());
//                }
//            }
//
//            @Override
//            public void onCancelled(FirebaseError firebaseError) {
//
//            }
//        });
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

    @Override
    public void onDestroy(){
        super.onDestroy();


    }
}
