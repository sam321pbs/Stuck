package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.VoteChoicesAdapter;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.model.VoteChoice;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckVoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChoices;
    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPostSimple mStuckPostSimple;

    @BindView(R.id.vote_toolbar)
    Toolbar mVoteToolbar;
    @BindView(R.id.single_item_question)
    TextView mQuestion;
    @BindView(R.id.sneak_peak_choice_1)
    TextView mSneakPeakChoice;
    @BindView(R.id.post_location)
    TextView mPostLocation;
    @BindView(R.id.delete_post_image_view)
    ImageView mDeleteImageView;

    @OnClick(R.id.delete_post_image_view)
    public void setDeleteImageView(View view) {
        mRefPost.removeValue();
    }

    private Firebase mRefPost;
    private List<VoteChoice> mStuckPostChoices;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            mStuckPostSimple = dataSnapshot.getValue(StuckPostSimple.class);
            mStuckPostChoices.clear();
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
                false, mStuckPostSimple.getChoiceOneVotes()));
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
                false, mStuckPostSimple.getChoiceTwoVotes()));
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
                false, mStuckPostSimple.getChoiceThreeVotes()));
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
                false, mStuckPostSimple.getChoiceFourVotes()));
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

        setUpToolbar();

        mStuckPostSimple = new StuckPostSimple(
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
            new HashMap<String, Object>()
        );

        mRefPost = new Firebase(getIntent().getStringExtra(StuckConstants.FIREBASE_REF));


        mQuestion.setText(mStuckPostSimple.getQuestion());
        mSneakPeakChoice.setText("");
        mPostLocation.setText(mStuckPostSimple.getLocation());

        setUpRecyclerViewChoices();
    }

    private void setUpRecyclerViewChoices() {
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);


        mRecyclerViewChoices.setLayoutManager(mLayoutManagerChoices);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewChoices.setHasFixedSize(true);


        mStuckPostChoices = new ArrayList<>();
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
            false, mStuckPostSimple.getChoiceOneVotes()));
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
            false, mStuckPostSimple.getChoiceTwoVotes()));
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
            false, mStuckPostSimple.getChoiceThreeVotes()));
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
            false, mStuckPostSimple.getChoiceFourVotes()));

        mAdapterChoices = new VoteChoicesAdapter(mStuckPostChoices, this,
            getIntent().getStringExtra(StuckConstants.FIREBASE_REF));
        mRecyclerViewChoices.setAdapter(mAdapterChoices);

        mRefPost.addValueEventListener(mValueEventListener);
    }

    private void setUpToolbar() {

        setSupportActionBar(mVoteToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefPost.removeEventListener(mValueEventListener);
    }
}
