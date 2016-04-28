package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.CardViewListAdapter;
import com.example.sammengistu.stuck.model.StuckPost;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;

public class StuckMainListActivity extends AppCompatActivity implements View.OnClickListener
{

    private RecyclerView mRecyclerViewQuestions;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Toolbar mMainListToolbar;
    private FloatingActionButton mNewPostFAB;

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

        setUpToolbar();
        setUpFloatingActionButton();


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

    private void setUpToolbar() {
        // my_child_toolbar is defined in the layout file
        mMainListToolbar =
            (Toolbar) findViewById(R.id.new_stuck_post_toolbar);
        setSupportActionBar(mMainListToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        getSupportActionBar().setDisplayShowTitleEnabled(true);
    }

    private void setUpFloatingActionButton(){
        mNewPostFAB = (FloatingActionButton) findViewById(R.id.fab_add);

        int actionBarHeight = 10;
        // Calculate ActionBar height
        TypedValue tv = new TypedValue();
        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        }

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        );

        params.gravity = Gravity.END;
        params.setMargins(0, actionBarHeight / 2, 16, 0);
        mNewPostFAB.setLayoutParams(params);

        mNewPostFAB.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, StuckNewPostActivity.class);
        startActivity(intent);
    }
}
