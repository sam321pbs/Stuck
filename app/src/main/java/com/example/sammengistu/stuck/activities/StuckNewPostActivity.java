package com.example.sammengistu.stuck.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import com.example.sammengistu.stuck.GeneralArea;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.MyPostChoiceAdapter;
import com.example.sammengistu.stuck.asynctask.SaveToDBTask;
import com.example.sammengistu.stuck.model.Choice;
import com.example.sammengistu.stuck.model.StuckPostSimple;

import android.Manifest;
import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class StuckNewPostActivity extends AppCompatActivity implements
    GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static String TAG = "StuckNewPostActivity";

    private RecyclerView.Adapter mAdapter;
    private List<Choice> mChoicesList;
    private DatabaseReference mRefActivePosts;
    private String mEmail;
    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @BindView(R.id.my_post_edit_text)
    EditText mQuestionEditText;
    @BindView(R.id.recycler_view_single_choice_view)
    RecyclerView mMyChoicesRecyclerView;
    @BindView(R.id.add_choice_button)
    FloatingActionButton mAddChoiceButton;
    @BindView(R.id.new_post_done)
    TextView mNewPostDone;
    @BindView(R.id.new_stuck_post_toolbar)
    Toolbar mNewPostToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_new_stuck_post);
        ButterKnife.bind(this);

        setUpToolbar();

        mAuth = FirebaseAuth.getInstance();

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode
        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        DatabaseReference firebaseRef = FirebaseDatabase.getInstance().getReference();
        mRefActivePosts = firebaseRef.child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 /* The user has been logged out */
                if (firebaseAuth == null) {
                    Log.i(TAG, "USer has been logged out");
                    StuckMainListActivity.takeUserToLoginScreenOnUnAuth(StuckNewPostActivity.this);
                } else {
                    //not logged out
                    Log.i(TAG, "USer not been logged out");
                }
            }
        };

        mGoogleApiClient = new GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();

        mGoogleApiClient.connect();

        mMyChoicesRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(layoutManager);

        mChoicesList = new ArrayList<>();

        //User needs at least two choices
        mChoicesList.add(new Choice("", 0));
        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this, mAddChoiceButton);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }

    @SuppressWarnings("deprecation")
    private void setUpToolbar() {

        setSupportActionBar(mNewPostToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
            upArrow.setColorFilter(getResources().getColor(R.color.colorWhite), PorterDuff.Mode.SRC_ATOP);

            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(upArrow);
        }
    }

    private boolean isQuestionFilled() {
        return !mQuestionEditText.getText().toString().equals("") ||
            !mQuestionEditText.getText().toString().equals(" ");
    }

    private boolean isAllChoicesFilled() {
        for (Choice choice : mChoicesList) {
            Log.i("NewPost", "current choice = " + choice.getChoice());
            if (choice.getChoice().equals("") ||
                choice.getChoice().equals(" ")) {
                return false;
            }
        }
        return true;
    }

    private void alertUserItemMissing() {
        AlertDialog.Builder fillEveryThingDialog = new AlertDialog.Builder(StuckNewPostActivity.this);
        fillEveryThingDialog.setTitle(getString(R.string.one_or_more_cards_are_empty));
        fillEveryThingDialog.setMessage(getString(R.string.please_fill_all_boxes));
        fillEveryThingDialog.show();
        fillEveryThingDialog.setCancelable(true);
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
     * Creates a stuck post based on users input, gets the general location of the user, and the
     * time
     * the post was created
     */
    private void createPost() {

        mNewPostDone.setEnabled(false);
        ProgressDialog dialog = new ProgressDialog(this);
        dialog.setMessage("Creating post");
        dialog.show();

        /**
         * Set raw version of date to the ServerValue.TIMESTAMP value and save into
         * timestampCreatedMap
         */
        HashMap<String, Object> timestampCreated = new HashMap<>();
        timestampCreated.put(StuckConstants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);


        StuckPostSimple stuckPost = new StuckPostSimple(
            StuckSignUpActivity.encodeEmail(mEmail),
            mQuestionEditText.getText().toString(),
            GeneralArea.getAddressOfCurrentLocation(getLastKnownLocation(), this),
            mChoicesList.get(0).getChoice(),
            mChoicesList.get(1).getChoice(),
            (mChoicesList.size() == 3 || mChoicesList.size() == 4 ?
                mChoicesList.get(2).getChoice() : ""),
            (mChoicesList.size() == 4 ? mChoicesList.get(3).getChoice() : ""),
            StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
            StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
            timestampCreated,
            (-1 * new Date().getTime()));

        new SaveToDBTask(stuckPost, this, dialog).execute();


        Intent intent = new Intent(StuckNewPostActivity.this, StuckMainListActivity.class);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent,
                ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
        } else {
            startActivity(intent);
        }
    }

    private void setupWindowAnimations() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // inside your activity (if you did not enable transitions in your theme)
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);

            Explode explode = new Explode();
            explode.setDuration(400);

            getWindow().setEnterTransition(explode);

            // set an exit transition
            getWindow().setExitTransition(new Explode());
        }
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

    @OnClick(R.id.add_choice_button)
    public void setAddChoiceButton() {

        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this, mAddChoiceButton);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        if (mChoicesList.size() == 4) {
            mAddChoiceButton.setVisibility(View.INVISIBLE);
            mAddChoiceButton.setEnabled(false);
        }
    }

    @OnClick(R.id.new_post_done)
    public void setNewPostDone() {

        if (isQuestionFilled() && isAllChoicesFilled()) {

            createPost();
        } else {
            alertUserItemMissing();
        }
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
}
