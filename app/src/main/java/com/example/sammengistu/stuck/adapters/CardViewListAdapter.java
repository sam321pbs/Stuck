package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.StuckPost;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CardViewListAdapter extends RecyclerView.Adapter<CardViewListAdapter.ViewHolder> {

    private List<StuckPost> mStuckPosts;

    // Provide a suitable constructor (depends on the kind of dataset)
    public CardViewListAdapter(List<StuckPost> myDataset) {
        mStuckPosts = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public CardViewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.stuck_single_item_question, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mStuckPostQuestion.setText(mStuckPosts.get(position).getQuestion());
        holder.mStuckPostLocation.setText(mStuckPosts.get(position).getStuckPostLocation());
        holder.mStuckPostSneakPeakChoice.setText(mStuckPosts.get(position).getChoice1().substring(0,2));

//        holder.mChoice1.setText(mStuckPosts.get(0).getChoice1());
//        holder.mChoice2.setText(mStuckPosts.get(0).getChoice2());
//        holder.mChoice3.setText(mStuckPosts.get(0).getChoice3());
//        holder.mChoice4.setText(mStuckPosts.get(0).getChoice4());

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        Log.i("Adapter", mStuckPosts.size() + "");
        return mStuckPosts.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView mStuckPostQuestion;
        public TextView mStuckPostLocation;
        public TextView mStuckPostSneakPeakChoice;

//        public TextView mChoice1;
//        public TextView mChoice2;
//        public TextView mChoice3;
//        public TextView mChoice4;

        public ViewHolder(View v) {
            super(v);
            mStuckPostQuestion = (TextView)v.findViewById(R.id.single_item_question);
            mStuckPostLocation = (TextView)v.findViewById(R.id.post_location);
            mStuckPostSneakPeakChoice = (TextView)v.findViewById(R.id.sneak_peak_choice_1);
//            mChoice1 = (TextView)v.findViewById(R.id.single_item_choice_1);
//            mChoice2 = (TextView)v.findViewById(R.id.single_item_choice_2);
//            mChoice3 = (TextView)v.findViewById(R.id.single_item_choice_3);
//            mChoice4 = (TextView)v.findViewById(R.id.single_item_choice_4);
        }
    }
}
