package com.lp.flashremote.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;


import com.lp.flashremote.beans.PropertiesUtil;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


/**
 * Created by xiyou3g on 2017/9/18.
 */

public class WifiHostBiz {
    private static final String TAG="WifiHostBiz";
    private WifiManager wifiManager;

    public enum WIFI_AP_STATE{
        WIFI_AP_STATE_ENABLED,//能用状态
        WIFI_AP_STATE_FAILED  //失败
    }
    public WifiHostBiz(Context context){
        wifiManager=(WifiManager) context.getSystemService(context.WIFI_SERVICE);
    }

    public boolean isWifiApEnable(){
        return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
    }

    private WIFI_AP_STATE getWifiApState() {
        int temp;
        try {
            Method method=wifiManager.getClass().getMethod("getWifiApState");
            temp= (int) method.invoke(wifiManager);
            if (temp>10){
                temp=temp-10;
            }
            return WIFI_AP_STATE.class.getEnumConstants()[temp];

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            Log.e(TAG,"wifimanger反射异常");
            e.printStackTrace();
            return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
        }
        return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
    }


    public String setWifiAPEnable(boolean enable){
       Log.e(TAG, ":开启热点");
        String ipSerult;
        if (enable){
            wifiManager.setWifiEnabled(false);
        }else{
            wifiManager.setWifiEnabled(true);
        }
        try {
            Method method=wifiManager.getClass().getMethod("setWifiApEnabled",
                    WifiConfiguration.class, Boolean.TYPE);
            WifiConfiguration wifiConfig=new WifiConfiguration();


            wifiConfig.SSID= PropertiesUtil.WIFINAME; //wifi名称
            wifiConfig.preSharedKey=PropertiesUtil.WIFIPWD; //密码

            wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

            method.invoke(wifiManager, wifiConfig, enable);
            ipSerult=IpAddressUtil.intIP2StringIP(wifiManager.getDhcpInfo().serverAddress);
            return ipSerult;
        } catch (Exception e) {
            Log.e(TAG,"wifimanger反射设置异常"+e.getCause());
            e.printStackTrace();
        }
        return null;
    }
    public void closeWifiAp() {

        Log.e("closehot","wifiAp closing......");
            try {
                Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
                method.setAccessible(true);
                WifiConfiguration config = (WifiConfiguration) method.invoke(wifiManager);
                Method method2 = wifiManager.getClass().getMethod("setWifiApEnabled",
                        WifiConfiguration.class, boolean.class);

                method2.invoke(wifiManager, config, false);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }

    }

}
