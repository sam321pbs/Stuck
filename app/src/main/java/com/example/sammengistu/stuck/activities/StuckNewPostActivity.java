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

public class StuckNewPostActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mQuestionEditText;
    private RecyclerView mMyChoicesRecyclerView;
    private Button mAddChoiceButton;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;
    private Toolbar mNewPostToolbar;
    private TextView mNewPostDone;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stuck_post);

        setUpToolbar();

        initializeViews();

        mMyChoicesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(mLayoutManager);

        mChoicesList = new ArrayList<>();

        mChoicesList.add(new Choice(""));
        mChoicesList.add(new Choice(""));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }

    private void initializeViews(){
        mQuestionEditText = (EditText) findViewById(R.id.my_post_edit_text);
        mMyChoicesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_single_choice_view);
        mAddChoiceButton = (Button) findViewById(R.id.add_choice_button);
        mAddChoiceButton.setOnClickListener(this);

    }

    private void setUpToolbar(){

        // my_child_toolbar is defined in the layout file
        mNewPostToolbar =
            (Toolbar) findViewById(R.id.new_stuck_post_toolbar);
        setSupportActionBar(mNewPostToolbar);

        mNewPostDone = (TextView) findViewById(R.id.new_post_done);

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
//            mAddChoiceButton.setVisibility(View.INVISIBLE);
            mAddChoiceButton.setEnabled(false);
        }
    }
}