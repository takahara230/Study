package com.example.takas.study;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 */
public class IndexPairListFragment extends Fragment {
    public IndexPairListFragment() {
        // Required empty public constructor
    }

    public static IndexPairListFragment newInstance() {
        return new IndexPairListFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_number_of_men, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        listView = view.findViewById(R.id.listView1);

        Spinner spinner = view.findViewById(R.id.spinner);
        // スピナーのアイテムが選択された時に呼び出されるコールバックリスナーを登録します
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                // 選択されたアイテムを取得します
                int num = spinner.getSelectedItemPosition();
                m_count = num+4;
                Toast.makeText(view.getContext(), String.valueOf(num), Toast.LENGTH_SHORT).show();

                adapter.clear();

                addItem();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        setAdapters();

        addItem();
    }




    protected ListView listView;

    protected List<String> dataList;
    protected ArrayAdapter<String> adapter;
    public void setAdapters(){
        dataList = new ArrayList<>();
        FragmentActivity activity = getActivity();
        if(activity!=null) {
            adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    dataList);
            listView.setAdapter(adapter);
        }
    }

    protected int m_count = 4;
    protected Map<String,Map<String,Object>> m_his;
    protected Map<String,Integer> m_kumiawase;


    @SuppressLint("DefaultLocale")
    public void addItem(){
        // adapter.add("Hello!");
        m_his = new HashMap<>();
        m_kumiawase = new HashMap<>();
        for(Integer i=0;i<m_count;i++){
            m_his.put(i.toString(),new HashMap<String,Object>());
        }
        int num = 0;
        for(int i=0;i<20;i++){
            List<Integer> k = new ArrayList<>();
            num = getMemberId(num,k);
            num = getMemberId(num,k);
            num = getMemberId(num,k);
            num = getMemberId(num,k);
            kumiawase(k);
            adapter.add(String.format("[%d] %d-%d vs %d-%d",i+1,k.get(0)+1,k.get(1)+1,k.get(2)+1,k.get(3)+1));


        }
    }
    Integer getMemberId(int last_num,List<Integer> k){
        for(;;) {
            for (Integer i = 0; i < m_count; i++) {
                Map<String,Object> l = m_his.get(i.toString());
                Integer ic = 0;
                Object o = l.get("key_count");
                if(o!=null && (o instanceof Integer)){
                    ic = (Integer) o;
                }
                if (ic <= last_num) {
                    l.put("key_count",ic+1);
                    k.add(i);
                    return last_num;
                }
            }
            last_num++;
        }
    }

    void kumiawase(List<Integer> member)
    {
        if(m_kumiawase==null){
            m_kumiawase = new HashMap<>();
        }
        Collections.sort(member);

        Integer m01 = kumiawase_count(member,0,1);
        Integer m23 = kumiawase_count(member,2,3);
        Integer m0 = m01+m23;

        Integer m02 = kumiawase_count(member,0,2);
        Integer m13 = kumiawase_count(member,1,3);
        Integer m1 = m02+m13;

        Integer m03 = kumiawase_count(member,0,3);
        Integer m12 = kumiawase_count(member,1,2);
        Integer m2 = m03+m12;

        if(m0<=m1 && m0 <= m2){
            kumiawase_inc(member,0,1,m01+1);
            kumiawase_inc(member,2,3,m23+1);
        }else if(m1 <= m2){
            kumiawase_inc(member,0,2,m02+1);
            kumiawase_inc(member,1,3,m13+1);
            Integer v = member.get(1);
            member.set(1,member.get(2));
            member.set(2,v);
        }else{
            kumiawase_inc(member,0,3,m03+1);
            kumiawase_inc(member,1,2,m12+1);
            Integer v = member.get(3);
            member.set(3,member.get(2));
            member.set(2,member.get(1));
            member.set(1,v);
        }
    }

    Integer kumiawase_count(List<Integer> member, Integer l0,Integer l1)
    {
        Integer m = m_kumiawase.get(String.format(Locale.US, "%d-%d",member.get(l0),member.get(l1)));
        if(m==null){
            m=0;
        }
        return m;
    }

    void kumiawase_inc(List<Integer> member, Integer l0,Integer l1,Integer v)
    {
        m_kumiawase.put(String.format(Locale.US, "%d-%d",member.get(l0),member.get(l1)),v);
    }

}
