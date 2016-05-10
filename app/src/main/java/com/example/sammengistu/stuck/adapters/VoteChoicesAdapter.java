package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.activities.StuckSignUpActivity;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.model.VoteChoice;
import com.example.sammengistu.stuck.viewHolders.VoteChoicesADViewHolder;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;

import java.util.List;


public class VoteChoicesAdapter extends RecyclerView.Adapter<VoteChoicesADViewHolder> {

    private static final String TAG = "VoteChoicesAdapter55";
    private List<VoteChoice> mStuckPostChoices;
    private Context mContext;
    private int mShowAnimation;
    private Firebase mFireBaseRefForVotes;
    private StuckPostSimple mStuckPostSimple;
    private String mUserEmail;
    private Firebase mUsersVoteValue;


    // Provide a suitable constructor (depends on the kind of dataset)
    public VoteChoicesAdapter(List<VoteChoice> myDataset, Context context,
                              String fireBaseRef, StuckPostSimple stuckPostSimple) {
        mStuckPostChoices = myDataset;
        mContext = context;
        mShowAnimation = 0;
        mFireBaseRefForVotes = new Firebase(fireBaseRef);
        mStuckPostSimple = stuckPostSimple;

        SharedPreferences preferences = mContext.getApplicationContext()
            .getSharedPreferences(StuckConstants.SHARED_PREFRENCE_USER, 0);

         mUserEmail = StuckSignUpActivity.encodeEmail(
            preferences.getString(StuckConstants.KEY_ENCODED_EMAIL, ""));

        mUsersVoteValue= new Firebase(StuckConstants.FIREBASE_URL)
            .child(StuckConstants.FIREBASE_URL_USERS_VOTES).child(mUserEmail)
            .child(mStuckPostSimple.getQuestion());
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VoteChoicesADViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.stuck_view_choice, parent, false);
        // set the view's size, margins, padding and layout parameters

        return new VoteChoicesADViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(VoteChoicesADViewHolder holder, int position) {

        final int pos = position;
        final VoteChoicesADViewHolder currentViewHolder = holder;
        final VoteChoice currentChoice = mStuckPostChoices.get(position);

        mUsersVoteValue.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // do some stuff once

                if (snapshot != null) {
                    Integer integer = snapshot.getValue(Integer.class);
                    Log.i("VOTECHOICEADA", mUserEmail + " voted for = " + integer);

                    if (integer == null ){
                        mUsersVoteValue.setValue(4);
                    } else {
                        if (integer == pos) {
                            currentChoice.setVotedFor(true);
                            currentViewHolder.mCardViewChoice.setBackgroundColor(
                                mContext.getResources().getColor(R.color.colorVoted));
                        } else {
                            currentViewHolder.mCardViewChoice.setBackgroundColor(
                                mContext.getResources().getColor(R.color.colorWhite));
                        }
                    }
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mChoice.setText(currentChoice.getChoice());
        holder.mNumberOfVotes.setText(currentChoice.getVotes() + "");

//        changeViewColor(currentChoice, currentViewHolder);

        Log.i(TAG, mStuckPostSimple.getEmail().replaceAll("\\s" , "") +
            " = " + mUserEmail.replaceAll("\\s" , ""));

        if (!mStuckPostSimple.getEmail().replaceAll("\\s" , "").equals(
           mUserEmail.replaceAll("\\s" , ""))) {
            holder.mCardViewChoice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Todo: Don't change value if user already voted for it

                    if (NetworkStatus.isOnline(mContext)) {

                        //Change previous selected vote back down to one
                        adjustVotes();

                        updateVotes(currentChoice, pos, mUsersVoteValue, currentViewHolder);

                        Log.i(TAG, "Notifying");
                        notifyDataSetChanged();

                    } else {
                        NetworkStatus.showOffLineDialog(mContext);
                    }
                }
            });
        }

        //Shows animation when created and not notified
        if (mShowAnimation < mStuckPostChoices.size()) {
            mShowAnimation++;
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(400 * position);

            holder.mCardViewChoice.startAnimation(fadeIn);
        }
    }

    private void adjustVotes() {
        for (int pos = 0; pos < mStuckPostChoices.size(); pos ++) {
            VoteChoice currentChoice = mStuckPostChoices.get(pos);
//            Log.i(TAG, vc.getChoice() + " voted for = " + vc.isVotedFor());
            if (currentChoice.isVotedFor()) {
                currentChoice.setVotes(currentChoice.getVotes() - 1);

                if (pos == 0) {
                    mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_ONE_VOTES)
                        .setValue(currentChoice.getVotes());
                } else if (pos == 1) {
                    mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_TWO_VOTES)
                        .setValue(currentChoice.getVotes());
                } else if (pos == 2) {
                    mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_THREE_VOTES)
                        .setValue(currentChoice.getVotes());
                } else {
                    mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_FOUR_VOTES)
                        .setValue(currentChoice.getVotes());
                }

            }
        }
    }

    @SuppressWarnings("deprecation")
    private void updateVotes(VoteChoice currentChoice, int pos, Firebase changeVoteValue,
                             VoteChoicesADViewHolder currentViewHolder) {

        if (currentChoice.isVotedFor()) {
            currentChoice.setVotedFor(false);
            changeVoteValue.setValue(4);
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorWhite));

        } else {
            currentChoice.setVotedFor(true);
            currentChoice.setVotes(currentChoice.getVotes() + 1);

            if (pos == 0) {
                mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_ONE_VOTES)
                    .setValue(currentChoice.getVotes());
            } else if (pos == 1) {
                mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_TWO_VOTES)
                    .setValue(currentChoice.getVotes());
            } else if (pos == 2) {
                mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_THREE_VOTES)
                    .setValue(currentChoice.getVotes());
            } else {
                mFireBaseRefForVotes.child(StuckConstants.CHILD_CHOICE_FOUR_VOTES)
                    .setValue(currentChoice.getVotes());
            }

            changeVoteValue.setValue(pos);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mStuckPostChoices.size();
    }

    @SuppressWarnings("deprecation")
    private void changeViewColor(VoteChoice currentChoice, VoteChoicesADViewHolder currentViewHolder) {
        if (currentChoice.isVotedFor()) {
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorVoted));

        } else {
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorWhite));
        }
    }

}
