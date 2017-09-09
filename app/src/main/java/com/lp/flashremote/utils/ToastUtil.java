package com.lp.flashremote.utils;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {
    public static void toastText(Context context,String s){
        Toast.makeText(context,s,Toast.LENGTH_SHORT).show();
    }
}
