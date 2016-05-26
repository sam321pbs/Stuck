package com.example.sammengistu.stuck.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.GeneralArea;
import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.CardViewQuestionAdapter;
import com.example.sammengistu.stuck.adapters.MyPostsAdapter;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.stuck_offline_db.ContentProviderStuck;
import com.example.sammengistu.stuck.stuck_offline_db.StuckDBConverter;
import com.example.sammengistu.stuck.viewHolders.MyPostListADViewHolder;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckMainListActivity extends AppCompatActivity
    implements LoaderManager.LoaderCallbacks<Cursor>,
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
    AdapterView.OnItemSelectedListener {

    private static final String TAG = "StuckMainListActivity55";

    private String mEmail;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private List<StuckPostSimple> stuckPostsLoaded;
    private GoogleApiClient mGoogleApiClient;
    private boolean mShowMyPosts;
    private List<MyPostListADViewHolder.PostWithFBRef> stuckPostList;

    private DatabaseReference mActivePostsRef;

    @BindView(R.id.main_list_stuck_toolbar)
    Toolbar mMainListToolbar;
    @BindView(R.id.fab_add)
    FloatingActionButton mNewPostFAB;
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    //    @BindView(R.id.filter_stuck_posts)
//    TextView mFilterTextView;
    @BindView(R.id.recycler_view_question_post)
    RecyclerView mRecyclerViewQuestions;
    @BindView(R.id.my_posts_main_list)
    TextView mMyPosts;

    private FirebaseAuth mAuth;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_stuck_main_list);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        stuckPostList = new ArrayList<>();

        mShowMyPosts = false;

        mActivePostsRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    takeUserToLoginScreenOnUnAuth(StuckMainListActivity.this);
                } else {
                    Log.i(TAG, "User email = " + user.getEmail());
                }
            }

