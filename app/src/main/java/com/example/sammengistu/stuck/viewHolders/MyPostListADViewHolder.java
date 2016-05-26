package com.example.sammengistu.stuck.viewHolders;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.StuckConstants;
import com.example.sammengistu.stuck.activities.StuckVoteActivity;
import com.example.sammengistu.stuck.dialogs.PopUpMapDialog;
import com.example.sammengistu.stuck.model.StuckPostSimple;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class MyPostListADViewHolder extends RecyclerView.ViewHolder {

    public String TAG = "MyPostViewHolder";

    public TextView mStuckPostQuestion;
    public TextView mStuckPostLocation;
    public TextView mStuckPostSneakPeakChoice;
    public TextView mStuckPostTotalVotes;

    public String mPostEmail;
    public String mQuestion;
    public String mLocation;
    public String mChoice1;
    public String mChoice2;
    public String mChoice3;
    public String mChoice4;
    public String mRef;

    public int mChoice1Votes;
    public int mChoice2Votes;
    public int mChoice3Votes;
    public int mChoice4Votes;

    public MyPostListADViewHolder(View v, final Activity activity) {
        super(v);
        mStuckPostQuestion = (TextView) v.findViewById(R.id.single_item_question_text_view);
        mStuckPostLocation = (TextView) v.findViewById(R.id.post_location);
        mStuckPostSneakPeakChoice = (TextView) v.findViewById(R.id.sneak_peak_choice_1);
        mStuckPostTotalVotes = (TextView) v.findViewById(R.id.stuck_question_total_votes);

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent stuckVoteIntent = new Intent(activity, StuckVoteActivity.class);

                stuckVoteIntent.putExtra(StuckConstants.PASSED_IN_EMAIL, mPostEmail);
                stuckVoteIntent.putExtra(StuckConstants.LOCATION_VIEW_HOLDER, mLocation);
                stuckVoteIntent.putExtra(StuckConstants.QUESTION_VIEW_HOLDER, mQuestion);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_1_VIEW_HOLDER, mChoice1);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_2_VIEW_HOLDER, mChoice2);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_3_VIEW_HOLDER, mChoice3);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_4_VIEW_HOLDER, mChoice4);
                stuckVoteIntent.putExtra(StuckConstants.FIREBASE_REF, mRef);

                stuckVoteIntent.putExtra(StuckConstants.CHOICE_1_VOTES_VIEW_HOLDER, mChoice1Votes);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_2_VOTES_VIEW_HOLDER, mChoice2Votes);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_3_VOTES_VIEW_HOLDER, mChoice3Votes);
                stuckVoteIntent.putExtra(StuckConstants.CHOICE_4_VOTES_VIEW_HOLDER, mChoice4Votes);

                //TODO: cHeck that its 21 and greater
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    // only for gingerbread and newer versions
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        activity.startActivity(stuckVoteIntent,
                            ActivityOptions.makeSceneTransitionAnimation(
                                activity).toBundle());
                    } else {
                        activity.startActivity(stuckVoteIntent);
                    }
                }
            }
        });

        mStuckPostLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo: open google maps
                FragmentManager fm = activity.getFragmentManager();
                PopUpMapDialog popUpMapDialog = PopUpMapDialog.newInstance(
                    mStuckPostLocation.getText().toString());
                popUpMapDialog.show(fm, "");
            }
        });
    }

    public static class PostWithFBRef {

        private String mFirebaseRef;
        private StuckPostSimple mStuckPostSimple;

        public PostWithFBRef(String firebaseRef, StuckPostSimple stuckPostSimple) {
            mFirebaseRef = firebaseRef;
            mStuckPostSimple = stuckPostSimple;
        }

        public String getFirebaseRef() {
            return mFirebaseRef;
        }

        public StuckPostSimple getStuckPostSimple() {
            return mStuckPostSimple;
        }
    }

}

