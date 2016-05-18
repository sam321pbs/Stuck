package com.example.sammengistu.stuck.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import com.example.sammengistu.stuck.GeneralArea;
import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.MyPostChoiceAdapter;
import com.example.sammengistu.stuck.model.Choice;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.stuck_offline_db.ContentProviderStuck;
import com.example.sammengistu.stuck.stuck_offline_db.StuckDBConverter;
import com.firebase.client.AuthData;
import com.firebase.client.Firebase;
import com.firebase.client.ServerValue;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
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
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;
    private Firebase mRefActivePosts;
    private String mEmail;
    private Firebase.AuthStateListener mAuthListener;
    private Firebase mFirebaseRef;
    private GoogleApiClient mGoogleApiClient;

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
        setContentView(R.layout.activity_new_stuck_post);
        ButterKnife.bind(this);

        setUpToolbar();

        SharedPreferences pref = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode
        mEmail = pref.getString(StuckConstants.KEY_ENCODED_EMAIL, "");

        mFirebaseRef = new Firebase(StuckConstants.FIREBASE_URL);
        mRefActivePosts = new Firebase(StuckConstants.FIREBASE_URL).child(StuckConstants.FIREBASE_URL_ACTIVE_POSTS);

        mAuthListener = new Firebase.AuthStateListener() {
            @Override
            public void onAuthStateChanged(AuthData authData) {
                /* The user has been logged out */
                if (authData == null) {
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

        mFirebaseRef.addAuthStateListener(mAuthListener);

        mMyChoicesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(mLayoutManager);

        mChoicesList = new ArrayList<>();

        mChoicesList.add(new Choice("", 0));
        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }

    private void setUpToolbar() {

        setSupportActionBar(mNewPostToolbar);

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        if (ab != null) {
            // Enable the Up button
            ab.setDisplayHomeAsUpEnabled(true);

            getSupportActionBar().setDisplayShowTitleEnabled(false);
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
     * Creates a stuck post based on users input, gets the general location of the user, and the time
     * the post was created
     */
    private void createPost() {

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
            (mChoicesList.size() == 3 ? mChoicesList.get(2).getChoice(): ""),
            (mChoicesList.size() == 4 ? mChoicesList.get(3).getChoice(): ""),
            StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
            StuckConstants.ZERO_VOTES, StuckConstants.ZERO_VOTES,
            timestampCreated,
            (-1 * new Date().getTime()));

        if (!NetworkStatus.isOnline(this)) {
            Uri contentUri = Uri.withAppendedPath(ContentProviderStuck.CONTENT_URI,
                StuckConstants.TABLE_OFFLINE_POST);

            getContentResolver().insert(contentUri,
                StuckDBConverter.insertStuckPostToDB(stuckPost, StuckConstants.FALSE));

            Toast.makeText(this,
                R.string.make_post_when_when_back_online, Toast.LENGTH_LONG).show();
        } else {
            mRefActivePosts.push().setValue(stuckPost);
        }

        Intent intent = new Intent(StuckNewPostActivity.this, StuckMainListActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_choice_button)
    public void setAddChoiceButton() {

        mChoicesList.add(new Choice("", 0));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this);
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
    public void onDestroy() {
        super.onDestroy();
        mFirebaseRef.removeAuthStateListener(mAuthListener);
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
