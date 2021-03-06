package com.example.sammengistu.stuck.activities;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.adapters.VoteChoicesAdapter;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.model.VoteChoice;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class StuckVoteActivity extends AppCompatActivity {

    private static String TAG = "StuckVoteActivity55";

    private RecyclerView.Adapter mAdapterChoices;
    private RecyclerView.LayoutManager mLayoutManagerChoices;
    private StuckPostSimple mStuckPostSimple;
    private DatabaseReference mRefPost;
    private List<VoteChoice> mStuckPostChoices;
    private SharedPreferences mSharedPreferences;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;

    @BindView(R.id.delete_post_image_view)
    ImageView mDeleteImageView;
    @BindView(R.id.vote_toolbar)
    Toolbar mVoteToolbar;
    @BindView(R.id.single_item_question_text_view)
    TextView mQuestion;
    @BindView(R.id.choice_one_title)
    TextView mChoiceOneTitle;
    @BindView(R.id.post_from_title)
    TextView mPostFromTitle;
    @BindView(R.id.total_votes_title)
    TextView mTotalVotesTitle;
    @BindView(R.id.stuck_question_total_votes)
    TextView mPostLocation;
    @BindView(R.id.sneak_peak_choice_1)
    TextView mStuckPostTotalVotes;
    @BindView(R.id.help_vote_message)
    TextView mHelpVoteMessage;

    private ValueEventListener mValueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            try{
                mStuckPostSimple = dataSnapshot.getValue(StuckPostSimple.class);
                mStuckPostChoices.clear();


                mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
                    false, mStuckPostSimple.getChoiceOneVotes()));

                mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
                    false, mStuckPostSimple.getChoiceTwoVotes()));

                if (!mStuckPostSimple.getChoiceThree().equals("")) {
                    mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
                        false, mStuckPostSimple.getChoiceThreeVotes()));
                }

                if (!mStuckPostSimple.getChoiceFour().equals("")) {
                    mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
                        false, mStuckPostSimple.getChoiceFourVotes()));
                }

                int totalVotes = mStuckPostSimple.getChoiceOneVotes() +
                    mStuckPostSimple.getChoiceTwoVotes() +
                    mStuckPostSimple.getChoiceThreeVotes() +
                    mStuckPostSimple.getChoiceFourVotes();

                String totalVotesForTextView= totalVotes + "";
                mStuckPostTotalVotes.setText(totalVotesForTextView);
                mAdapterChoices.notifyDataSetChanged();
            } catch (Exception e){
                Toast.makeText(StuckVoteActivity.this,
                    R.string.that_post_was_deleted, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(StuckVoteActivity.this, StuckMainListActivity.class);
                startActivity(intent);
            }
        }

        @Override
        public void onCancelled(DatabaseError firebaseError) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupWindowAnimations();
        setContentView(R.layout.activity_stuck_vote);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        // use a linear layout manager
        mLayoutManagerChoices = new LinearLayoutManager(this);

        mSharedPreferences = getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0); // 0 - for private mode

        mStuckPostSimple = new StuckPostSimple(
            getIntent().getStringExtra(StuckConstants.PASSED_IN_EMAIL),
            getIntent().getStringExtra(StuckConstants.QUESTION_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.LOCATION_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_1_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_2_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_3_VIEW_HOLDER),
            getIntent().getStringExtra(StuckConstants.CHOICE_4_VIEW_HOLDER),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            getIntent().getIntExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, 0),
            new HashMap<String, Object>(),
            (-1 * new Date().getTime())
        );

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                 /* The user has been logged out */
                if (firebaseAuth == null) {
                    Log.i(TAG, "USer has been logged out");
                    StuckMainListActivity.takeUserToLoginScreenOnUnAuth(StuckVoteActivity.this);
                } else {
                    //not logged out
                    Log.i(TAG, "USer not been logged out");
                }
            }
        };

        setUpToolbar();

        mRefPost = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getIntent().getStringExtra(StuckConstants.FIREBASE_REF));

        mQuestion.setText(mStuckPostSimple.getQuestion());
        mPostLocation.setText(mStuckPostSimple.getLocation());

        int totalVotes = mStuckPostSimple.getChoiceOneVotes() +
            mStuckPostSimple.getChoiceTwoVotes() +
            mStuckPostSimple.getChoiceThreeVotes() +
            mStuckPostSimple.getChoiceFourVotes();

        String totalVotesForTextView = totalVotes + "";
        mStuckPostTotalVotes.setText(totalVotesForTextView);
        mTotalVotesTitle.setText(R.string.post_from);
        mChoiceOneTitle.setText(R.string.total_votes);
        mPostFromTitle.setVisibility(View.INVISIBLE);
        setUpRecyclerViewChoices();
    }

    /**
     * Creates VoteChoices for all choices for the post and then creates the adapter
     */
    private void setUpRecyclerViewChoices() {
        RecyclerView recyclerViewChoices = (RecyclerView) findViewById(R.id.recycler_view_choices_vote);

        if (recyclerViewChoices != null) {
            recyclerViewChoices.setLayoutManager(mLayoutManagerChoices);
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            recyclerViewChoices.setHasFixedSize(true);
        }

        mStuckPostChoices = new ArrayList<>();

        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceOne(),
            false, mStuckPostSimple.getChoiceOneVotes()));
        mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceTwo(),
            false, mStuckPostSimple.getChoiceTwoVotes()));

        if (!mStuckPostSimple.getChoiceThree().equals("")) {
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceThree(),
                false, mStuckPostSimple.getChoiceThreeVotes()));
        }

        if (!mStuckPostSimple.getChoiceFour().equals("")){
            mStuckPostChoices.add(new VoteChoice(mStuckPostSimple.getChoiceFour(),
                false, mStuckPostSimple.getChoiceFourVotes()));
        }
        DatabaseReference firebasePostRef = FirebaseDatabase.getInstance()
            .getReferenceFromUrl(getIntent().getStringExtra(StuckConstants.FIREBASE_REF));

        mAdapterChoices = new VoteChoicesAdapter(mStuckPostChoices, this,
            getIntent().getStringExtra(StuckConstants.FIREBASE_REF), mStuckPostSimple,
            firebasePostRef.getKey());

        if (recyclerViewChoices != null) {
            recyclerViewChoices.setAdapter(mAdapterChoices);
        }

        mRefPost.addValueEventListener(mValueEventListener);
    }

    /**
     * Sets up the toolbar for the activity and if the current user is the creater of the post they
     * have the choice to delete it
     */
    @SuppressWarnings("deprecation")
    private void setUpToolbar() {

        setSupportActionBar(mVoteToolbar);

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

        String space = getString(R.string.space);

        if (mStuckPostSimple.getEmail().replaceAll(space , "").equals(
            mSharedPreferences.getString(
                StuckConstants.KEY_ENCODED_EMAIL, "").replaceAll(space , ""))){

            mDeleteImageView.setEnabled(true);
            mDeleteImageView.setVisibility(View.VISIBLE);
            mHelpVoteMessage.setText(R.string.tap_trash_can_to_delete_post);
        } else {

            mDeleteImageView.setEnabled(false);
            mDeleteImageView.setVisibility(View.INVISIBLE);
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

    @OnClick(R.id.delete_post_image_view)
    public void setDeleteImageView(View view) {
        new AlertDialog.Builder(StuckVoteActivity.this)
            .setTitle(getString(R.string.delete_post_dialog_message))
            .setNegativeButton(getString(R.string.cancel), null)
            .setPositiveButton(getString(R.string.delete), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mRefPost.removeEventListener(mValueEventListener);
                    mRefPost.removeValue(new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError,
                                               DatabaseReference databaseReference) {

                            Intent intent = new Intent(StuckVoteActivity.this, StuckMainListActivity.class);
                            startActivity(intent);
                                             }

//                        new Firebase.CompletionListener() {
//                        @Override
//                        public void onComplete(FirebaseError firebaseError, Firebase firebase) {
//                            Intent intent = new Intent(StuckVoteActivity.this, StuckMainListActivity.class);
//                            startActivity(intent);
//                        }
                });
                }
            }).show();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefPost.removeEventListener(mValueEventListener);
    }
}
