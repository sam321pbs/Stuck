package com.example.sammengistu.stuck.activities;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.MyPostChoiceAdapter;
import com.example.sammengistu.stuck.model.Choice;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

public class StuckNewPostActivity extends AppCompatActivity {

    private EditText mQuestionEditText;
    private RecyclerView mMyChoicesRecyclerView;
    private CardView mAddChoiceCardView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<Choice> mChoicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_stuck_post);

        mQuestionEditText = (EditText) findViewById(R.id.my_post_edit_text);
        mMyChoicesRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_single_choice_view);
        mAddChoiceCardView = (CardView) findViewById(R.id.add_choice_card_view);

        mMyChoicesRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mMyChoicesRecyclerView.setLayoutManager(mLayoutManager);

        mChoicesList = new ArrayList<>();

        mChoicesList.add(new Choice(""));
        mChoicesList.add(new Choice(""));
        mChoicesList.add(new Choice(""));
        mChoicesList.add(new Choice(""));

        // specify an adapter (see also next example)
        mAdapter = new MyPostChoiceAdapter(mChoicesList);
        mMyChoicesRecyclerView.setAdapter(mAdapter);
    }
}
