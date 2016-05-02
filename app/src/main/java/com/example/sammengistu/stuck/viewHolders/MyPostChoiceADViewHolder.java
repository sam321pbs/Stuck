package com.example.sammengistu.stuck.viewHolders;

import com.example.sammengistu.stuck.R;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

public class MyPostChoiceADViewHolder extends RecyclerView.ViewHolder {

    // each data item is just a string in this case
    public  EditText mChoiceEditText;
    public CardView mChoiceCardView;

    public MyPostChoiceADViewHolder(View v) {
        super(v);
        mChoiceEditText = (EditText) v.findViewById(R.id.my_choice_edit_text);
        mChoiceCardView = (CardView) v.findViewById(R.id.new_choice_card_view);
    }
}