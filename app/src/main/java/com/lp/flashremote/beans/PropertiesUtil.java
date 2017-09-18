package com.lp.flashremote.beans;

import android.content.Context;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by xiyou3g on 2017/9/18.
 *
 */

public class PropertiesUtil {

    public static String WIFINAME;
    public static String WIFIPWD;

    public static void getProperties(Context context){
        Properties pp=new Properties();
        try {
            pp.load(context.getAssets().open("appConfig/wifiinfo"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WIFINAME=pp.getProperty("WIFINAME");
        WIFIPWD=pp.getProperty("WIFIPWD");
    }
}
