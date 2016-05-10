package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.Choice;
import com.example.sammengistu.stuck.viewHolders.MyPostChoiceADViewHolder;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;


public class MyPostChoiceAdapter extends RecyclerView.Adapter<MyPostChoiceADViewHolder> {
    private List<Choice> mChoiceDataSet;
    private Context mAppContext;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder


    // Provide a suitable constructor (depends on the kind of dataset)
    public MyPostChoiceAdapter(List<Choice> myDataset, Context appContext) {
        mChoiceDataSet = myDataset;
        mAppContext = appContext;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyPostChoiceADViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
        // create a new view
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View v = inflater.inflate(R.layout.card_view_my_choice, parent, false);

        return new MyPostChoiceADViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final MyPostChoiceADViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final int pos = holder.getAdapterPosition();
        Log.i("PostAdapter", "Current pos = " + pos + " Choice list value = " + mChoiceDataSet.get(pos).getChoice());
        View.OnLongClickListener deleteChoiceListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mChoiceDataSet.size() > 2) {

                    AlertDialog.Builder deleteChoiceDialog =
                        new AlertDialog.Builder(mAppContext);

                    deleteChoiceDialog.setTitle(mAppContext.getString(R.string.warning_delete_choice));
                    deleteChoiceDialog.setMessage(mChoiceDataSet.get(pos).getChoice());
                    deleteChoiceDialog.setPositiveButton(mAppContext.getString(R.string.delete),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mChoiceDataSet.remove(pos);
                                notifyDataSetChanged();
                            }
                        });
                    deleteChoiceDialog.setMessage(holder.mChoiceEditText.getText().toString());
                    deleteChoiceDialog.setNegativeButton(mAppContext.getString(R.string.cancel), null);
                    deleteChoiceDialog.show();

                } else {
                    //Make toast cant
                    Toast.makeText(mAppContext, R.string.cant_delete_choice,
                        Toast.LENGTH_SHORT).show();
                }

                return false;
            }
        };


        holder.mChoiceEditText.setOnLongClickListener(deleteChoiceListener);
        holder.mChoiceEditText.setText(mChoiceDataSet.get(pos).getChoice());
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

        holder.mChoiceCardView.setOnLongClickListener(deleteChoiceListener);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mChoiceDataSet.size();
    }

}
