package com.example.sammengistu.stuck.viewHolders;

import com.example.sammengistu.stuck.R;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class VoteChoicesADViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case
    public TextView mChoice;
    public TextView mNumberOfVotes;
    public CardView mCardViewChoice;

    public VoteChoicesADViewHolder(View v) {
        super(v);

        mChoice = (TextView) v.findViewById(R.id.single_item_choice);
        mNumberOfVotes = (TextView) v.findViewById(R.id.single_item_number_of_votes);
        mCardViewChoice = (CardView) v.findViewById(R.id.stuck_view_choice_card_view);

    }
}