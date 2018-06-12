package com.example.takas.study;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;

public class SetResultsDialogFragment extends DialogFragment {

    public static final String FIELD_TITLE = "title";

    public static SetResultsDialogFragment newInstance(Fragment target, int requestCode) {
        SetResultsDialogFragment fragment = new SetResultsDialogFragment();
        fragment.setTargetFragment(target, requestCode);

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // dialog title
        if (args.containsKey(FIELD_TITLE)) {
            builder.setTitle(args.getInt(FIELD_TITLE));
        }

        // dialog customize content view
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_prof, null);
        builder.setView(content);


        ///////////////////////////////////////////////////////////////////////////////////////////
        // make dialog
        return builder.create();
    }


}
