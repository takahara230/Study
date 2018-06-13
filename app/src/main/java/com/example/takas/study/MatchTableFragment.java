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
public class MatchTableFragment extends Fragment {

    public  static  String TAG = MatchTableFragment.class.getSimpleName();

    /**
     * デフォルトコンストラクター
     */
    public MatchTableFragment() {
        // Required empty public constructor
    }

    /**
     * 新規フラグメントインスタンス
     * @return フラグメント
     */
    public static MatchTableFragment newInstance() {
        return new MatchTableFragment();
    }

    /**
     *
     * @param inflater インフラッター
     * @param container コンテナ
     * @param savedInstanceState　復帰時のデータ
     * @return 作成ビュー
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_number_of_men, container, false);
    }

    /**
     * view作成後に呼び出される。
     * @param view 作られたビュー
     * @param savedInstanceState 復帰時の保持データか
     */
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

                makeFirstPar(null);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        // コート数変更イベント処理登録
        SetEventChangeCoatNum(view);
        // リストアダプター設定
        setAdapters();

        // 起動直後
        makeFirstPar(null);
    }

    /**
     * コート数変更時の処理記述
     * @param view 対象ビュー
     */
    void SetEventChangeCoatNum(@NonNull View view)
    {
        Spinner spinner = view.findViewById(R.id.select_court_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                int num = spinner.getSelectedItemPosition();
                m_coat = num+1;
                makeFirstPar(m_players);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
    }




    protected ListView listView;

    protected List<String> dataList;
    protected ArrayAdapter<String> adapter;

    /**
     * listアダプターの初期設定
     */
    public void setAdapters(){
        dataList = new ArrayList<>();
        FragmentActivity activity = getActivity();
        if(activity!=null) {
            adapter = new ArrayAdapter<>(
                    activity,
                    android.R.layout.simple_list_item_1,
                    dataList);
            listView.setAdapter(adapter);

            // アイテムがクリックされたときに呼び出されるコールバックを登録
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent,
                                        View view, int position, long id) {
                openResDlg(position);

                }
            });
        }
    }

    /**
     * アイテムタップ時の処理
     */
    private  void openResDlg(int position){
        Bundle args = new Bundle();
        Map<String,Object> match = m_MatchTable.get(position);
        String game_no = (String)match.get(GAME_NO);
        args.putString(GAME_NO,game_no);
        Integer i0 = (Integer)match.get(PLAYER_0);
        Integer i1 = (Integer)match.get(PLAYER_1);
        if(m_players!=null) {
            String p1 = m_players.get(i0);
            String p2 = m_players.get(i1);
            String l = String.format("%s-%s",p1,p2);
            args.putString(PAR_0,l);
        }else{
            String l = String.format("%d-%d",i0+1,i1+1);
            args.putString(PAR_0,l);
        }
        Integer i2 = (Integer)match.get(PLAYER_2);
        Integer i3 = (Integer)match.get(PLAYER_3);
        if(m_players!=null) {
            String p1 = m_players.get(i2);
            String p2 = m_players.get(i3);
            String l = String.format("%s-%s",p1,p2);
            args.putString(PAR_1,l);
        }else{
            String l = String.format("%d-%d",i2+1,i3+1);
            args.putString(PAR_1,l);
        }
        SetResultsDialogFragment dialogFragment = SetResultsDialogFragment.newInstance(this,1);
        dialogFragment.setArguments(args);
        FragmentActivity activity = (FragmentActivity) getContext();
        dialogFragment.show(activity.getSupportFragmentManager(), getString(R.string.add_member));

    }

    protected  int m_coat = 1;  // コート数
    protected int m_count = 4;  // メンバー数(無名用)
    List<String> m_players;     // メンバー名
    protected Map<String,Map<String,Object>> m_his;
    protected Map<String,Integer> m_kumiawase;
    protected List<Integer> m_Already;
    protected List<Map<String,Object>> m_MatchTable;

    public static  String GAME_NO = "game_no";
    public static String PLAYER_0 = "player_0";
    public static String PLAYER_1 = "player_1";
    public static String PLAYER_2 = "player_2";
    public static String PLAYER_3 = "player_3";
    public static String PAR_0 = "par_0";
    public static String PAR_1 = "par_1";

    /**
     *
     * @param players メンバーリスト null の場合は 通し番号
     */
    @SuppressLint("DefaultLocale")
    public void makeFirstPar(List<String> players){
        adapter.clear();
        m_his = new HashMap<>();
        m_kumiawase = new HashMap<>();
        m_MatchTable = new ArrayList<>();

        if(players!=null) {
            m_players = players;
            m_count = players.size();
        }
        // 対戦履歴保管用オブジェクト生成
        for(Integer i=0;i<m_count;i++){
            m_his.put(i.toString(),new HashMap<String,Object>());
        }
        //
        int num = 0;    // 試合回数、
        for(int i=0;i<m_count*2;i++){
            m_Already = new ArrayList<>();
            for( int c = 0 ; c < m_coat ; c++ ) {
                List<Integer> k = new ArrayList<>();
                num = getMemberId(num, k);
                num = getMemberId(num, k);
                num = getMemberId(num, k);
                num = getMemberId(num, k);
                kumiawase(k);
                String l;
                if(players!=null) {
                    String p1 = m_players.get(k.get(0));
                    String p2 = m_players.get(k.get(1));
                    String p3 = m_players.get(k.get(2));
                    String p4 = m_players.get(k.get(3));
                    l = String.format("%s-%s vs %s-%s",p1,p2,p3,p4);
                }else{
                    l = String.format("%d-%d vs %d-%d", k.get(0) + 1, k.get(1) + 1, k.get(2) + 1, k.get(3) + 1);
                }
                String game_no;
                if(m_coat>1)
                    game_no=String.format("[%d-%d]", i + 1, c + 1);
                else
                    game_no=String.format("[%d]", i + 1);
                l=String.format("%s %s",game_no,l);
                dataList.add(l);
                Map<String,Object> match = new HashMap<>();
                match.put(GAME_NO,game_no);
                match.put(PLAYER_0,k.get(0));
                match.put(PLAYER_1,k.get(1));
                match.put(PLAYER_2,k.get(2));
                match.put(PLAYER_3,k.get(3));
                m_MatchTable.add(match);
            }
        }

        // ListView の更新
        adapter.notifyDataSetChanged();
    }

    /**
     *
     * @param last_num
     * @param k
     * @return
     */
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

    /**
     *
     * @param member
     */
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

    /**
     *
     * @param member
     * @param l0
     * @param l1
     * @return
     */
    Integer kumiawase_count(List<Integer> member, Integer l0,Integer l1)
    {
        Integer m = m_kumiawase.get(String.format(Locale.US, "%d-%d",member.get(l0),member.get(l1)));
        if(m==null){
            m=0;
        }
        return m;
    }

    /**
     *
     * @param member
     * @param l0
     * @param l1
     * @param v
     */
    void kumiawase_inc(List<Integer> member, Integer l0,Integer l1,Integer v)
    {
        m_kumiawase.put(String.format(Locale.US, "%d-%d",member.get(l0),member.get(l1)),v);
    }

}
