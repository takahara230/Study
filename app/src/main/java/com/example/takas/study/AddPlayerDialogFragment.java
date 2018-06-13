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
import android.widget.EditText;

public class AddPlayerDialogFragment extends DialogFragment{

    public static final String FIELD_TITLE = "title";
    public static final String FIELD_LAYOUT = "layout";
    public static final String FIELD_LABEL_POSITIVE = "label_positive";
    public static final String FIELD_LABEL_NEGATIVE = "label_negative";

    private EditText mEditName;

    public static AddPlayerDialogFragment newInstance(Fragment target, int requestCode) {
        AddPlayerDialogFragment fragment = new AddPlayerDialogFragment();
        fragment.setTargetFragment(target, requestCode);

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    void submit(String inputText) {
        Fragment target = getTargetFragment();
        if (target == null) { return; }

        Intent data = new Intent();
        data.putExtra(Intent.EXTRA_TEXT, inputText);
        target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle args = getArguments();

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // dialog title
        if (args!=null && args.containsKey(FIELD_TITLE)) {
            builder.setTitle(args.getInt(FIELD_TITLE));
        }

        // dialog customize content view
        LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = inflater.inflate(R.layout.dialog_prof, null);
        builder.setView(content);
        mEditName = content.findViewById(R.id.editText_name);


        // negative button title and click listener
        if (args!=null && args.containsKey(FIELD_LABEL_NEGATIVE)) {
            builder.setNegativeButton(args.getInt(FIELD_LABEL_NEGATIVE), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        // positive button title and click listener
        if (args!=null && args.containsKey(FIELD_LABEL_POSITIVE)) {
            builder.setPositiveButton(args.getInt(FIELD_LABEL_POSITIVE), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    Fragment target = getTargetFragment();
                    if (target == null) { return; }

                    if(mEditName!=null) {
                        Intent data = new Intent();
                        String name = mEditName.getText().toString();
                        data.putExtra(Intent.EXTRA_TEXT, name);
                        target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                    }
                }
            });
        }

        ///////////////////////////////////////////////////////////////////////////////////////////
        // make dialog
        return builder.create();
    }
}
