package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.MyPostChoiceAdapter;
import com.example.sammengistu.stuck.model.Choice;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StuckNewPostActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.my_post_edit_text)
    EditText mQuestionEditText;

    @BindView(R.id.recycler_view_single_choice_view)
    RecyclerView mMyChoicesRecyclerView;

    @BindView(R.id.add_choice_button)
    Button mAddChoiceButton;

    @BindView(R.id.new_post_done)
    TextView mNewPostDone;

    @BindView(R.id.new_stuck_post_toolbar)
    Toolbar mNewPostToolbar;

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stuck_post);
        ButterKnife.bind(this);

        setUpToolbar();

        initializeViews();

        mMyChoicesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(mLayoutManager);

        mChoicesList = new ArrayList<>();

        mChoicesList.add(new Choice(""));
        mChoicesList.add(new Choice(""));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList, this);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }

    private void initializeViews(){

        mAddChoiceButton.setOnClickListener(this);

    }

    private void setUpToolbar(){

        setSupportActionBar(mNewPostToolbar);

        mNewPostDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: Check all boxs are filled
                if (isQuestionFilled() && isAllChoicesFilled()) {
                    Intent intent = new Intent(StuckNewPostActivity.this, StuckMainListActivity.class);
                    startActivity(intent);

                } else {
                    AlertDialog.Builder fillEveryThingDialog = new AlertDialog.Builder(StuckNewPostActivity.this);
                    fillEveryThingDialog.setTitle("Please fill all boxes");
                    fillEveryThingDialog.show();
                    fillEveryThingDialog.setCancelable(true);
                }
            }
        });

        // Get a support ActionBar corresponding to this toolbar
        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private boolean isQuestionFilled(){
        return !mQuestionEditText.getText().toString().equals("");
    }

    private boolean isAllChoicesFilled(){
        for (Choice choice: mChoicesList){
            if (choice.getChoice().equals("")){
                return false;
            }
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        mChoicesList.add(new Choice(""));
        mAdapter.notifyDataSetChanged();

        if (mChoicesList.size() == 5){
            mAddChoiceButton.setVisibility(View.INVISIBLE);
            mAddChoiceButton.setEnabled(false);
        }
    }

}
