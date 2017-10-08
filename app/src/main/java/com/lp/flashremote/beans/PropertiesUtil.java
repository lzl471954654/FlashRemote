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

    public static String HELLOSERVER;
    public static String HELLOCLIENT;

    public static String CONNECTED_TO_USER;
    public static String CONNECTED_SUCCESS;

    public static String END_FLAG;
    public static String COMMAND;
    public static String FILE_LIST_FLAG;
    public static String FILE_READY;
    public static String FILE_DELETE;
    public static String SERVER_IP;
    public static String FILE_PATH;

    public static void getProperties(Context context){
        Properties pp=new Properties();
        try {
            pp.load(context.getAssets().open("appConfig/wifiinfo"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        WIFINAME=pp.getProperty("WIFINAME");
        WIFIPWD=pp.getProperty("WIFIPWD");

        HELLOSERVER=pp.getProperty("HELLOSERVER");
        HELLOCLIENT=pp.getProperty("HELLOCLIENT");

        CONNECTED_TO_USER=pp.getProperty("CONNECTED_TO_USER");
        CONNECTED_SUCCESS=pp.getProperty("CONNECTED_SUCCESS");
        END_FLAG=pp.getProperty("END_FLAG");
        COMMAND=pp.getProperty("COMMAND");
        FILE_LIST_FLAG=pp.getProperty("FILE_LIST_FLAG");
        FILE_READY=pp.getProperty("FILE_READY");
        FILE_DELETE=pp.getProperty("FILE_DELETE");
        FILE_PATH = pp.getProperty("FILE_PATH");
        SERVER_IP=pp.getProperty("SERVER_IP");

    }
}
