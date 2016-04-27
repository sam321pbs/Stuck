package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.ChoicesAdapter;
import com.example.sammengistu.stuck.model.StuckPost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class StuckVoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewSingleQuestion;
    private RecyclerView mRecyclerViewChoices;


    private RecyclerView.Adapter mAdapterQuestions;
    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerQuestions;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPost mStuckPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_vote);

        // use a linear layout manager
        mLayoutManagerQuestions = new LinearLayoutManager(this);
        mLayoutManagerChoices = new LinearLayoutManager(this);

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

//        setUpRecyclerViewQuestions();
        setUpRecyclerViewChoices();

    }

//    private void setUpRecyclerViewQuestions() {
//        mRecyclerViewSingleQuestion = (RecyclerView) findViewById(R.id.recycler_view_question_vote);
//
//
//        mRecyclerViewSingleQuestion.setLayoutManager(mLayoutManagerQuestions);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
//        mRecyclerViewSingleQuestion.setHasFixedSize(true);
//
//
//        List<StuckPost> stuckPost = new ArrayList<>();
//        stuckPost.add(mStuckPost);
//
//        mAdapterQuestions = new CardViewListAdapter(stuckPost);
//        mRecyclerViewSingleQuestion.setAdapter(mAdapterQuestions);
//    }

    private void setUpRecyclerViewChoices() {
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);


        mRecyclerViewChoices.setLayoutManager(mLayoutManagerChoices);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewChoices.setHasFixedSize(true);

        List<String> stuckPostChoices = new ArrayList<>();
        stuckPostChoices.add(mStuckPost.getChoice1());
        stuckPostChoices.add(mStuckPost.getChoice2());
        stuckPostChoices.add(mStuckPost.getChoice3());
        stuckPostChoices.add(mStuckPost.getChoice4());


        mAdapterChoices = new ChoicesAdapter(stuckPostChoices);
        mRecyclerViewChoices.setAdapter(mAdapterChoices);
    }
}
