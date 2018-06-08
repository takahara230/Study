package com.example.takas.study;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class ExampleDialogFragment extends DialogFragment{
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity(), android.R.style.Theme_Material_Light_Dialog);
        dialog.setContentView(R.layout.view_dialog);
        dialog.setTitle(R.string.select_member);

        // DialogFragment のレイアウトを縦横共に全域まで広げます
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);


        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listView.getCount()<4){
                    AlertDialog dlg =  new AlertDialog.Builder(getActivity())
                            .setTitle("メンバー選択")
                            .setMessage("4人以上必要です")
                            .setPositiveButton("OK",null)
                            .show();
                    dlg.show();
                }else {
                    // クリック時の処理
                    dialog.dismiss();
                }
            }
        });

        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                dialog.dismiss();
            }
        });

        Button btn_add_member = dialog.findViewById(R.id.btn_add_member);
        btn_add_member.setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  openDialogOriginalView();
                                              }
                                          });


        listView = dialog.findViewById(R.id.listView0);
        setAdapters();

        return dialog;
    }


    private void openDialogOriginalView() {
        Bundle args = new Bundle();
        args.putInt(MemberNameDialogFragment.FIELD_TITLE, R.string.add_member);
        // 自分で定義したレイアウト
        args.putInt(MemberNameDialogFragment.FIELD_LAYOUT, R.layout.dialog_prof);
        args.putInt(MemberNameDialogFragment.FIELD_LABEL_POSITIVE, android.R.string.ok);
        args.putInt(MemberNameDialogFragment.FIELD_LABEL_NEGATIVE, android.R.string.cancel);
        //MemberNameDialogFragment dialogFragment = new MemberNameDialogFragment();
        MemberNameDialogFragment dialogFragment = MemberNameDialogFragment.newInstance(this,1);
        dialogFragment.setArguments(args);
        FragmentActivity activity = (FragmentActivity) getContext();
        dialogFragment.show(activity.getSupportFragmentManager(), getString(R.string.add_member));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode != Activity.RESULT_OK) { return; }

                String name = data.getStringExtra(Intent.EXTRA_TEXT);
                if(name!=null && name.length()>0) {
                    adapter.add(name);
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    protected ListView listView;

    protected List<String> dataList;
    protected ArrayAdapter<String> adapter;
    public void setAdapters(){
        dataList = new ArrayList<>();
        dataList.add("高原");
        dataList.add("長瀬");
        final FragmentActivity activity = getActivity();
        if(activity!=null) {
            adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_multiple_choice,
                    dataList);
            listView.setAdapter(adapter);
            int count = listView.getCount();
            for(int i=0;i<count;i++){
                listView.setItemChecked(i,true);
            }

            // アイテムがクリックされたときに呼び出されるコールバックを登録
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                    // クリックされた時の処理
                }
            });

            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    // 長押しされた項目でMenuInflater.inflate()に指定するメニューリソースを切り替える
                    PopupMenu popup = new PopupMenu(activity, view);
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            if(item.getItemId() == R.id.menu_delete){
                                deleteItem(position);

                            }
                            return true;
                        }
                    });
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.item_menu, popup.getMenu());
                    popup.show();
                    return true;
                }
            });
        }
    }

    private void deleteItem(int position) {

        // それぞれの要素を削除
        dataList.remove(position);


        // ListView の更新
        adapter.notifyDataSetChanged();
    }

}
