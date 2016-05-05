package com.example.sammengistu.stuck.activities;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.CardViewListFBAdapter;
import com.example.sammengistu.stuck.dialogs.FilterDialog;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckMainListActivity extends AppCompatActivity {

    private static final String TAG = "StuckMainListActivity";
    @BindView(R.id.main_list_stuck_toolbar)
    Toolbar mMainListToolbar;
    @BindView(R.id.fab_add)
    FloatingActionButton mNewPostFAB;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.filter_stuck_posts)
    TextView mFilterTextView;
    @BindView(R.id.recycler_view_question_post)
    RecyclerView mRecyclerViewQuestions;

    @OnClick(R.id.filter_stuck_posts)
    public void showFilter(View view) {

        FilterDialog filterDialog = new FilterDialog();
        filterDialog.show(getSupportFragmentManager(), "Filter");
    }

    @OnClick(R.id.fab_add)
    public void setNewPostFAB(View view) {

        mFirebaseRef.unauth();
//        Intent intent = new Intent(this, StuckNewPostActivity.class);
//        intent.putExtra(StuckConstants.PASSED_IN_EMAIL, mEmail);
//        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(intent,
//                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//        } else {
//
//            startActivity(intent);
//        }
    }

    private String mEmail;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mFirebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stuck_main_list);

        ButterKnife.bind(this);

        mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS);
        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                /* The user has been logged out */
                if (authData == null) {

                    Log.i(TAG, "USer has been logged out");
                    takeUserToLoginScreenOnUnAuth();
                } else {
                    //not logged out
                    Log.i(TAG, "USer not been logged out");
                    Log.i(TAG, authData.getProviderData().containsKey("email") + " " + authData.getProviderData().get("email"));
                }
            }
        };
        mFirebaseRef.addAuthStateListener(mAuthListener);

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode
        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
            .setRequestAgent("android_studio:ad_template").build();
        adView.loadAd(adRequest);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);

        if (mRecyclerViewQuestions != null) {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            mRecyclerViewQuestions.setHasFixedSize(true);
            mRecyclerViewQuestions.setLayoutManager(mLayoutManager);
        }

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initializeAdapter();
                mAdapter.notifyDataSetChanged();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        setUpToolbar();
        setUpFloatingActionButton();
        Toast.makeText(this, mEmail, Toast.LENGTH_LONG).show();
    }

    private void takeUserToLoginScreenOnUnAuth() {
        Intent intent = new Intent(this, StuckLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void initializeAdapter() {
        Firebase ref = new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

        mAdapter = new CardViewListFBAdapter(StuckPostSimple.class,
            R.layout.stuck_single_item_question,
            CardViewListFBAdapter.CardViewListADViewHolder.class, ref,
            StuckMainListActivity.this, mEmail);

        mRecyclerViewQuestions.setAdapter(mAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Todo: load up posts
        initializeAdapter();

    }

    //TODO: implement logic to load
    private void swipeRefreshingUI() {
//        mSwipeRefreshLayout.setRefreshing(mIsRefreshing);
    }

    private void setUpToolbar() {
        setSupportActionBar(mMainListToolbar);

        if (getSupportActionBar() != null) {
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        mFirebaseRef.removeAuthStateListener(mAuthListener);
    }

}
