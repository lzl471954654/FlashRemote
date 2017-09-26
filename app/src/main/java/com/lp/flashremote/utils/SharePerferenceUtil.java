package com.lp.flashremote.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.lp.flashremote.beans.UserInfo;




public class SharePerferenceUtil {
    public static void saveUserInfo(Context context,String username, String password){
        SharedPreferences.Editor editor=context.getSharedPreferences("uData",
                Context.MODE_PRIVATE).edit();
        editor.putString("username",username);
        editor.putString("password",password);
        editor.apply();
    }

    public static void getUserInfo(Context context){
        SharedPreferences preferences=context.getSharedPreferences("uData",Context.MODE_PRIVATE);
        UserInfo.setUsername(preferences.getString("username",""));
        UserInfo.setPassword(preferences.getString("password",""));
    }
}
