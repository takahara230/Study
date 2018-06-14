package com.example.takas.study;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.HashMap;

public class SetResultsDialogFragment extends DialogFragment {

    public static SetResultsDialogFragment newInstance(Fragment target, int requestCode) {
        SetResultsDialogFragment fragment = new SetResultsDialogFragment();
        fragment.setTargetFragment(target, requestCode);

        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if(activity==null) return null;
        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Material_Light_Dialog);
        dialog.setContentView(R.layout.dialog_set_results);
        dialog.setTitle(R.string.fin_game);

        final Bundle args = getArguments();

        TextView label_game_no =  dialog.findViewById(R.id.game_no);
        if(label_game_no!=null){
            String label = args.getString(MatchTableFragment.GAME_NO);
            String l = String.format("ゲームNo.%s",label);
            label_game_no.setText(l);
        }
        TextView label_par0 = dialog.findViewById(R.id.label_par1);
        label_par0.setText(args.getString(MatchTableFragment.PAR_0));
        TextView label_par1 = dialog.findViewById(R.id.label_par2);
        label_par1.setText(args.getString(MatchTableFragment.PAR_1));


        Button btn_cancel = dialog.findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                dialog.dismiss();
            }
        });

        Button btn_ok = dialog.findViewById(R.id.ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
                Fragment target = getTargetFragment();
                if (target != null) {
                    Intent data = new Intent();
                    HashMap<String, Integer> hashMap = new HashMap<>();
                    Spinner s1 = dialog.findViewById(R.id.spinner_par1);
                    Spinner s2 = dialog.findViewById(R.id.spinner_par2);
                    Integer i1 = s1.getSelectedItemPosition();
                    Integer i2 = s2.getSelectedItemPosition();
                    hashMap.put(MatchTableFragment.PAR_0,i1);
                    hashMap.put(MatchTableFragment.PAR_1,i2);
                    int pos = args.getInt(MatchTableFragment.GAME_INDEX);
                    hashMap.put(MatchTableFragment.GAME_INDEX,pos);
                    data.putExtra("hashMap", hashMap);
                    target.onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, data);
                }
            }
        });
        Button btn_delete = dialog.findViewById(R.id.delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                dialog.dismiss();
            }
        });


        return dialog;
/*
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(R.string.fin_game);

LayoutInflater inflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View content = inflater.inflate(R.layout.dialog_set_results, null);
        builder.setView(content);

        TextView label_game_no =  content.findViewById(R.id.game_no);
        if(label_game_no!=null){
            String label = args.getString(MatchTableFragment.GAME_NO);
            String l = String.format("ゲームNo.%s",label);
            label_game_no.setText(l);
        }
        TextView label_par0 = content.findViewById(R.id.label_par1);
        label_par0.setText(args.getString(MatchTableFragment.PAR_0));
        TextView label_par1 = content.findViewById(R.id.label_par2);
        label_par1.setText(args.getString(MatchTableFragment.PAR_1));


        Button btn_cancel = content.findViewById(R.id.cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                content.dismiss();
            }
        });

        ///////////////////////////////////////////////////////////////////////////////////////////
        // make dialog
        return builder.create();
        */
    }


}
