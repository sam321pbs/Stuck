package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.model.StuckPost;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class CardViewListAdapter extends RecyclerView.Adapter<CardViewListAdapter.ViewHolder> {

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
    public CardViewListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.stuck_single_item_question, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v, mActivity, mVoteClass);
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

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public static String TAG = "ViewHolder";

        // each data item is just a string in this case
        public TextView mStuckPostQuestion;
        public TextView mStuckPostLocation;
        public TextView mStuckPostSneakPeakChoice;

        public String mQuestion;
        public String mLocation;
        public String mChoice1;
        public String mChoice2;
        public String mChoice3;
        public String mChoice4;

        public ViewHolder(View v, final Activity activity, final Class classs) {
            super(v);
            mStuckPostQuestion = (TextView)v.findViewById(R.id.single_item_question);
            mStuckPostLocation = (TextView)v.findViewById(R.id.post_location);
            mStuckPostSneakPeakChoice = (TextView)v.findViewById(R.id.sneak_peak_choice_1);

            if (activity != null){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent StuckVoteIntent = new Intent(activity, classs);

                        StuckVoteIntent.putExtra(StuckConstants.LOCATION_VIEW_HOLDER, mLocation);
                        StuckVoteIntent.putExtra(StuckConstants.QUESTION_VIEW_HOLDER, mQuestion);
                        StuckVoteIntent.putExtra(StuckConstants.CHOICE_1_VIEW_HOLDER, mChoice1);
                        StuckVoteIntent.putExtra(StuckConstants.CHOICE_2_VIEW_HOLDER, mChoice2);
                        StuckVoteIntent.putExtra(StuckConstants.CHOICE_3_VIEW_HOLDER, mChoice3);
                        StuckVoteIntent.putExtra(StuckConstants.CHOICE_4_VIEW_HOLDER, mChoice4);

                        activity.startActivity(StuckVoteIntent);


                    }
                });
            }
        }
    }
}
