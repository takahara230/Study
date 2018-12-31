package com.example.takas.study;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class CmUtils {
    public static void UserPrefSave(){



    }

    public static  <T> T UserPrefJsonGet(Activity activity, String key,Type typeOfT){
        SharedPreferences pref = activity.getSharedPreferences("pref", MODE_PRIVATE);
        Gson gson = new Gson();
        T data = gson.fromJson(pref.getString(key, ""),typeOfT);
        return data;
    }

    public static void UserPrefJsonPut(Activity activity,String key,Object d){

    }

    public static void copyToClipboard(Context context, String label, String text) {
        // copy to clipboard
        ClipboardManager clipboardManager =
                (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (null == clipboardManager) {
            return;
        }
        clipboardManager.setPrimaryClip(ClipData.newPlainText(label, text));
    }
}
