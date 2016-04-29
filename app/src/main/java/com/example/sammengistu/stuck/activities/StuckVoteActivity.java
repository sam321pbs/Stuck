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


public class StuckVoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewChoices;
    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPost mStuckPost;
    private Toolbar mVoteToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_vote);

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

        TextView mQuestion = (TextView) findViewById(R.id.single_item_question);
        TextView sneakPeakChoice = (TextView) findViewById(R.id.sneak_peak_choice_1);
        TextView postLocation = (TextView) findViewById(R.id.post_location);

        mQuestion.setText(mStuckPost.getQuestion());
        sneakPeakChoice.setText("");
        postLocation.setText(mStuckPost.getStuckPostLocation());

        setUpRecyclerViewChoices();
    }

    private void setUpRecyclerViewChoices() {
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);


        mRecyclerViewChoices.setLayoutManager(mLayoutManagerChoices);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewChoices.setHasFixedSize(true);

//        List<String> stuckPostChoices = new ArrayList<>();
        List<VoteChoice> stuckPostChoices = new ArrayList<>();
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice1(), false, 10));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice2(), false, 1));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice3(), false, 4));
        stuckPostChoices.add(new VoteChoice(mStuckPost.getChoice4(), false, 9));

        mAdapterChoices = new VoteChoicesAdapter(stuckPostChoices, this);
        mRecyclerViewChoices.setAdapter(mAdapterChoices);
    }

    private void setUpToolbar(){
        // my_child_toolbar is defined in the layout file
        mVoteToolbar =
            (Toolbar) findViewById(R.id.vote_toolbar);
        setSupportActionBar(mVoteToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }
}
