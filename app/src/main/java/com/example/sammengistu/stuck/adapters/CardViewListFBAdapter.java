package com.example.sammengistu.stuck.adapters;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.activities.StuckVoteActivity;
import com.example.sammengistu.stuck.dialogs.PopUpMapDialog;
import com.example.sammengistu.stuck.model.StuckPostSimple;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.ui.FirebaseRecyclerAdapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

/**
 * Card view adapter for mainListActivity
 */
public class CardViewListFBAdapter extends FirebaseRecyclerAdapter<StuckPostSimple, CardViewListFBAdapter.CardViewListADViewHolder> {

    private static Activity mShowPostActivity;

    public CardViewListFBAdapter(Class<StuckPostSimple> modelClass, int modelLayout,
                                 Class<CardViewListFBAdapter.CardViewListADViewHolder> viewHolderClass,
                                 Query ref) {
        super(modelClass, modelLayout, viewHolderClass, ref);
    }

    public CardViewListFBAdapter(Class<StuckPostSimple> modelClass, int modelLayout,
                                 Class<CardViewListFBAdapter.CardViewListADViewHolder> viewHolderClass,
                                 Firebase ref, Activity activity) {
        super(modelClass, modelLayout, viewHolderClass, ref);
        mShowPostActivity = activity;
    }

    @Override
    public Firebase getRef(int position) {
        return super.getRef(position);
    }

    @Override
    protected void populateViewHolder(CardViewListFBAdapter.CardViewListADViewHolder cardViewListADViewHolder,
                                      StuckPostSimple stuckPostSimple, int i) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        cardViewListADViewHolder.mStuckPostQuestion.setText(stuckPostSimple.getQuestion());
        cardViewListADViewHolder.mStuckPostLocation.setText(stuckPostSimple.getLocation());
        cardViewListADViewHolder.mPostEmail = stuckPostSimple.getEmail();

        String choiceOne = stuckPostSimple.getChoiceOne();
        //Shows preview of the first choice
        if (choiceOne.length() > 9) {
            cardViewListADViewHolder.mStuckPostSneakPeakChoice.setText(choiceOne.substring(0, 9));
        } else {
            cardViewListADViewHolder.mStuckPostSneakPeakChoice.setText(choiceOne);
        }

        int totalVotes = stuckPostSimple.getChoiceOneVotes() + stuckPostSimple.getChoiceTwoVotes() +
            stuckPostSimple.getChoiceThreeVotes() + stuckPostSimple.getChoiceFourVotes();

        cardViewListADViewHolder.mStuckPostTotalVotes.setText(totalVotes + "");

        cardViewListADViewHolder.mRef = getRef(i);

        //Set up stuck info for stuck vote activity
        cardViewListADViewHolder.mQuestion = stuckPostSimple.getQuestion();

        if (stuckPostSimple.getLocation().equals("")) {
            cardViewListADViewHolder.mLocation = "N/a";
        } else {
            cardViewListADViewHolder.mLocation = stuckPostSimple.getLocation();
        }

        cardViewListADViewHolder.mChoice1 = stuckPostSimple.getChoiceOne();
        cardViewListADViewHolder.mChoice2 = stuckPostSimple.getChoiceTwo();
        cardViewListADViewHolder.mChoice3 = stuckPostSimple.getChoiceThree();
        cardViewListADViewHolder.mChoice4 = stuckPostSimple.getChoiceFour();

        cardViewListADViewHolder.mChoice1Votes = stuckPostSimple.getChoiceOneVotes();
        cardViewListADViewHolder.mChoice2Votes = stuckPostSimple.getChoiceTwoVotes();
        cardViewListADViewHolder.mChoice3Votes = stuckPostSimple.getChoiceThreeVotes();
        cardViewListADViewHolder.mChoice4Votes = stuckPostSimple.getChoiceFourVotes();
    }

    /**
     * Viewholde for adapter items
     */
    public static class CardViewListADViewHolder extends RecyclerView.ViewHolder {

        public String TAG = "ViewHolder";

        public TextView mStuckPostQuestion;
        public TextView mStuckPostLocation;
        public TextView mStuckPostSneakPeakChoice;
        public TextView mStuckPostTotalVotes;
        public Firebase mRef;

        public String mPostEmail;
        public String mQuestion;
        public String mLocation;
        public String mChoice1;
        public String mChoice2;
        public String mChoice3;
        public String mChoice4;

        private int mChoice1Votes;
        private int mChoice2Votes;
        private int mChoice3Votes;
        private int mChoice4Votes;

        public CardViewListADViewHolder(View v) {
            super(v);
            mStuckPostQuestion = (TextView) v.findViewById(R.id.single_item_question);
            mStuckPostLocation = (TextView) v.findViewById(R.id.post_location);
            mStuckPostSneakPeakChoice = (TextView) v.findViewById(R.id.sneak_peak_choice_1);
            mStuckPostTotalVotes = (TextView) v.findViewById(R.id.stuck_question_total_votes);


            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

//                     get the common element for the transition in this activity
//                        final View androidRobotView = findViewById(R.id.image_small);

//                     define a click listener

                    Intent stuckVoteIntent = new Intent(CardViewListFBAdapter.mShowPostActivity, StuckVoteActivity.class);

                    stuckVoteIntent.putExtra(StuckConstants.PASSED_IN_EMAIL, mPostEmail);
                    stuckVoteIntent.putExtra(StuckConstants.LOCATION_VIEW_HOLDER, mLocation);
                    stuckVoteIntent.putExtra(StuckConstants.QUESTION_VIEW_HOLDER, mQuestion);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_1_VIEW_HOLDER, mChoice1);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_2_VIEW_HOLDER, mChoice2);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_3_VIEW_HOLDER, mChoice3);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_4_VIEW_HOLDER, mChoice4);
                    stuckVoteIntent.putExtra(StuckConstants.FIREBASE_REF, mRef.toString());

                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, mChoice1Votes);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_2_VOTES_VIEW_HOLDER, mChoice2Votes);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_3_VOTES_VIEW_HOLDER, mChoice3Votes);
                    stuckVoteIntent.putExtra(StuckConstants.CHOICE_4_VOTES_VIEW_HOLDER, mChoice4Votes);

                    Log.i(TAG, "firebase tos string " + mRef.toString());


                    CardViewListFBAdapter.mShowPostActivity.startActivity(stuckVoteIntent);

                }
            });

            mStuckPostLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Todo: open google maps
                    FragmentManager fm = CardViewListFBAdapter.mShowPostActivity.getFragmentManager();
                    PopUpMapDialog popUpMapDialog = PopUpMapDialog.newInstance(
                        mStuckPostLocation.getText().toString());
                    popUpMapDialog.show(fm, "");
                }
            });
        }

    }
}
