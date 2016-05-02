package com.example.sammengistu.stuck.dialogs;

import com.example.sammengistu.stuck.R;
import com.example.sammengistu.stuck.adapters.FilterListAdapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by SamMengistu on 5/2/16.
 */
public class FilterDialog extends DialogFragment {

    private EditText mEditText;
    private ListView mFilteredItemsLV;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View filterView = getActivity().getLayoutInflater()
            .inflate(R.layout.dialog_filter_lists, null);

        mEditText = (EditText) filterView.findViewById(R.id.filter_edit_text);
        mFilteredItemsLV = (ListView) filterView.findViewById(android.R.id.list);

        List<String > strings = new ArrayList<>();
        strings.add("New york");
        strings.add("Silver Spring");
        strings.add("Alexandria, Va");

        mFilteredItemsLV.setAdapter(new FilterListAdapter(strings, getActivity()));

        return new AlertDialog.Builder(getActivity())
            .setView(filterView)
            .show();
    }
}
