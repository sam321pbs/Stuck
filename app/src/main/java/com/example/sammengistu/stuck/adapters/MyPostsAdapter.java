package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.example.sammengistu.stuck.viewHolders.MyPostListADViewHolder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyPostsAdapter extends RecyclerView.Adapter<MyPostListADViewHolder>{

    private List<StuckPostSimple> mStuckPostSimples;
    private Activity mActivity;


    public MyPostsAdapter (List<StuckPostSimple> stuckPostSimples, Activity activity){
        mStuckPostSimples = stuckPostSimples;
        mActivity = activity;
    }

    @Override
    public MyPostListADViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
            from(parent.getContext()).
            inflate(R.layout.stuck_single_item_question, parent, false);

        return new MyPostListADViewHolder(itemView, mActivity);
    }

    @Override
    public void onBindViewHolder(MyPostListADViewHolder holder, int position) {

        StuckPostSimple stuckPostSimple = mStuckPostSimples.get(position);
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.mStuckPostQuestion.setText(stuckPostSimple.getQuestion());
        holder.mStuckPostLocation.setText(stuckPostSimple.getLocation());
        holder.mPostEmail = stuckPostSimple.getEmail();

        String choiceOne = stuckPostSimple.getChoiceOne();
        //Shows preview of the first choice
        if (choiceOne.length() > 9) {
            String sneakPeak = choiceOne.substring(0, 9) + "...";
            holder.mStuckPostSneakPeakChoice.setText(sneakPeak);
        } else {
            holder.mStuckPostSneakPeakChoice.setText(choiceOne);
        }

        int totalVotes = stuckPostSimple.getChoiceOneVotes() + stuckPostSimple.getChoiceTwoVotes() +
            stuckPostSimple.getChoiceThreeVotes() + stuckPostSimple.getChoiceFourVotes();

        String totalVote = totalVotes + "";
        holder.mStuckPostTotalVotes.setText(totalVote);

//        holder.mRef = getRef(i);

        //Set up stuck info for stuck vote activity
        holder.mQuestion = stuckPostSimple.getQuestion();

        if (stuckPostSimple.getLocation().equals("")) {
            holder.mLocation = "N/a";
        } else {
            holder.mLocation = stuckPostSimple.getLocation();
        }

        holder.mChoice1 = stuckPostSimple.getChoiceOne();
        holder.mChoice2 = stuckPostSimple.getChoiceTwo();
        holder.mChoice3 = stuckPostSimple.getChoiceThree();
        holder.mChoice4 = stuckPostSimple.getChoiceFour();

        holder.mChoice1Votes = stuckPostSimple.getChoiceOneVotes();
        holder.mChoice2Votes = stuckPostSimple.getChoiceTwoVotes();
        holder.mChoice3Votes = stuckPostSimple.getChoiceThreeVotes();
        holder.mChoice4Votes = stuckPostSimple.getChoiceFourVotes();
    }

    @Override
    public int getItemCount() {
        return mStuckPostSimples.size();
    }
}