//            @Override
//            public void onAuthStateChanged(AuthData authData) {
//                /* The user has been logged out */
//                if (authData == null) {
//                    takeUserToLoginScreenOnUnAuth(StuckMainListActivity.this);
//                }
//            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode

        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mGoogleApiClient.connect();

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
                showMyPosts(null);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

        initializeAdapter();
        setUpToolbar();
        Log.i(TAG, "onCreate");

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

        Query byTimeStampQueryRef = mActivePostsRef.orderByChild(StuckConstants.DATE_TIME_STAMP);
        stuckPostList = new ArrayList<>();

        final List<StuckPostSimple> stuckPostSimples = new ArrayList<>();

        if (!NetworkStatus.isOnline(StuckMainListActivity.this)) {
            NetworkStatus.showOffLineDialog(StuckMainListActivity.this);

        } else {

            byTimeStampQueryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    for (DataSnapshot stuckSnap : dataSnapshot.getChildren()) {

                        StuckPostSimple stuckPostSimple = stuckSnap.getValue(StuckPostSimple.class);
                        stuckPostSimple.setDatabaseReference(stuckSnap.getRef());
                        Log.i(TAG, "Stuckpost = " + stuckPostSimple.getEmail());

                        MyPostListADViewHolder.PostWithFBRef postWithFBRef =
                            new MyPostListADViewHolder.PostWithFBRef(
                                stuckSnap.getRef().toString(), stuckPostSimple);

                        stuckPostList.add(postWithFBRef);
                        stuckPostSimples.add(stuckPostSimple);

                    }
                    mAdapter = new CardViewQuestionAdapter(stuckPostSimples, StuckMainListActivity.this);
                    Log.i(TAG, "Adapter called");
                    mRecyclerViewQuestions.setAdapter(mAdapter);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            Log.i(TAG, "StuckPost size = " + stuckPostList.size());
        }

        mAdapter = new MyPostsAdapter(stuckPostList, StuckMainListActivity.this);
        Log.i(TAG, "Adapter called");
        mRecyclerViewQuestions.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMainListToolbar.inflateMenu(R.menu.menu_main);
        mMainListToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                return onOptionsItemSelected(item);
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_my_posts:
                mShowMyPosts = !mShowMyPosts;
                showMyPosts(item);
                break;
            case R.id.action_log_out:
                Log.i(TAG, "Tap log out");
                FirebaseAuth.getInstance().signOut();
                break;

            case R.id.action_delete_account:

                new AlertDialog.Builder(this)
                    .setTitle(getString(R.string.delete_account_dialog_title))
                    .setMessage(getString(R.string.delete_account_dialog_message))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            Log.i(TAG, "User = " + user.getEmail());
                            if (user != null) {
                                String userEncodedEmail = StuckSignUpActivity.encodeEmail(user.getEmail());
                                //Delete all posts
                                DatabaseReference votesRef = FirebaseDatabase.getInstance().getReference()
                                    .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

                                Query queryRef = FirebaseDatabase.getInstance().getReference()
                                    .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS)
                                    .orderByChild(StuckConstants.FIREBASE_EMAIL)
                                    .equalTo(userEncodedEmail);

                                deleteAccount(user, queryRef, userEncodedEmail);

                            }
                        }
                    })
                    .setNegativeButton(getString(R.string.no_thanks), null)
                    .show();
                break;

            default:
                break;
        }

        return true;
    }

    /**
     * Deletes the users account from Auth
     * Deletes users saved votes
     * Deletes users account off the database
     * @param user - used to delete user
     * @param queryRef - to delete all posts
     * @param userEncodedEmail - to delete votes and account info from db
     */
    private void deleteAccount(FirebaseUser user, final Query queryRef, String userEncodedEmail) {

        //Delete user votes
        final DatabaseReference userVoteRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS_VOTES)
            .child(userEncodedEmail);

        //delete user account from db
        final DatabaseReference userRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_USERS)
            .child(userEncodedEmail);

       // Delete user auth
        user.delete()
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        userVoteRef.removeValue();
                        userRef.removeValue();

                        deleteAllUsersStuckPosts(queryRef);

                        Toast.makeText(StuckMainListActivity.this,
                            R.string.account_was_deleted,
                            Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(StuckMainListActivity.this,
                            StuckLoginActivity.class);
                        startActivity(intent);
                    }
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.i(TAG, "failed to delete user");
                }
            });
    }

    /**
     * Deletes users posts by first querying all their posts and using the ref to remove values
     * @param queryRef - to delete all posts
     */
    private void deleteAllUsersStuckPosts(Query queryRef) {

        final List<DatabaseReference> userPosts = new ArrayList<>();

        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot stuckSnap : dataSnapshot.getChildren()) {

                    StuckPostSimple stuckPostSimple =
                        stuckSnap.getValue(StuckPostSimple.class);

                    stuckPostSimple.setDatabaseReference(stuckSnap.getRef());

                    userPosts.add(stuckSnap.getRef());
                }

                for (DatabaseReference userPostRef : userPosts) {
                    userPostRef.removeValue();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * querys all posts that are equal to the users email
     * then flips the returned results so the posts are in timestamp order
     */
    private void setMyPostAdapter() {

        stuckPostList = new ArrayList<>();

        Query queryRef = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS)
            .orderByChild(StuckConstants.FIREBASE_EMAIL)
            .equalTo(StuckSignUpActivity.encodeEmail(mEmail));

        Log.i(TAG, "Email in ref = " + mEmail + " Firebase ref = " + queryRef.toString());

        queryRef.addChildEventListener(
            new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Log.i(TAG, "FB ref = " + dataSnapshot.getRef().toString());

                    MyPostListADViewHolder.PostWithFBRef postWithFBRef =
                        new MyPostListADViewHolder.PostWithFBRef(
                            dataSnapshot.getRef().toString(), dataSnapshot.getValue(StuckPostSimple.class));

                    stuckPostList.add(postWithFBRef);

                    Log.i(TAG, "Email in qeury = " + postWithFBRef.getStuckPostSimple().getEmail());

                    List<MyPostListADViewHolder.PostWithFBRef> temp = new ArrayList<>();
                    //reverses the list to show most recent first
                    for (int i = stuckPostList.size() - 1; i >= 0; i--) {
                        temp.add(stuckPostList.get(i));
                    }

                    mAdapter = new MyPostsAdapter(temp, StuckMainListActivity.this);
                    mRecyclerViewQuestions.setAdapter(mAdapter);
                    mAdapter.notifyDataSetChanged();
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            }
        );
    }

    /**
     * If user selects show my posts calls the appropriate method to update the adapter
     * @param item - if menu item is null it will show all posts and if not shows users posts
     */
    private void showMyPosts(MenuItem item) {
        Log.i(TAG, "Show my post = " + mShowMyPosts);
        if (mShowMyPosts) {

            if (item != null) {
                item.setTitle("Show All Posts");
            }
            setMyPostAdapter();

        } else {

            if (item != null) {
                item.setTitle("Show My Posts");
            }
            initializeAdapter();
        }
        mAdapter.notifyDataSetChanged();
    }

    private void setUpToolbar() {
        mMainListToolbar.inflateMenu(R.menu.menu_main);

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

    private void getFirstPostToPutInDB() {
        Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
            StuckConstants.TABLE_OFFLINE_POST);

        getContentResolver().delete(contentUri,
            StuckConstants.COLUMN_MOST_RECENT_POST + " = ?",
            new String[]{StuckConstants.TRUE});

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference()
            .child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

        ref.addListenerForSingleValueEvent(
            new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                            if (dataSnapshot1 != null) {
                                StuckPostSimple stuckPostSimple = dataSnapshot1.getValue(StuckPostSimple.class);

                                Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                                    StuckConstants.TABLE_OFFLINE_POST);

                                getContentResolver().insert(contentUri,
                                    StuckDBConverter.insertStuckPostToDB(stuckPostSimple,
                                        StuckConstants.TRUE));
                            }
                            break;
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            }
        );
    }

    private Location getLastKnownLocation() {

        if (ActivityCompat.checkSelfPermission(this
            , Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {

            return null;
        }

        return LocationServices.FusedLocationApi.getLastLocation(
            mGoogleApiClient);
    }

    /**
     * If their are posts in the local database you can add them to firebase then delete them
     * from the local database
     */
    private void addNewPostsToFirebase() {
        if (stuckPostsLoaded.size() > 0 && NetworkStatus.isOnline(this)) {
            for (int i = 0; i < stuckPostsLoaded.size(); i++) {
                StuckPostSimple stuckPostSimple = stuckPostsLoaded.get(i);
                stuckPostSimple.setLocation(GeneralArea.getAddressOfCurrentLocation(getLastKnownLocation(), this));
                //Push the post straight to firebase
                FirebaseDatabase.getInstance().getReference().child(
                    StuckConstants.FIREBASE_URL_ACTIVE_POSTS).push().setValue(stuckPostSimple);

                Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                    StuckConstants.TABLE_OFFLINE_POST);

                Log.i(TAG, "Deleted db = " + this.getContentResolver().delete(contentUri,
                    StuckConstants.COLUMN_QUESTION + " = ?", new String[]{stuckPostSimple.getQuestion()}));

            }

            getLoaderManager().restartLoader(StuckConstants.LOADER_ID, null, this);
        }
    }

    /**
     * Sets up window animation if build version is greater than 21
     * Uses explode to enter or exit
     */
    private void setupWindowAnimations() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Explode explode = new Explode();
            explode.setDuration(1000);
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            getWindow().setEnterTransition(explode);

            Slide slide = new Slide();
            slide.setDuration(1000);
            getWindow().setReturnTransition(slide);

            // set an exit transition
            getWindow().setExitTransition(new Explode());
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
            StuckConstants.TABLE_OFFLINE_POST);

        return new CursorLoader(
            this,
            contentUri,
            null,
            null,
            null,
            null);
    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        stuckPostsLoaded = new ArrayList<>();
        data.moveToFirst();
        //Got from http://stackoverflow.com/questions/10111166/get-all-rows-from-sqlite
        if (data.moveToFirst()) {

            while (!data.isAfterLast()) {

                //Loads posts that were saved offline
                if (data.getString(data.getColumnIndex(StuckConstants.COLUMN_MOST_RECENT_POST))
                    .equals(StuckConstants.FALSE)) {

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

                    StuckPostSimple stuckPostSimple = new StuckPostSimple(
                        StuckSignUpActivity.encodeEmail(stuckEmail), stuckQuestion,
                        stuckLocation,
                        stuckChoice1, stuckChoice2, stuckChoice3, stuckChoice4,
                        StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                        StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
                        new HashMap<String, Object>(), (-1 * new Date().getTime()));

                    Log.i(TAG, stuckPostSimple.getEmail() + " " + stuckPostSimple.getQuestion());

                    stuckPostsLoaded.add(stuckPostSimple);
                }
                data.moveToNext();
            }
        }
        addNewPostsToFirebase();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @OnClick(R.id.fab_add)
    public void setNewPostFAB(View view) {
        Intent intent = new Intent(this, StuckNewPostActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode

        if (pref.getBoolean(StuckConstants.USER_MADE_OFFLINE_POST, true)) {
            //Check if db has user posts
            getLoaderManager().initLoader(StuckConstants.LOADER_ID, null, this);
            getFirstPostToPutInDB();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAuth.removeAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @OnClick(R.id.my_posts_main_list)
    public void myPosts() {


    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
