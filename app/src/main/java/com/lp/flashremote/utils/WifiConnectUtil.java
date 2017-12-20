package com.lp.flashremote.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by xiyou3g on 2017/9/19.
 *
 * 自动连接WiFi工具类
 */

public class WifiConnectUtil {

    private WifiManager manager;

    public enum WifiCipherType
    {
        WIFICIPHER_WEP,WIFICIPHER_WPA, WIFICIPHER_NOPASS
    }
    public WifiConnectUtil(Context context){
        manager=(WifiManager) context.getSystemService(context.WIFI_SERVICE);
     }
    //打开wifi功能
    private boolean OpenWifi()
    {
        boolean bRet = true;
        if (!manager.isWifiEnabled())
        {
            bRet = manager.setWifiEnabled(true);
        }
        return bRet;
    }

    public boolean Connect(String SSID, String Password){

        if(!this.OpenWifi())
        {
            Log.e("error","打开wifi 失败");
            return false;
        }
        while(manager.getWifiState() == WifiManager.WIFI_STATE_ENABLING ){
            Thread.currentThread();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        WifiConfiguration wifiConfig = this.CreateWifiInfo(SSID, Password,WifiCipherType.WIFICIPHER_WPA);

        /*boolean disConnectedFlag = manager.disconnect();
        System.out.println("disConnectedFlag is "+disConnectedFlag);*/

        if(wifiConfig == null)
        {
            Log.e("error","wifiConfig == null");
            return false;
        }

        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if(tempConfig != null)
        {
            Log.e("111111","tempConfig"+tempConfig.networkId);
            manager.removeNetwork(tempConfig.networkId);
        }


        int netID = manager.addNetwork(wifiConfig);
        System.out.println("netID is "+netID);
        boolean bRet = manager.enableNetwork(netID, true);
        Log.e("bRet",bRet+"----------");
        return bRet;
    }

    //查看以前是否也配置过这个网络
    private WifiConfiguration IsExsits(String SSID)
    {
        List<WifiConfiguration> existingConfigs = manager.getConfiguredNetworks();
        if(existingConfigs!=null){
            for (WifiConfiguration existingConfig : existingConfigs)
            {
                if (existingConfig.SSID.equals("\""+SSID+"\""))
                {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    private WifiConfiguration CreateWifiInfo(String SSID, String Password,WifiCipherType Type){
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if(Type == WifiCipherType.WIFICIPHER_NOPASS)
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == WifiCipherType.WIFICIPHER_WEP)
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if(Type == WifiCipherType.WIFICIPHER_WPA)
        {
            config.preSharedKey = "\""+Password+"\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        else
        {
            return null;
        }
        return config;

    }

}
