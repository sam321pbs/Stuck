package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.VoteChoice;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by SamMengistu on 4/26/16.
 */
public class VoteChoicesAdapter extends RecyclerView.Adapter<VoteChoicesAdapter.ViewHolder> {

    private List<VoteChoice> mStuckPostChoices;
    private Context mContext;

    // Provide a suitable constructor (depends on the kind of dataset)
    public VoteChoicesAdapter(List<VoteChoice> myDataset, Context context) {
        mStuckPostChoices = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public VoteChoicesAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                            int viewType) {

        // create a new view
        View v = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.stuck_view_choice, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @SuppressWarnings("deprecation")
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        VoteChoice currentChoice = mStuckPostChoices.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mChoice.setText(currentChoice.getChoice());
        holder.mNumberOfVotes.setText(currentChoice.getVotes());

        if (currentChoice.isVotedFor()){
            holder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorVoted));
        } else {
            holder.mCardViewChoice.setBackgroundColor(
                mContext.getResources().getColor(R.color.colorWhite));
        }

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {

        return mStuckPostChoices.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case

        public TextView mChoice;
        public TextView mNumberOfVotes;
        public View mCardViewChoice;

        public ViewHolder(View v) {
            super(v);
            mChoice = (TextView) v.findViewById(R.id.single_item_choice);
            mNumberOfVotes = (TextView) v.findViewById(R.id.number_of_votes_for_choice);
            mCardViewChoice = v;
        }
    }
}
