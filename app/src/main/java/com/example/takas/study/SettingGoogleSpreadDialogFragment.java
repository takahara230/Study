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
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class SettingGoogleSpreadDialogFragment extends DialogFragment {
    static String KEY_CURRENT_GOOGLE="current_google";


    public static SettingGoogleSpreadDialogFragment newInstance(boolean currentGoogle) {
        SettingGoogleSpreadDialogFragment fragment = new SettingGoogleSpreadDialogFragment();

        Bundle args = new Bundle();
        args.putBoolean(KEY_CURRENT_GOOGLE,currentGoogle);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity)getActivity();
        if(activity==null) return null;

        final Bundle args = getArguments();
        boolean currentGoogle = args.getBoolean(KEY_CURRENT_GOOGLE);


        final Dialog dialog = new Dialog(activity, android.R.style.Theme_Material_Light_Dialog);
        dialog.setContentView(R.layout.dialog_setting_google_spread);
        dialog.setTitle(R.string.action_google);

        final CheckBox checkBox = dialog.findViewById(R.id.download_data_reuse);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                EditText editUrl = dialog.findViewById(R.id.editGoogleUrl);
                EditText editSheet = dialog.findViewById(R.id.editSheetName);

                if(isChecked){
                    editUrl.setEnabled(false);
                    editSheet.setEnabled(false);
                }else{
                    editUrl.setEnabled(true);
                    editSheet.setEnabled(true);
                }
            }
        });

        EditText editUrl = dialog.findViewById(R.id.editGoogleUrl);
        EditText editSheet = dialog.findViewById(R.id.editSheetName);

        String spreadsheetId = activity.getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SPREADSHEETID,null);
        if(spreadsheetId!=null)
            editUrl.setText(spreadsheetId);
        String sheetName = activity.getPreferences(Context.MODE_PRIVATE).getString(CmDef.PREF_SHEETNAME,null);

        if(sheetName!=null)
            editSheet.setText(sheetName);

        ArrayList<HashMap<String,String>> data = activity.getSaveGoogleData();
        if(data!=null){
            checkBox.setChecked(true);
            editSheet.setEnabled(false);
            editUrl.setEnabled(false);
        }else{
            checkBox.setEnabled(false);
        }
        if(!currentGoogle){
            RadioButton allClear = dialog.findViewById(R.id.rb_all_clear);
            RadioButton clear = dialog.findViewById(R.id.rb_clear);
            allClear.setEnabled(false);
            clear.setEnabled(false);
        }else{
            RadioGroup group = dialog.findViewById(R.id.radioGroupCLear);
            group.check(R.id.rb_clear);
        }

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
                boolean reload = !checkBox.isChecked();
                EditText url = dialog.findViewById(R.id.editGoogleUrl);
                EditText sheet = dialog.findViewById(R.id.editSheetName);
                RadioGroup group = dialog.findViewById(R.id.radioGroupCLear);
                boolean allclear = false;
                if(group.getCheckedRadioButtonId()==R.id.rb_all_clear)
                    allclear = true;
                String spreadSheetId = url.getText().toString();
                String sheetName = sheet.getText().toString();
                if(reload && (spreadSheetId.length()==0 || sheetName.length()==0)){
                    new AlertDialog.Builder(getActivity())
                            .setTitle(R.string.action_google)
                            .setMessage(R.string.error_need_spread_url)
                            .setPositiveButton("OK", null)
                            .show();
                    return;

                }
                dialog.dismiss();
                activity.readFromGoogle(reload,spreadSheetId,sheetName,allclear);
            }
        });
        return dialog;
    }
}
