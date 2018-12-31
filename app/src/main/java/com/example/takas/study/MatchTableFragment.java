package com.example.takas.study;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;

import org.w3c.dom.DOMError;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 *
 */
public class MatchTableFragment extends Fragment {
    public static  String PREF_COAT = "Coat";

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_number_of_men, container, false);
    }

    boolean m_haveSpreadCash;
    Button m_change_button;
    android.support.v7.widget.AppCompatSpinner m_cort_num_spinner;

    /**
     * view作成後に呼び出される。
     * @param view 作られたビュー
     * @param savedInstanceState 復帰時の保持データか
     */
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        //spinner = (Spinner) findViewById(R.id.spinner);
        //spinner.setSelection(index);

        final MainActivity activity = (MainActivity)getActivity();
        ArrayList<HashMap<String,String>> data = activity.getSaveGoogleData();
        m_haveSpreadCash = data!=null;

        listView = view.findViewById(R.id.listView1);
        m_change_button = view.findViewById(R.id.button_change);
        //m_spinner.setSelection(m_spinnerPosition);
        m_change_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // クリック時の処理
                activity.changeButton();
            }
        });

        m_cort_num_spinner = view.findViewById(R.id.select_court_spinner);
        {
            SharedPreferences pref = activity.getPreferences(MODE_PRIVATE);
            m_coat = pref.getInt(PREF_COAT,1);
        }
        m_cort_num_spinner.setSelection(m_coat-1,false);
        m_cort_num_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            // アイテムが選択された時の動作
            public void onItemSelected(AdapterView parent,View view, int position,long id) {
                m_coat=position+1;

                SharedPreferences pref = activity.getPreferences(MODE_PRIVATE);
                SharedPreferences.Editor e = pref.edit();
                e.putInt(PREF_COAT,m_coat);
                e.commit();

                if(m_players2==null)
                    makePar(true,m_number);
                else {
                    makePar2(true);
                }

            }

            // 何も選択されなかった時の動作
            public void onNothingSelected(AdapterView parent) {
            }
        });


        // コート数変更イベント処理登録
        SetEventChangeCoatNum(view);
        // リストアダプター設定
        setAdapters();
        // 起動直後
        //makePar(true,4);

        try{


        String mode = (String)activity.PrefGet(MainActivity.PREF_MAKE_MODE,new TypeToken<String>(){}.getType());
        if(mode!=null){
            if(mode.equals(MainActivity.MODE_GOOGLE)){
                ArrayList<HashMap<String,Object>> players = (ArrayList<HashMap<String, Object>>)activity.PrefGet(MainActivity.PREF_PLAYER_INFO, new TypeToken<ArrayList<HashMap<String, Object>>>() {
                }.getType());
                m_players2 = new ArrayList<>();
                for (HashMap<String,Object> map:players
                     ) {
                    HashMap<String,Object> n = new HashMap<>();
                    String name = (String)map.get(KEY_NAME);
                    n.put(KEY_NAME,name);
                    Double v = (Double)map.get(KEY_PLAY_NUM);
                    n.put(KEY_PLAY_NUM, v.intValue());
                    Object s = map.get(KEY_SELECTION);
                    n.put(KEY_SELECTION,s);

                    v = (Double)map.get(KEY_LEVEL);
                    n.put(KEY_LEVEL,v.intValue());

                    m_players2.add(n);
                }

                m_change_button.setText(R.string.action_google);
                m_MatchTable = (List<Map<String,Object>>)activity.PrefGet(MainActivity.PREF_MATCH_TABLE,new TypeToken<List<Map<String,Object>>>(){}.getType());
                m_kumiawase = new HashMap<>();
                updateList();

                activity.setMode(mode);
            }
        }
        }catch (Exception e){
            makePar(true,4);
        }
    }


    /**
     * コート数変更時の処理記述
     * @param view 対象ビュー
     */
    void SetEventChangeCoatNum(@NonNull View view)
    {
        /*
        Spinner spinner = view.findViewById(R.id.select_court_spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                int num = spinner.getSelectedItemPosition();
                m_coat = num+1;
                // コート数変更（初期化）
                if(m_players2==null)
                    makePar(true);
                else
                    makePar2(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        */
    }




    protected ListView listView;

    protected List<String> dataList;
    protected ArrayAdapter<String> adapter;

    /**
     * listアダプターの初期設定
     */
    public void setAdapters(){
        if(dataList==null) {
            dataList = new ArrayList<>();
        }
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
                    if(dataList.size()-1==position){
                        dataList.remove(dataList.size()-1);

                        // リスト追加
                        if(m_players2==null)
                            makePar(false,m_number);
                        else
                            makePar2(false);
                    } else {
                        openResDlg(position);
                    }
                }
            });
        }
    }

    Integer getIntegerValue(Object o) {
        if (o instanceof Double) {
            Double d = (Double)o;
            return d.intValue();
        }else if(o instanceof  Integer){
            return (Integer)o;
        }
        return 0;
    }

    /**
     * アイテムタップ時の処理
     */
    private  void openResDlg(int position){
        Bundle args = new Bundle();
        args.putInt(GAME_INDEX,position);
        Map<String,Object> match = m_MatchTable.get(position);
        String game_no = (String)match.get(GAME_NO);
        args.putString(GAME_NO,game_no);
        Integer i0 = getIntegerValue (match.get(PLAYER_0));
        Integer i1 = getIntegerValue (match.get(PLAYER_1));
        if(m_players2!=null) {
            HashMap<String,Object> p1 = m_players2.get(i0);
            HashMap<String,Object> p2 = m_players2.get(i1);
            String n1 = (String)p1.get(KEY_NAME);
            String n2 = (String)p2.get(KEY_NAME);
            String l = String.format("%s-%s",n1,n2);
            args.putString(PAR_0,l);
        }else{
            String l = String.format(Locale.US,"%d-%d",i0+1,i1+1);
            args.putString(PAR_0,l);
        }
        Integer i2 = getIntegerValue (match.get(PLAYER_2));
        Integer i3 = getIntegerValue (match.get(PLAYER_3));
        if(m_players2!=null) {
            HashMap<String,Object> p1 = m_players2.get(i2);
            HashMap<String,Object> p2 = m_players2.get(i3);
            String n1 = (String)p1.get(KEY_NAME);
            String n2 = (String)p2.get(KEY_NAME);
            String l = String.format("%s-%s",n1,n2);
            args.putString(PAR_1,l);
        }else{
            String l = String.format(Locale.US, "%d-%d",i2+1,i3+1);
            args.putString(PAR_1,l);
        }
        SetResultsDialogFragment dialogFragment = SetResultsDialogFragment.newInstance(this,REQUEST_SETRESUTLS);
        dialogFragment.setArguments(args);
        FragmentActivity activity = (FragmentActivity) getContext();
        dialogFragment.show(activity.getSupportFragmentManager(), getString(R.string.add_member));

    }

    protected  int m_match_num = 0; //試合数
    protected  int m_coat = 1;  // コート数
    protected int m_number = 4;  // メンバー数(無名用)
