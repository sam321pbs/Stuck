package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.NetworkStatus;
import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.VoteChoice;
import com.example.sammengistu.stuck.viewHolders.VoteChoicesADViewHolder;
import com.firebase.client.Firebase;

import android.content.Context;
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

    private List<VoteChoice> mStuckPostChoices;
    private Context mContext;
    private int mShowAnimation;
    private Firebase mFireBaseRef;


    // Provide a suitable constructor (depends on the kind of dataset)
    public VoteChoicesAdapter(List<VoteChoice> myDataset, Context context, String fireBaseRef) {
        mStuckPostChoices = myDataset;
        mContext = context;
        mShowAnimation = 0;
        mFireBaseRef = new Firebase(fireBaseRef);
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
    public void onBindViewHolder(VoteChoicesADViewHolder holder, final int position) {

        final int pos = position;
        final VoteChoicesADViewHolder currentViewHolder = holder;
        final VoteChoice currentChoice = mStuckPostChoices.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mChoice.setText(currentChoice.getChoice());
        holder.mNumberOfVotes.setText(currentChoice.getVotes() + "");

        changeViewColor(currentChoice, currentViewHolder);

        holder.mCardViewChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: Don't change value if user already voted for it

                if (NetworkStatus.isOnline(mContext)) {
                    //Change previous selected vote back down to one
                    for (VoteChoice vc : mStuckPostChoices) {
                        if (vc.isVotedFor()) {
                            vc.setVotes(vc.getVotes() - 1);
//                        changeViewColor(vc, currentViewHolder);
                        }
                    }

                    if (currentChoice.isVotedFor()) {
                        currentChoice.setVotedFor(false);
                    } else {
                        currentChoice.setVotedFor(true);
                        currentChoice.setVotes(currentChoice.getVotes() + 1);

                        if (pos == 0) {
                            mFireBaseRef.child(StuckConstants.CHILD_CHOICE_ONE_VOTES)
                                .setValue(currentChoice.getVotes());
                        } else if (pos == 1) {
                            mFireBaseRef.child(StuckConstants.CHILD_CHOICE_TWO_VOTES)
                                .setValue(currentChoice.getVotes());
                        } else if (pos == 2) {
                            mFireBaseRef.child(StuckConstants.CHILD_CHOICE_THREE_VOTES)
                                .setValue(currentChoice.getVotes());
                        } else {
                            mFireBaseRef.child(StuckConstants.CHILD_CHOICE_FOUR_VOTES)
                                .setValue(currentChoice.getVotes());
                        }
                    }

                    //Changes all other votes to false
                    for (VoteChoice vc : mStuckPostChoices) {
                        if (currentChoice != vc) {
                            vc.setVotedFor(false);
                        }
                    }

                    Log.i("VoteChoiceAda", "Notifying");
                    notifyDataSetChanged();

                } else {
                    NetworkStatus.showOffLineDialog(mContext);
                }
            }
        });

        //Shows animation when created and not notified
        if (mShowAnimation < mStuckPostChoices.size()){
            mShowAnimation++;
            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(400 * position);

            holder.mCardViewChoice.startAnimation(fadeIn);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mStuckPostChoices.size();
    }

    @SuppressWarnings("deprecation")
    private void changeViewColor(VoteChoice currentChoice, VoteChoicesADViewHolder currentViewHolder) {
        if (currentChoice.isVotedFor()){
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorVoted));

        } else {
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorWhite));
        }
    }

}
