package com.example.sammengistu.stuck.activities;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.CardViewListFBAdapter;
import com.example.sammengistu.stuck.dialogs.FilterDialog;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.stuck_offline_db.ContentProviderStuck;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.ui.FirebaseRecyclerAdapter;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckMainListActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "StuckMainListActivity55";
    private String mEmail;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mFirebaseRef;
    private List<StuckPostSimple> stuckPostsLoaded;

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
                    takeUserToLoginScreenOnUnAuth(StuckMainListActivity.this);
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

    public static void takeUserToLoginScreenOnUnAuth(Activity activity) {
        Intent intent = new Intent(activity, StuckLoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
    }

    /**
     * Checks network status then loads active posts
     */
    private void initializeAdapter() {
        if (!NetworkStatus.isOnline(StuckMainListActivity.this)) {
            NetworkStatus.showOffLineDialog(StuckMainListActivity.this);
        } else {
            Firebase ref = new Firebase(StuckConstants.FIREBASE_URL)
                .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

            mAdapter = new CardViewListFBAdapter(StuckPostSimple.class,
                R.layout.stuck_single_item_question,
                CardViewListFBAdapter.CardViewListADViewHolder.class, ref,
                StuckMainListActivity.this);

            mRecyclerViewQuestions.setAdapter(mAdapter);
        }
    }

    private void setUpToolbar() {
        setSupportActionBar(mMainListToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    /**
     * Sets up floating action bar based on the screen size
     * if the width of the screen is less then 600pixels it
     * will show th action button on the top of the screen
     */
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
    protected void onResume() {
        super.onResume();
        //Check if db has user posts
        getLoaderManager().initLoader(StuckConstants.LOADER_ID, null, this);
        initializeAdapter();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((FirebaseRecyclerAdapter) mAdapter).cleanup();
        mFirebaseRef.removeAuthStateListener(mAuthListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
            StuckConstants.TABLE_OFFLINE_POST);

        CursorLoader loader = new CursorLoader(
            this,
            contentUri,
            null,
            null,
            null,
            null);
        return loader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        stuckPostsLoaded = new ArrayList<>();
        Log.i(TAG, "Load finished part 2");
        data.moveToFirst();

        //Got from http://stackoverflow.com/questions/10111166/get-all-rows-from-sqlite
        if (data.moveToFirst()) {

            while (!data.isAfterLast()) {

                String stuckEmail = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_EMAIL));

                String stuckQuestion = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_QUESTION));

                String stuckLocation = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_LOCATION));

                String stuckChoice1 = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_ONE));

                String stuckChoice2 = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_TWO));

                String stuckChoice3 = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_THREE));

                String stuckChoice4 = data.getString(data
                    .getColumnIndex(StuckConstants.COLUMN_CHOICE_FOUR));

                StuckPostSimple stuckPostSimple = new StuckPostSimple(StuckSignUpActivity.encodeEmail(stuckEmail), stuckQuestion,
                    stuckLocation, stuckChoice1, stuckChoice2, stuckChoice3, stuckChoice4,
                    StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                    StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                    new HashMap<String, Object>());

                Log.i(TAG, stuckPostSimple.getEmail() + " " + stuckPostSimple.getQuestion());

                stuckPostsLoaded.add(stuckPostSimple);
                data.moveToNext();
            }
        }
        addNewPostsToFirebase();
    }

    /**
     * If their are posts in the local database you can add them to firebase then delete them
     */
    private void addNewPostsToFirebase (){
        if (stuckPostsLoaded.size() > 0 && NetworkStatus.isOnline(this)) {
            for (int i = 0; i < stuckPostsLoaded.size(); i++) {
                StuckPostSimple stuckPostSimple = stuckPostsLoaded.get(i);
                //Push the post straight to firebase
                new Firebase(StuckConstants.FIREBASE_URL).child(
                    StuckConstants.FIREBASE_URL_ACTIVE_POSTS).push().setValue(stuckPostSimple);

                Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                    StuckConstants.TABLE_OFFLINE_POST);

                Log.i(TAG, "Deleted db = " + this.getContentResolver().delete(contentUri,
                    StuckConstants.COLUMN_QUESTION + " = ?", new String[]{stuckPostSimple.getQuestion()}));

            }

            getLoaderManager().restartLoader(StuckConstants.LOADER_ID, null, this);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @OnClick(R.id.filter_stuck_posts)
    public void showFilter(View view) {

        FilterDialog filterDialog = new FilterDialog();
        filterDialog.show(getSupportFragmentManager(), "Filter");
    }

    @OnClick(R.id.fab_add)
    public void setNewPostFAB(View view) {

        Intent intent = new Intent(this, StuckNewPostActivity.class);
        startActivity(intent);

    }
}