//    List<String> m_players;     // メンバー名
    protected Map<String,Map<String,Object>> m_his;
    protected Map<String,Integer> m_kumiawase;
//    protected List<Integer> m_Already;
    protected List<Map<String,Object>> m_MatchTable;

    public static  String GAME_NO = "game_no";
    public static String GAME_INDEX = "game_index";
    public static String PLAYER_0 = "player_0";
    public static String PLAYER_1 = "player_1";
    public static String PLAYER_2 = "player_2";
    public static String PLAYER_3 = "player_3";
    public static String PAR_0 = "par_0";
    public static String PAR_1 = "par_1";
    public static String KEY_MATCH_FIN = "match_fin";

    /**
     *
     */
    @SuppressLint("DefaultLocale")
    public void makePar(boolean clear,int number){
//        adapter.clear();
        String string = getString(R.string.action_index_label);
        m_change_button.setText(String.format(string,number));
        if(clear || m_MatchTable==null) {
            m_number = number;
            m_match_num = 0;
            m_his = new HashMap<>();
            m_kumiawase = new HashMap<>();
            m_MatchTable = new ArrayList<>();
            dataList.clear();
            // 対戦履歴保管用オブジェクト生成
            for(Integer i=0;i<m_number;i++){
                m_his.put(i.toString(),new HashMap<String,Object>());
            }
        }else{
            //dataList.remove(dataList.size()-1);
        }

        //
        int i=m_MatchTable.size()/m_coat;
        int max = i+4;
        for(;i<max;i++){
            //m_Already = new ArrayList<>();
            for( int c = 0 ; c < m_coat ; c++ ) {
                List<Integer> k = new ArrayList<>();
                m_match_num = getMemberId(m_match_num, k);
                m_match_num = getMemberId(m_match_num, k);
                m_match_num = getMemberId(m_match_num, k);
                m_match_num = getMemberId(m_match_num, k);
                kumiawase(k);
                String l;
                l = String.format("%d-%d vs %d-%d", k.get(0) + 1, k.get(1) + 1, k.get(2) + 1, k.get(3) + 1);
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

        dataList.add("< 組み合わせ追加... >");

        // ListView の更新
        adapter.notifyDataSetChanged();
    }
//////////////////////////////////////////////////////////////////////////////////////////
    ArrayList<HashMap<String,Object>> m_players2;
    ArrayList<Integer> m_targetlist;
    int m_playmin;

    void sectionInit(){
        Integer max = 0;
        Integer min = -1;
        m_targetlist = new ArrayList<>();
        for (int index = 0; index < m_players2.size(); index++) {
            HashMap<String, Object> player = m_players2.get(index); // メンバー情報
            boolean selection = (boolean) player.get(KEY_SELECTION);
            if (selection) {    // 選択されているメンバー
                Integer count = (Integer) player.get(KEY_PLAY_NUM); // 回数
                if(count>max){
                    max = count;
                }
                if(min==-1 || count < min){
                    min = count;
                }
                m_targetlist.add(index);
            }
        }
        Integer lim = m_coat*4;
        while(max!=min && m_targetlist.size()>lim){
            int targetcount = max;
            max=0;
            ArrayList<Integer> candidate = new ArrayList<>(); // 候補
            for (Integer c=0;c<m_targetlist.size();c++){
                Integer index = m_targetlist.get(c);
                HashMap<String, Object> player = m_players2.get(index); // メンバー情報
                Integer count = (Integer) player.get(KEY_PLAY_NUM); // 回数
                if(count==targetcount){
                    candidate.add(index);
                }else if(count>max){
                    max = count;
                }
            }
            if(candidate.size()==0){
                // error
                return;
            } else if(lim <= m_targetlist.size()-candidate.size()){
                // 全て外す
                for (Integer c = candidate.size()-1;c>=0;c--) {
                    Integer i = candidate.get(c);
                    m_targetlist.remove(i);//この場合要素iが削除されるみたい、
                }
            }else{
                int size = m_targetlist.size()-lim;
                for(int i=size;i>0;i--){
                    Random r = new Random();
                    int c = r.nextInt(candidate.size());
                    Integer index = candidate.get(c);
                    candidate.remove(c);
                    m_targetlist.remove(index); // この場合要素が削除されるみたい
                }
            }
        }
    }

    void findAndSet(List<Integer> match)
    {
        if(m_targetlist.size()<4){

            return;
        }
        int firstcount = m_targetlist.size();
        Random r = new Random();
        int i = r.nextInt(firstcount);
        Integer index = m_targetlist.get(i);
        m_targetlist.remove(i);

        match.add(index);
        HashMap<String,Object> player = m_players2.get(index);
        Integer level = (Integer)player.get(KEY_LEVEL);
        m_targetlist = SortData(m_targetlist,level);
        for(int ix=2;ix>=0;ix--){
            Integer index2 = m_targetlist.get(ix);
            match.add(index2);
            m_targetlist.remove(ix);
        }
        for(int ix=0;ix<4;ix++){
            Integer index2 = match.get(ix);
            HashMap<String,Object> p =  m_players2.get(index2);
            Integer num = (Integer)p.get(KEY_PLAY_NUM);
            num++;
            p.put(KEY_PLAY_NUM,num);
        }
        kumiawase2(match);
    }

    /// レベルの近い順に並べる？
    private ArrayList<Integer> SortData(ArrayList<Integer> mydata,final Integer level){
        Collections.sort(mydata/*並べ替えするオブジェクト*/, new Comparator<Integer>() {
            public int compare(Integer o1, Integer o2) {
                HashMap<String,Object> p1 = m_players2.get(o1);
                HashMap<String,Object> p2 = m_players2.get(o2);
                Integer n1 = (Integer) p1.get(KEY_PLAY_NUM);
                Integer n2 = (Integer) p2.get(KEY_PLAY_NUM);
                if(n1!=n2) return n1-n2;

                Integer l1= (Integer)p1.get(KEY_LEVEL);
                Integer l2= (Integer)p2.get(KEY_LEVEL);
                l1 = Math.abs(l1-level);
                l2 = Math.abs(l2-level);
                return l1-l2;
            }
        });
        return mydata;
    }

    static String KEY_NAME="name";
    static String KEY_PLAY_NUM="play_num";
    static String KEY_SELECTION="selection";
    static String KEY_LEVEL="level";

    /**
     * google spread から取り込んだ
     * @param players
     */
    public void makeFirstPar2(ArrayList<HashMap<String,String>> players,boolean googleSpread,boolean allclear) {
        if (players == null) return;

        m_change_button.setText(googleSpread?R.string.action_google:R.string.action_reg_member);

        boolean clear = true;
        if(m_players2!=null && m_MatchTable!=null && m_MatchTable.size()>0 && !allclear){
//            m_kumiawase.clear();
            dataList.remove(dataList.size()-1); //
            //dataList.clear(); todo　多分こっちで大丈夫

            for(int i=m_MatchTable.size()-1;i>=0;i--){
                Map<String,Object> match = m_MatchTable.get(i);
                Integer fin = (Integer)match.get(KEY_MATCH_FIN);
                if(fin==null){
                    m_MatchTable.remove(i);
                    dataList.remove(i);
                }
            }
            if(m_MatchTable.size()>0)
                clear = false;
        }
        if(clear) {
            m_players2 = new ArrayList<>();
            for (Integer index = 0; index < players.size(); index++) {
                HashMap<String, String> player = players.get(index);
                String selection = player.get(MainActivity.KEY_SELECTION);
                String name = player.get(MainActivity.KEY_NAME);
                String level = player.get(MainActivity.KEY_LEVEL);

                HashMap<String, Object> match = new HashMap<>();
                match.put(KEY_NAME, name);
                match.put(KEY_PLAY_NUM, 0);
                match.put(KEY_SELECTION, selection==null || selection.equals("1"));
                match.put(KEY_LEVEL, Integer.parseInt(level));
                m_players2.add(match);
            }
        }else{
            for (int index = 0; index < players.size(); index++) {
                HashMap<String,String> p0 = players.get(index);
                HashMap<String, Object> p1 = m_players2.get(index);
                String selection = p0.get(MainActivity.KEY_SELECTION);
                p1.put(KEY_SELECTION, selection == "1" ? true : false);
                p1.put(KEY_PLAY_NUM,0);
                //m_players2.set(index,p1);
            }
            for (Map<String,Object> match:m_MatchTable
                 ) {
                Integer[] index = new Integer[4];
                index[0] = (Integer)match.get(PLAYER_0);
                index[1] = (Integer)match.get(PLAYER_1);
                index[2] = (Integer)match.get(PLAYER_2);
                index[3] = (Integer)match.get(PLAYER_3);
                for(int i=0;i<4;i++) {
                    HashMap<String, Object> p0 = m_players2.get(index[i]);
                    Integer n = (Integer) p0.get(KEY_PLAY_NUM);
                    n++;
                    p0.put(KEY_PLAY_NUM, n);
                }
            }
        }
        makePar2(clear);
    }

    public  void makePar2(boolean clear)
    {
        if(clear){
            m_playmin = 0;
            m_kumiawase = new HashMap<>();
            m_MatchTable = new ArrayList<>();
            dataList.clear();
            for (HashMap<String,Object> player:m_players2
                 ) {
                player.put(KEY_PLAY_NUM,0);
            }
        }
        //
        int i=m_MatchTable.size()/m_coat;
        int max = i+4; // i+4;
        for(;i<max;i++){
            sectionInit();
            for( int c = 0 ; c < m_coat ; c++ ) {
                ArrayList<Integer> k = new ArrayList<>();
                findAndSet(k);
                if(k.size()==0){
                    String l="空き";
                    String game_no;
                    if (m_coat > 1)
                        game_no = String.format("[%d-%d]", i + 1, c + 1);
                    else
                        game_no = String.format("[%d]", i + 1);
                    l = String.format("%s %s", game_no, l);
                    dataList.add(l);
                    Map<String, Object> match = new HashMap<>();
                    m_MatchTable.add(match);
                }else {
                    String l;

                    String n0 = getLabel(k.get(0));
                    String n1 = getLabel(k.get(1));
                    String n2 = getLabel(k.get(2));
                    String n3 = getLabel(k.get(3));
                    l = String.format("%s-%s vs %s-%s", n0, n1, n2, n3);

                    String game_no;
                    if (m_coat > 1)
                        game_no = String.format("[%d-%d]", i + 1, c + 1);
                    else
                        game_no = String.format("[%d]", i + 1);
                    l = String.format("%s %s", game_no, l);
                    dataList.add(l);
                    Map<String, Object> match = new HashMap<>();
                    match.put(GAME_NO, game_no);
                    match.put(PLAYER_0, k.get(0));
                    match.put(PLAYER_1, k.get(1));
                    match.put(PLAYER_2, k.get(2));
                    match.put(PLAYER_3, k.get(3));
                    m_MatchTable.add(match);
                }
            }
        }

        dataList.add("< 組み合わせ追加... >");

        // ListView の更新
        adapter.notifyDataSetChanged();

        //
        //m_kumiawase = new HashMap<>();
        //m_MatchTable = new ArrayList<>();
        MainActivity activity = (MainActivity) getActivity();
        activity.PrefSave(MainActivity.PREF_MATCH_TABLE,m_MatchTable);
        activity.PrefSave(MainActivity.PREF_PLAYER_INFO,m_players2);

    }

    void updateList()
    {
        int max = m_MatchTable.size();
        int index=0;
        int i=0;
        for(;index<max;i++){
            for( int c = 0 ; c < m_coat ; c++ ) {
                Map<String,Object> match = m_MatchTable.get(index);
                if(match.size()==0){
                    String l="空き";
                    String game_no;
                    if (m_coat > 1)
                        game_no = String.format("[%d-%d]", i + 1, c + 1);
                    else
                        game_no = String.format("[%d]", i + 1);
                    l = String.format("%s %s", game_no, l);
                    dataList.add(l);
                }else{
                    Integer i0 = ParseMatchIndex(match.get(PLAYER_0));
                    Integer i1 = ParseMatchIndex(match.get(PLAYER_1));
                    Integer i2 = ParseMatchIndex(match.get(PLAYER_2));
                    Integer i3 = ParseMatchIndex(match.get(PLAYER_3));

                    String l;

                    String n0 = getLabel(i0);
                    String n1 = getLabel(i1);
                    String n2 = getLabel(i2);
                    String n3 = getLabel(i3);
                    l = String.format("%s-%s vs %s-%s", n0, n1, n2, n3);

                    String game_no;
                    if (m_coat > 1)
                        game_no = String.format("[%d-%d]", i + 1, c + 1);
                    else
                        game_no = String.format("[%d]", i + 1);
                    l = String.format("%s %s", game_no, l);
                    dataList.add(l);
                }
                index++;
                if(index>=max)
                    break;
            }
        }
        String clipdata="";
        for (String t:
             dataList) {
            clipdata=clipdata+t;
            clipdata=clipdata+System.getProperty("line.separator");
        }
        CmUtils.copyToClipboard(getActivity(),"",clipdata);
        dataList.add("< 組み合わせ追加... >");

        // ListView の更新
        adapter.notifyDataSetChanged();
    }

    Integer ParseMatchIndex(Object o){
        Double d = (Double)o;
        return d.intValue();
    }

    /**
    *
    */
    String getLabel(Integer index)
    {
        HashMap<String, Object> p0 = m_players2.get(index);
        String n0 = (String) p0.get(KEY_NAME);
        Integer c0 = (Integer) p0.get(KEY_PLAY_NUM);
        return String.format("%s%d",n0,c0);
    }



    /**
     * 試合数がlast_num以下のメンバーを探すしてセット
     * @param last_num 試合数
     * @param k 対象メンバーの出力先
     * @return 最小試合数
     */
    Integer getMemberId(int last_num,List<Integer> k){
        for(;;) {
            for (Integer i = 0; i < m_number; i++) {
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
     * 組んだ数が少ない組み合わせを探す
     * @param member 対象メンバー(4人)
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

    int comp(ArrayList<Integer> o1,ArrayList<Integer> o2){
        if(o1.get(1) < o2.get(1)) return 1;
        if(o2.get(1) < o1.get(1)) return -1;

        if(o1.get(2) < o2.get(2)) return 1;
        if(o2.get(2) < o1.get(2)) return -1;

        return 0;
    }

    void kumiawase2(List<Integer> member)
    {
        if(m_kumiawase==null){
            m_kumiawase = new HashMap<>();
        }
        Collections.sort(member);

        ArrayList<ArrayList<Integer>> xx = new ArrayList<>();


        Integer m01 = kumiawase_count(member,0,1);
        Integer m23 = kumiawase_count(member,2,3);

        Integer m02 = kumiawase_count(member,0,2);
        Integer m13 = kumiawase_count(member,1,3);

        Integer m03 = kumiawase_count(member,0,3);
        Integer m12 = kumiawase_count(member,1,2);

        int[][] x = new int[3][2];

        x[0][0]=m01+m23;
        x[1][0]=m02+m13;
        x[2][0]=m03+m12;

        HashMap<String,Object> p0 = m_players2.get(member.get(0));
        HashMap<String,Object> p1 = m_players2.get(member.get(1));
        HashMap<String,Object> p2 = m_players2.get(member.get(2));
        HashMap<String,Object> p3 = m_players2.get(member.get(3));
        Integer l0 = (Integer)p0.get(KEY_LEVEL);
        Integer l1 = (Integer)p1.get(KEY_LEVEL);
        Integer l2 = (Integer)p2.get(KEY_LEVEL);
        Integer l3 = (Integer)p3.get(KEY_LEVEL);
        x[0][1]=Math.abs ((l0+l1) - (l2+l3));
        x[1][1]=Math.abs ((l0+l2) - (l1+l3));
        x[2][1]=Math.abs ((l0+l3) - (l1+l2));
        for(Integer i=0;i<3;i++){
            ArrayList<Integer> y = new ArrayList<>();
            y.add(i);
            y.add(x[i][0]);
            y.add(x[i][1]);
            xx.add(y);
        }
        if(comp(xx.get(1),xx.get(2))<0){
            xx.remove(1);
        }
        if(comp(xx.get(0),xx.get(1))<0){
            xx.remove(0);
        }

        Integer r = xx.get(0).get(0);
        if(r==0){
            kumiawase_inc(member,0,1,m01+1);
            kumiawase_inc(member,2,3,m23+1);
        }else if(r==1){
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
     * 組み合わせ数を取得
     * @param member 対象メンバー(4人)
     * @param l0 メンバー1インデックス
     * @param l1 メンバー2インデックス
     * @return 組んだ数
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
     * 組み合わせの数を登録
     * @param member 対象メンバー(4人)
     * @param l0 メンバー1インデックス
     * @param l1 メンバー2インデックス
     * @param v 組んだ数
     */
    void kumiawase_inc(List<Integer> member, Integer l0,Integer l1,Integer v)
    {
        m_kumiawase.put(String.format(Locale.US, "%d-%d",member.get(l0),member.get(l1)),v);
    }

    public int REQUEST_SETRESUTLS=100;

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        if (requestCode == REQUEST_SETRESUTLS) {
            if (resultCode == Activity.RESULT_OK) {
                // positive_button 押下時の処理

                HashMap<String, Integer> hashMap = (HashMap<String, Integer>) data.getSerializableExtra("hashMap");
                int pos = hashMap.get(GAME_INDEX);
                int s0 = hashMap.get(PAR_0);
                int s1 = hashMap.get(PAR_1);
                String label = makeParLabel(pos);
                String l = String.format(Locale.US,"%s ( %d x %d )完",label,s0,s1);
                dataList.set(pos,l);
                adapter.notifyDataSetChanged();

                Map<String,Object> match = m_MatchTable.get(pos);
                match.put(KEY_MATCH_FIN,1);
            } else if (resultCode == DialogInterface.BUTTON_NEGATIVE) {
                // negative_button 押下時の処理
            }
        }
    }

    String makeParLabel(int position)
    {
        Map<String,Object> match = m_MatchTable.get(position);
        String game_no = (String)match.get(GAME_NO);
        Integer i0 = getIntegerValue (match.get(PLAYER_0));
        Integer i1 = getIntegerValue (match.get(PLAYER_1));
        String l0;
        if(m_players2!=null) {
            HashMap<String,Object> p1 = m_players2.get(i0);
            HashMap<String,Object> p2 = m_players2.get(i1);
            String n1 = (String)p1.get(KEY_NAME);
            String n2 = (String)p2.get(KEY_NAME);
            l0 = String.format("%s-%s",n1,n2);
        }else{
            l0 = String.format(Locale.US,"%d-%d",i0+1,i1+1);
        }
        Integer i2 = getIntegerValue (match.get(PLAYER_2));
        Integer i3 = getIntegerValue (match.get(PLAYER_3));
        String l1;
        if(m_players2!=null) {
            HashMap<String,Object> p1 = m_players2.get(i2);
            HashMap<String,Object> p2 = m_players2.get(i3);
            String n1 = (String)p1.get(KEY_NAME);
            String n2 = (String)p2.get(KEY_NAME);
            l1 = String.format("%s-%s",n1,n2);
        }else{
            l1 = String.format(Locale.US, "%d-%d",i2+1,i3+1);
        }
        String l = String.format("%s %s vs %s",game_no,l0,l1);
        return l;
    }


    public ArrayList<String>  getGameRes()
    {
        ArrayList<String> data = new ArrayList<>();
        for(int i=m_MatchTable.size()-1;i>=0;i--){
            Map<String,Object> match = m_MatchTable.get(i);
            Integer fin = (Integer)match.get(KEY_MATCH_FIN);
            if(fin!=null){
                String text = makeParLabel(i);
                data.add(text);

            }
        }

        return data;
    }


}
