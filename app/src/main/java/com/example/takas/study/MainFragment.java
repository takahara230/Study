package com.example.takas.study;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.CheckResult;
import android.support.annotation.ColorInt;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MainFragment extends Fragment {

    private  final static String KEY_NAME = "key_name";
    private final  static String KEY_BACKGROUND = "key_background_color";

    @CheckResult
    public static MainFragment createInstance(String name,@ColorInt int color){
        MainFragment fragment = new MainFragment();

        Bundle args = new Bundle();
        args.putString(KEY_NAME,name);
        args.putInt(KEY_BACKGROUND,color);

        fragment.setArguments(args);
        return fragment;
    }

    private String mName = "";
    private @ColorInt int mBackgroundColor= Color.TRANSPARENT;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if(args!=null){
            mName = args.getString(KEY_NAME);
            mBackgroundColor = args.getInt(KEY_BACKGROUND,Color.TRANSPARENT);
        }
    }

    private TextView mTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreateView(inflater,container,savedInstanceState);
        return inflater.inflate(R.layout.fragment_main,container,false);
    }

    @Override
    public void onViewCreated(View view,Bundle savedInstanceState){
        super.onViewCreated(view,savedInstanceState);
        mTextView = (TextView)view.findViewById(R.id.textView);
        view.findViewById(R.id.button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                mTextView.setText(mTextView.getText()+"!");
            }
        });

        view.setBackgroundColor(mBackgroundColor);
        mTextView.setText(mName);
    }
}
