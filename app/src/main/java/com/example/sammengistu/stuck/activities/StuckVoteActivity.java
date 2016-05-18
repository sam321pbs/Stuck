package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.VoteChoicesAdapter;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.model.VoteChoice;
import com.firebase.client.AuthData;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckVoteActivity extends AppCompatActivity {

    private static String TAG = "StuckVoteActivity55";
    private RecyclerView mRecyclerViewChoices;
    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPostSimple mStuckPostSimple;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mRefPost;
    private Firebase mFirebaseRef;
    private List<VoteChoice> mStuckPostChoices;
    private SharedPreferences mSharedPreferences;

    @BindView(R.id.delete_post_image_view)
    ImageView mDeleteImageView;
    @BindView(R.id.vote_toolbar)
    Toolbar mVoteToolbar;
    @BindView(R.id.single_item_question)
    TextView mQuestion;
    @BindView(R.id.choice_one_title)
    TextView mChoiceOneTitle;
    @BindView(R.id.post_from_title)
    TextView mPostFromTitle;
    @BindView(R.id.total_votes_title)
    TextView mTotalVotesTitle;
//    @BindView(R.id.stuck_question_total_votes)
//    TextView mSneakPeakChoice;
    @BindView(R.id.stuck_question_total_votes)
    TextView mPostLocation;

    @BindView(R.id.sneak_peak_choice_1)
    TextView mStuckPostTotalVotes;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            mStuckPostSimple = dataSnapshot.getValue(StuckPostSimple.class);
            mStuckPostChoices.clear();
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
                false, mStuckPostSimple.getChoiceOneVotes()));
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
                false, mStuckPostSimple.getChoiceTwoVotes()));
            if (!mStuckPostSimple.getChoiceThree().equals("")) {
                mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
                    false, mStuckPostSimple.getChoiceThreeVotes()));
            }
            if (!mStuckPostSimple.getChoiceFour().equals("")) {
                mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
                    false, mStuckPostSimple.getChoiceFourVotes()));
            }

            int totalVotes = mStuckPostSimple.getChoiceOneVotes() +
                mStuckPostSimple.getChoiceTwoVotes() +
                mStuckPostSimple.getChoiceThreeVotes() +
                mStuckPostSimple.getChoiceFourVotes();

            mStuckPostTotalVotes.setText(totalVotes + "");
            mAdapterChoices.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(FirebaseError firebaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_vote);
        ButterKnife.bind(this);

        // use a linear layout manager
        mLayoutManagerChoices = new LinearLayoutManager(this);

        mSharedPreferences = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode

        mStuckPostSimple = new StuckPostSimple(
            getIntent().getStringExtra(StuckConstants.PASSED_IN_EMAIL),
            getIntent().getStringExtra(StuckConstants.QUESTION_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.LOCATION_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_1_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_2_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_3_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_4_VIEW_HOLDER),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            new HashMap<String, Object>(),
            (-1 * new Date().getTime())
        );

        mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL);
        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                /* The user has been logged out */
                if (authData == null) {

                    Log.i(TAG, "USer has been logged out");
                    StuckMainListActivity.takeUserToLoginScreenOnUnAuth(StuckVoteActivity.this);
                } else {
                    //not logged out
                    Log.i(TAG, "USer not been logged out");
                }
            }
        };

        setUpToolbar();
        mFirebaseRef.addAuthStateListener(mAuthListener);

        mRefPost = new Firebase(getIntent().getStringExtra(StuckConstants.FIREBASE_REF));

        mQuestion.setText(mStuckPostSimple.getQuestion());
//        mSneakPeakChoice.setText("");
        mPostLocation.setText(mStuckPostSimple.getLocation());

        int totalVotes = mStuckPostSimple.getChoiceOneVotes() +
            mStuckPostSimple.getChoiceTwoVotes() +
            mStuckPostSimple.getChoiceThreeVotes() +
            mStuckPostSimple.getChoiceFourVotes();

        mStuckPostTotalVotes.setText(totalVotes + "");
        mTotalVotesTitle.setText(R.string.post_from);
        mChoiceOneTitle.setText(R.string.total_votes);
        mPostFromTitle.setVisibility(View.INVISIBLE);
        setUpRecyclerViewChoices();
    }

    /**
     * Creates VoteChoices for all choices for the post and then creates the adapter
     */
    private void setUpRecyclerViewChoices() {
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);

        if (mRecyclerViewChoices != null) {
            mRecyclerViewChoices.setLayoutManager(mLayoutManagerChoices);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerViewChoices.setHasFixedSize(true);
        }

        mStuckPostChoices = new ArrayList<>();

        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
            false, mStuckPostSimple.getChoiceOneVotes()));
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
            false, mStuckPostSimple.getChoiceTwoVotes()));

        if (!mStuckPostSimple.getChoiceThree().equals("")) {
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
                false, mStuckPostSimple.getChoiceThreeVotes()));
        }

        if (!mStuckPostSimple.getChoiceFour().equals("")){
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
                false, mStuckPostSimple.getChoiceFourVotes()));
        }
        Firebase firebasePostRef = new Firebase(getIntent().getStringExtra(StuckConstants.FIREBASE_REF));

        mAdapterChoices = new VoteChoicesAdapter(mStuckPostChoices, this,
            getIntent().getStringExtra(StuckConstants.FIREBASE_REF), mStuckPostSimple,
            firebasePostRef.getKey());

        mRecyclerViewChoices.setAdapter(mAdapterChoices);

        mRefPost.addValueEventListener(mValueEventListener);
    }

    /**
     * Sets up the toolbar for the activity and if the current user is the creater of the post they
     * have the choice to delete it
     */
    private void setUpToolbar() {

        setSupportActionBar(mVoteToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        Log.i(TAG, "Post email = " + mStuckPostSimple.getEmail().replaceAll("\\s" , "") + " user email = "
            + mSharedPreferences.getString(StuckConstants.KEY_ENCODED_EMAIL, "").replaceAll("\\s" , ""));

        if (mStuckPostSimple.getEmail().replaceAll("\\s" , "").equals(
            mSharedPreferences.getString(
                StuckConstants.KEY_ENCODED_EMAIL, "").replaceAll("\\s" , ""))){

            mDeleteImageView.setEnabled(true);
            mDeleteImageView.setVisibility(View.VISIBLE);
        } else {

            mDeleteImageView.setEnabled(true);
            mDeleteImageView.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.delete_post_image_view)
    public void setDeleteImageView(View view) {
        mRefPost.removeEventListener(mValueEventListener);
        mRefPost.removeValue(new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                Intent intent = new Intent(StuckVoteActivity.this, StuckMainListActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefPost.removeEventListener(mValueEventListener);
    }
}
