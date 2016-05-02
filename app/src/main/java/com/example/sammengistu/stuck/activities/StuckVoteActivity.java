package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.VoteChoicesAdapter;
import com.example.sammengistu.stuck.model.StuckPost;
import com.example.sammengistu.stuck.model.VoteChoice;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class StuckVoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChoices;
    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPost mStuckPost;

    @BindView(R.id.vote_toolbar) Toolbar mVoteToolbar;
    @BindView(R.id.single_item_question) TextView mQuestion;
    @BindView(R.id.sneak_peak_choice_1) TextView mSneakPeakChoice;
    @BindView(R.id.post_location) TextView mPostLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_vote);
        ButterKnife.bind(this);

        // use a linear layout manager
        mLayoutManagerChoices = new LinearLayoutManager(this);

        setUpToolbar();

        mStuckPost = new StuckPost(
            getIntent().getStringExtra(StuckConstants.QUESTION_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_1_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_2_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_3_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_4_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.LOCATION_VIEW_HOLDER)
        );

        mQuestion.setText(mStuckPost.getQuestion());
        mSneakPeakChoice.setText("");
        mPostLocation.setText(mStuckPost.getStuckPostLocation());

        setUpRecyclerViewChoices();
    }

    private void setUpRecyclerViewChoices() {
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);


        mRecyclerViewChoices.setLayoutManager(mLayoutManagerChoices);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewChoices.setHasFixedSize(true);

        List<VoteChoice> stuckPostChoices = new ArrayList<>();
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice1(), false, 10));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice2(), false, 1));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice3(), false, 4));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice4(), false, 9));

        mAdapterChoices = new VoteChoicesAdapter(stuckPostChoices, this);
        mRecyclerViewChoices.setAdapter(mAdapterChoices);
    }

    private void setUpToolbar(){

        setSupportActionBar(mVoteToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }


    }
}
