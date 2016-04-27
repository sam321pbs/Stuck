package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;


public class StuckVoteActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewSingleQuestion;
    private RecyclerView mRecyclerViewChoices;


    private RecyclerView.Adapter mAdapterQuestions;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_vote);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        setUpRecyclerViewQuestions();



    }

    private void setUpRecyclerViewQuestions(){
        mRecyclerViewSingleQuestion = (RecyclerView) findViewById(R.id.recycler_view_question_vote);


        mRecyclerViewSingleQuestion.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewSingleQuestion.setHasFixedSize(true);


//        mAdapterQuestions = new CardViewListAdapter(myDataset);
        mRecyclerViewSingleQuestion.setAdapter(mAdapterQuestions);
    }

    private void setUpRecyclerViewChoices(){
        mRecyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);


        mRecyclerViewChoices.setLayoutManager(mLayoutManager);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewChoices.setHasFixedSize(true);


//        mAdapterQuestions = new CardViewListAdapter(myDataset);
        mRecyclerViewChoices.setAdapter(mAdapterQuestions);
    }
}
