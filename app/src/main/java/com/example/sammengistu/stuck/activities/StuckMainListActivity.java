package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.CardViewListAdapter;
import com.example.sammengistu.stuck.model.StuckPost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class StuckMainListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerViewQuestions;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_stuck_main_list);

        mRecyclerViewQuestions = (RecyclerView) findViewById(R.id.recycler_view_question_post);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerViewQuestions.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerViewQuestions.setLayoutManager(mLayoutManager);

        List<StuckPost> myDataset = new ArrayList<>();

        myDataset.add(new StuckPost(
            "Where should I take my girl friend to Dinner?",
            "Chipotle",
            "Mcdonald's",
            "Five guys",
            "Red Lobster",
            "College Park, MD"));

        myDataset.add(new StuckPost(
            "Where should I travel to next?",
            "Italy",
            "France",
            "Canada",
            "Ethiopia",
            "Arlington, VA"));




        // specify an adapter (see also next example)
        mAdapter = new CardViewListAdapter(myDataset, this, StuckVoteActivity.class);
        mRecyclerViewQuestions.setAdapter(mAdapter);
    }
}
