package com.example.takas.study;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import static android.app.Activity.RESULT_OK;

public class CommonDialogFragment extends DialogFragment {
    public static final String TAG = CommonDialogFragment.class.getSimpleName();

    private  static String KEY_TITLE = "title";
    private static  String KEY_NUMBER = "number";
    private static String KEY_REQUESTCODE = "requestcode";

    public static CommonDialogFragment newInstance(String title, int num,int requestCode) {
        CommonDialogFragment frag = new CommonDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putString(KEY_TITLE, title);
        bundle.putInt(KEY_NUMBER,num);
        bundle.putInt(KEY_REQUESTCODE,requestCode);
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // ダイアログのコンテンツ部分
        LayoutInflater i
                = (LayoutInflater) getActivity()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View content = i.inflate(R.layout.select_number_layout, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        String title = getArguments().getString(KEY_TITLE);
        int num = getArguments().getInt(KEY_NUMBER);
        final int requestcode = getArguments().getInt(KEY_REQUESTCODE);
        final EditText editText = content.findViewById(R.id.editSelectNumber);
        editText.setText(String.valueOf(num));
        // タイトル
        builder.setTitle(title);
        // コンテンツ
        builder.setView(content);
        // OK
        builder.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int whichButton) {
//                        listener.onPositiveClick();
                        MainActivity activity = (MainActivity)getActivity();
                        String numstring = editText.getText().toString();
                        double num = 0;
                        try {
                            num = Float.parseFloat(numstring);
                        } catch (NumberFormatException e) {
                            return;
                        }
                        num = Math.floor(num);
                        if(num<4){
                            new AlertDialog.Builder(getActivity())
                                    .setTitle(R.string.action_index)
                                    .setMessage(R.string.err_few_number)
                                    .setPositiveButton("OK", null)
                                    .show();

                            return;
                        }
                        Intent intent = new Intent();

                        intent.putExtra(KEY_NUMBER, (int)num);
                        activity.onActivityResult(requestcode,RESULT_OK,intent);
                        dismiss();
                    }
                }
        );
        builder.setNegativeButton(android.R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dismiss();
                            }
                        }
                );


        Dialog dialog = builder.create();

        return dialog;
    }

    public static int getNumber(Intent data)
    {
        return data.getIntExtra(KEY_NUMBER,4);
    }
}
