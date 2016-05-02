package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.StuckPost;
import com.example.sammengistu.stuck.viewHolders.CardViewListADViewHolder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class CardViewListAdapter extends RecyclerView.Adapter<CardViewListADViewHolder> {

    private List<StuckPost> mStuckPosts;
    private Activity mActivity;
    private Class mVoteClass;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardViewListAdapter(List<StuckPost> myDataset, Activity activity, Class voteClass) {
        mActivity = activity;
        mVoteClass = voteClass;
        mStuckPosts = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewListADViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.stuck_single_item_question, parent, false);
        // set the view's size, margins, paddings and layout parameters

        CardViewListADViewHolder vh = new CardViewListADViewHolder(v, mActivity, mVoteClass);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(CardViewListADViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mStuckPostQuestion.setText(mStuckPosts.get(position).getQuestion());
        holder.mStuckPostLocation.setText(mStuckPosts.get(position).getStuckPostLocation());
        holder.mStuckPostSneakPeakChoice.setText(mStuckPosts.get(position).getChoice1().substring(0, 2));

        //Set up stuck info for stuck vote activity
        holder.mQuestion = mStuckPosts.get(position).getQuestion();
        holder.mLocation = mStuckPosts.get(position).getStuckPostLocation();
        holder.mChoice1 = mStuckPosts.get(position).getChoice1();
        holder.mChoice2 = mStuckPosts.get(position).getChoice2();
        holder.mChoice3 = mStuckPosts.get(position).getChoice3();
        holder.mChoice4 = mStuckPosts.get(position).getChoice4();

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mStuckPosts.size();
    }

}
