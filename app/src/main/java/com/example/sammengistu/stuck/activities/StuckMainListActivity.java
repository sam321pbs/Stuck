package com.example.sammengistu.stuck.activities;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.CardViewListAdapter;
import com.example.sammengistu.stuck.dialogs.FilterDialog;
import com.example.sammengistu.stuck.model.StuckPost;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckMainListActivity extends AppCompatActivity {

    @BindView(R.id.main_list_stuck_toolbar)
    Toolbar mMainListToolbar;
    @BindView(R.id.fab_add)
    FloatingActionButton mNewPostFAB;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.filter_stuck_posts)
    TextView mFilterTextView;

    @OnClick(R.id.filter_stuck_posts)
    public void showFilter(View view) {
        FilterDialog filterDialog = new FilterDialog();
        filterDialog.show(getSupportFragmentManager(), "Filter");
    }

    @OnClick(R.id.fab_add)
    public void setNewPostFAB(View view) {
        Intent intent = new Intent(this, StuckNewPostActivity.class);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {

            startActivity(intent);
        }
    }

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_main_list);

        ButterKnife.bind(this);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
            .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        RecyclerView mRecyclerViewQuestions = (RecyclerView) findViewById(R.id.recycler_view_question_post);
        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        if (mRecyclerViewQuestions != null){
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerViewQuestions.setHasFixedSize(true);
        }

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

    //TODO: implement logic to load
    private void updateRefreshingUI() {
//        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private void setUpToolbar() {
        setSupportActionBar(mMainListToolbar);

        if(getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void setUpFloatingActionButton() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;

        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth <= 600) {
            //Device is a 7" tablet
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
        }
    }

}
