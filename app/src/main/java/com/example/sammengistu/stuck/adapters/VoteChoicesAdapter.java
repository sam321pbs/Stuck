package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.VoteChoice;
import com.example.sammengistu.stuck.viewHolders.VoteChoicesADViewHolder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
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

    // Provide a suitable constructor (depends on the kind of dataset)
    public VoteChoicesAdapter(List<VoteChoice> myDataset, Context context) {
        mStuckPostChoices = myDataset;
        mContext = context;
        mShowAnimation = 0;
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

        final VoteChoicesADViewHolder currentViewHolder = holder;
        final VoteChoice currentChoice = mStuckPostChoices.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mChoice.setText(currentChoice.getChoice());
        holder.mNumberOfVotes.setText(currentChoice.getVotes() + "");

        if (currentChoice.isVotedFor()){
            currentViewHolder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorVoted));

        } else {
                currentViewHolder.mCardViewChoice.setBackgroundColor(
                    mContext.getResources().getColor(R.color.colorWhite));
        }

        holder.mCardViewChoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Change previous selected vote back down to one
                for (VoteChoice vc: mStuckPostChoices){
                    if (vc.isVotedFor()){
                        vc.setVotes(vc.getVotes() - 1);
                    }
                }

                if (currentChoice.isVotedFor()){
                    currentChoice.setVotedFor(false);
                } else {
                    currentChoice.setVotedFor(true);
                    currentChoice.setVotes(currentChoice.getVotes() + 1);
                }

                //Changes all other votes to false
                for (VoteChoice vc: mStuckPostChoices){
                    if (currentChoice != vc){
                        vc.setVotedFor(false);
                    }
                }

                notifyDataSetChanged();
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
}
