package com.example.sammengistu.stuck;

import com.example.sammengistu.stuck.model.StuckPost;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class StuckMainListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("StuckMainList", "CardView break");
        setContentView(R.layout.activity_stuck_main_list);

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        List<StuckPost> myDataset = new ArrayList<>();

        myDataset.add(new StuckPost(
            "Where should I take my girl friend to Dinner?",
            "Chipotle",
            "Mcdonald's",
            "Five guys",
            "Red Lobster"));

//        mAdapter = new MyAdapter(new String[]{"Zain", "Nadeem"});

        // specify an adapter (see also next example)
        mAdapter = new CardViewAdapter(myDataset);
        mRecyclerView.setAdapter(mAdapter);
    }
}
