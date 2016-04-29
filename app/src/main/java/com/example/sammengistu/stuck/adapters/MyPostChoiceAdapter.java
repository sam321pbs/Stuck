package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.Choice;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.List;


public class MyPostChoiceAdapter extends RecyclerView.Adapter<MyPostChoiceAdapter.ViewHolder> {
    private List<Choice> mChoiceDataSet;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyPostChoiceAdapter(List<Choice> myDataset) {
        mChoiceDataSet = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyPostChoiceAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                             int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.card_view_my_choice, parent, false);

        return new ViewHolder( v );
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder,  int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        final int pos = holder.getAdapterPosition();
        holder.mChoiceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mChoiceDataSet.get(pos).setChoice(s.toString());

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChoiceDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public EditText mChoiceEditText;
        public ViewHolder(View v) {
            super(v);
            mChoiceEditText = (EditText) v.findViewById(R.id.my_choice_edit_text);
        }
    }
}
