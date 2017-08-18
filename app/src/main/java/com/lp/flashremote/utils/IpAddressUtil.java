package com.lp.flashremote.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by PUJW on 2017/8/18.
 * 获得当前手机端的ip地址
 */

public class IpAddressUtil {
    public static String getIPAddress(Context context){
        NetworkInfo info=((ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE))
                .getActiveNetworkInfo();
        if (info!=null && info.isConnected()){
            if (info.getType()==ConnectivityManager.TYPE_MOBILE){//移动网络
                try {
                    for (Enumeration<NetworkInterface> enumeration=NetworkInterface.getNetworkInterfaces();
                            enumeration.hasMoreElements();){
                        NetworkInterface intf=enumeration.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }
            }else if (info.getType()==ConnectivityManager.TYPE_WIFI){//wifi
                WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                String ipAddress = intIP2StringIP(wifiInfo.getIpAddress());//得到IPV4地址
                return ipAddress;
            }
        }else{
           // Toast.makeText(context,"请检查网络！",Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public static String intIP2StringIP(int ip) {
        return (ip & 0xFF) + "." +
                ((ip >> 8) & 0xFF) + "." +
                ((ip >> 16) & 0xFF) + "." +
                (ip >> 24 & 0xFF);
    }
}
